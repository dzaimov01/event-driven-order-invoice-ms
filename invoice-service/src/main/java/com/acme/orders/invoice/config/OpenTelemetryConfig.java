package com.acme.orders.invoice.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTelemetryConfig {

  @Bean
  public Tracer tracer() {
    OpenTelemetry openTelemetry = OpenTelemetry.noop();
    return openTelemetry.getTracer("invoice-service");
  }
}
