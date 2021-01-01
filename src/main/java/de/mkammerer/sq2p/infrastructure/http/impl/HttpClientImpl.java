package de.mkammerer.sq2p.infrastructure.http.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mkammerer.sq2p.infrastructure.http.BasicAuth;
import de.mkammerer.sq2p.infrastructure.http.HttpClient;
import de.mkammerer.sq2p.infrastructure.http.HttpClientFactory;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class HttpClientImpl implements HttpClient {
  private static final String USER_AGENT = "sonarqube-to-prometheus";

  private final java.net.http.HttpClient httpClient;
  private final ObjectMapper objectMapper;

  @Override
  public <T> T get(@Nullable BasicAuth basicAuth, URI url, Class<T> responseClass) throws IOException {
    var request = HttpRequest.newBuilder(url)
      .GET()
      .header("Accept", "application/json")
      .header("User-Agent", USER_AGENT)
      .timeout(HttpClientFactory.TIMEOUT);

    if (basicAuth != null) {
      request.header("Authorization", basicAuth.getHeaderValue());
    }

    HttpResponse<String> response = send(request.build());
    checkResponse(response);

    return objectMapper.readValue(response.body(), responseClass);
  }

  @Override
  public URI combineUrl(String base, String path) {
    String baseWithoutSlash = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
    String pathWithSlash = path.startsWith("/") ? path : "/" + path;

    return URI.create(baseWithoutSlash + pathWithSlash);
  }

  @Override
  public String encodeQueryParameter(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }

  private void checkResponse(HttpResponse<String> response) throws IOException {
    if (response.statusCode() / 100 != 2) {
      throw new IOException(String.format("Failed to execute request. Expected status 2xx, got %d", response.statusCode()));
    }
  }

  private HttpResponse<String> send(HttpRequest request) throws IOException {
    try {
      return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("Got interrupted while sending request", e);
    }
  }
}
