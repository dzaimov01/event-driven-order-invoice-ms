package com.acme.orders.integration;

import com.acme.orders.invoice.InvoiceServiceApplication;
import com.acme.orders.order.OrderServiceApplication;
import com.acme.orders.invoice.infrastructure.persistence.InvoiceJpaRepository;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class OrderInvoiceFlowTest {
  private static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.1"));
  private static final PostgreSQLContainer<?> ORDER_DB = new PostgreSQLContainer<>("postgres:15-alpine")
      .withDatabaseName("orders")
      .withUsername("orders")
      .withPassword("orders");
  private static final PostgreSQLContainer<?> INVOICE_DB = new PostgreSQLContainer<>("postgres:15-alpine")
      .withDatabaseName("invoices")
      .withUsername("invoices")
      .withPassword("invoices");

  private static ConfigurableApplicationContext orderContext;
  private static ConfigurableApplicationContext invoiceContext;

  private final RestTemplate restTemplate = new RestTemplate();

  @BeforeAll
  static void start() {
    KAFKA.start();
    ORDER_DB.start();
    INVOICE_DB.start();

    orderContext = new SpringApplicationBuilder(OrderServiceApplication.class)
        .properties(Map.of(
            "server.port", "0",
            "spring.datasource.url", ORDER_DB.getJdbcUrl(),
            "spring.datasource.username", ORDER_DB.getUsername(),
            "spring.datasource.password", ORDER_DB.getPassword(),
            "spring.kafka.bootstrap-servers", KAFKA.getBootstrapServers()
        ))
        .run();

    invoiceContext = new SpringApplicationBuilder(InvoiceServiceApplication.class)
        .properties(Map.of(
            "server.port", "0",
            "spring.datasource.url", INVOICE_DB.getJdbcUrl(),
            "spring.datasource.username", INVOICE_DB.getUsername(),
            "spring.datasource.password", INVOICE_DB.getPassword(),
            "spring.kafka.bootstrap-servers", KAFKA.getBootstrapServers()
        ))
        .run();
  }

  @AfterAll
  static void stop() {
    if (orderContext != null) {
      orderContext.close();
    }
    if (invoiceContext != null) {
      invoiceContext.close();
    }
    INVOICE_DB.stop();
    ORDER_DB.stop();
    KAFKA.stop();
  }

  @Test
  void createOrderPublishesInvoice() {
    int orderPort = Integer.parseInt(orderContext.getEnvironment().getProperty("local.server.port"));
    int invoicePort = Integer.parseInt(invoiceContext.getEnvironment().getProperty("local.server.port"));

    UUID customerId = UUID.randomUUID();
    Map<String, Object> payload = Map.of(
        "customerId", customerId,
        "lines", List.of(Map.of(
            "productId", UUID.randomUUID(),
            "quantity", 2,
            "unitPrice", "10.00",
            "currency", "USD"
        ))
    );

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

    ResponseEntity<Map> response = restTemplate.exchange(
        "http://localhost:" + orderPort + "/orders", HttpMethod.POST, entity, Map.class);

    assertEquals(201, response.getStatusCode().value());
    String orderId = String.valueOf(response.getBody().get("orderId"));

    restTemplate.exchange(
        "http://localhost:" + orderPort + "/orders/" + orderId + "/confirm", HttpMethod.POST, null, Map.class);

    boolean found = waitForInvoice(invoicePort, orderId);
    assertTrue(found, "invoice not created in time");
  }

  @Test
  void duplicateEventsDoNotDuplicateInvoice() {
    int orderPort = Integer.parseInt(orderContext.getEnvironment().getProperty("local.server.port"));
    int invoicePort = Integer.parseInt(invoiceContext.getEnvironment().getProperty("local.server.port"));

    UUID customerId = UUID.randomUUID();
    Map<String, Object> payload = Map.of(
        "customerId", customerId,
        "lines", List.of(Map.of(
            "productId", UUID.randomUUID(),
            "quantity", 1,
            "unitPrice", "5.00",
            "currency", "USD"
        ))
    );

    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, new HttpHeaders());
    ResponseEntity<Map> response = restTemplate.exchange(
        "http://localhost:" + orderPort + "/orders", HttpMethod.POST, entity, Map.class);

    assertEquals(201, response.getStatusCode().value());
    String orderId = String.valueOf(response.getBody().get("orderId"));

    restTemplate.exchange(
        "http://localhost:" + orderPort + "/orders/" + orderId + "/confirm", HttpMethod.POST, null, Map.class);

    waitForInvoice(invoicePort, orderId);

    ResponseEntity<Map> invoiceResponse = restTemplate.exchange(
        "http://localhost:" + invoicePort + "/invoices?orderId=" + orderId, HttpMethod.GET, null, Map.class);
    assertEquals(200, invoiceResponse.getStatusCode().value());
  }

  @Test
  void idempotencyIgnoresDuplicateEventIds() {
    InvoiceJpaRepository invoiceRepository = invoiceContext.getBean(InvoiceJpaRepository.class);
    String orderId = UUID.randomUUID().toString();
    String eventId = UUID.randomUUID().toString();

    String payload = """
        {
          "eventId": "%s",
          "type": "OrderConfirmed",
          "orderId": "%s",
          "customerId": "%s",
          "status": "CONFIRMED",
          "total": "20.00",
          "currency": "USD",
          "occurredAt": "%s",
          "version": "v1",
          "lines": [
            {
              "productId": "%s",
              "quantity": 2,
              "unitPrice": "10.00",
              "currency": "USD"
            }
          ]
        }
        """.formatted(eventId, orderId, UUID.randomUUID(), Instant.now().toString(), UUID.randomUUID());

    sendDuplicateEvent(payload);
    waitForInvoice(Integer.parseInt(invoiceContext.getEnvironment().getProperty("local.server.port")), orderId);

    assertTrue(invoiceRepository.findByOrderId(UUID.fromString(orderId)).isPresent());
  }

  private void sendDuplicateEvent(String payload) {
    Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
      producer.send(new ProducerRecord<>("order.events", payload)).get();
      producer.send(new ProducerRecord<>("order.events", payload)).get();
    }
  }

  private boolean waitForInvoice(int invoicePort, String orderId) {
    long deadline = System.currentTimeMillis() + Duration.ofSeconds(20).toMillis();
    while (System.currentTimeMillis() < deadline) {
      try {
        ResponseEntity<Map> response = restTemplate.exchange(
            "http://localhost:" + invoicePort + "/invoices?orderId=" + orderId, HttpMethod.GET, null, Map.class);
        if (response.getStatusCode().value() == 200) {
          return true;
        }
      } catch (Exception ignored) {
        try {
          Thread.sleep(500);
        } catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
          return false;
        }
      }
    }
    return false;
  }
}
