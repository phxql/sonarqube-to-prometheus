package de.mkammerer.sq2p.config.impl;

import com.moandjiezana.toml.Toml;
import de.mkammerer.sq2p.config.Config;
import de.mkammerer.sq2p.config.ConfigException;
import de.mkammerer.sq2p.config.ConfigLoader;

import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Set;

public class ConfigLoaderImpl implements ConfigLoader {
  @Override
  public Config load(InputStream stream) throws ConfigException {
    Toml config = new Toml().read(stream);

    String sonarQubeToken = config.getString("sonarqube.token");
    if (sonarQubeToken == null) {
      throw new ConfigException("'sonarqube.token' not set!");
    }

    return new Config(
      new Config.Server(config.getString("server.hostname", "0.0.0.0"), Math.toIntExact(config.getLong("server.port", 8080L)), Math.toIntExact(config.getLong("server.min_threads", 1L)), Math.toIntExact(config.getLong("server.max_threads", 8L))),
      new Config.SonarQube(URI.create(config.getString("sonarqube.url", "http://localhost:9000/")), sonarQubeToken, Duration.parse(config.getString("sonarqube.scrape_interval", "PT1H"))),
      new Config.Prometheus(config.getString("prometheus.metrics_path", "/metrics")),
      new Config.Projects(Set.copyOf(config.getList("projects.include", List.of())), Set.copyOf(config.getList("projects.exclude", List.of()))),
      new Config.Metrics(Set.copyOf(config.getList("metrics.include", List.of())), Set.copyOf(config.getList("metrics.exclude", List.of()))),
      new Config.Branches(Set.copyOf(config.getList("branches.include", List.of())), Set.copyOf(config.getList("branches.exclude", List.of())))
    );
  }
}
