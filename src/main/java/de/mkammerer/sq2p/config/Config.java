package de.mkammerer.sq2p.config;

import lombok.ToString;
import lombok.Value;

import java.net.URI;
import java.time.Duration;

@Value
public class Config {
  Server server;
  SonarQube sonarQube;
  Prometheus prometheus;

  @Value
  public static class SonarQube {
    URI url;
    @ToString.Exclude
    String token;
    Duration scrapeInterval;
  }

  @Value
  public static class Server {
    String hostname;
    int port;
    int minThreads;
    int maxThreads;
  }

  @Value
  public static class Prometheus {
    String metricsPath;
  }
}
