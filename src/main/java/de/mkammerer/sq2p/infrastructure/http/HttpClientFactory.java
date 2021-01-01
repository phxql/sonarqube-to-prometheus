package de.mkammerer.sq2p.infrastructure.http;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.mkammerer.sq2p.infrastructure.http.impl.HttpClientImpl;

import java.time.Duration;

public final class HttpClientFactory {
  public static final Duration TIMEOUT = Duration.ofSeconds(5);
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

  private HttpClientFactory() {
    // Static class
  }

  public static HttpClient create() {
    var httpClient = java.net.http.HttpClient.newBuilder()
      .connectTimeout(TIMEOUT)
      .build();

    return new HttpClientImpl(httpClient, OBJECT_MAPPER);
  }
}
