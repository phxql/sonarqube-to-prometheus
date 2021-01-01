package de.mkammerer.sq2p.server;

import de.mkammerer.sq2p.config.Config;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Slf4j
public class ServerHandler extends AbstractHandler {
  private final Config.Prometheus config;
  private final PrometheusMeterRegistry meterRegistry;

  @Override
  public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (target.equals(config.getMetricsPath())) {
      baseRequest.setHandled(true);
      handleMetrics(response);
    }
  }

  private void handleMetrics(HttpServletResponse response) throws IOException {
    response.setStatus(200);
    response.setHeader("Content-Type", "text/plain; version=0.0.4");

    try (OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8)) {
      // See https://prometheus.io/docs/instrumenting/exposition_formats/#text-based-format
      meterRegistry.scrape(writer);
    }
  }
}
