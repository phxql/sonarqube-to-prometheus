package de.mkammerer.sq2p.infrastructure.http;

import lombok.Value;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Value
public class BasicAuth {
  String username;
  String password;

  public String getHeaderValue() {
    String concat = username + ":" + password;

    return "Basic " + Base64.getEncoder().encodeToString(concat.getBytes(StandardCharsets.UTF_8));
  }

  public static BasicAuth ofToken(String token) {
    return new BasicAuth(token, "");
  }
}
