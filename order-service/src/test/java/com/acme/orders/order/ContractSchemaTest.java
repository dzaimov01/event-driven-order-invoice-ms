package com.acme.orders.order;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ContractSchemaTest {
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void orderEventMatchesSchema() throws Exception {
    try (InputStream schemaStream = getClass().getClassLoader().getResourceAsStream("v1/order-event.json")) {
      JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
      JsonSchema schema = factory.getSchema(schemaStream);

      String payload = """
          {
            \"eventId\": \"d4a0f7f6-0fb0-4f66-9d2b-88b8d77f7f70\",
            \"type\": \"OrderConfirmed\",
            \"orderId\": \"2a0d36b7-8c6f-4a8b-ae3f-3ad2f21ce6a6\",
            \"customerId\": \"4f5085f2-4b8d-4b54-9a8a-0a1c916f5b13\",
            \"status\": \"CONFIRMED\",
            \"total\": \"25.00\",
            \"currency\": \"USD\",
            \"occurredAt\": \"2024-05-10T12:00:00Z\",
            \"version\": \"v1\",
            \"lines\": [
              {
                \"productId\": \"f8df4d0a-1299-4ef5-a8f2-1d0f3cbb55c6\",
                \"quantity\": 2,
                \"unitPrice\": \"10.00\",
                \"currency\": \"USD\"
              }
            ]
          }
          """;

      JsonNode node = objectMapper.readTree(payload);
      Set<ValidationMessage> errors = schema.validate(node);
      assertTrue(errors.isEmpty(), errors.toString());
    }
  }
}
