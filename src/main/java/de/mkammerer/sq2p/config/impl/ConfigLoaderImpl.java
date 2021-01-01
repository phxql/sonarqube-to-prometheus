package de.mkammerer.sq2p.config.impl;

import de.mkammerer.sq2p.config.Config;
import de.mkammerer.sq2p.config.ConfigLoader;

import java.io.InputStream;
import java.net.URI;
import java.time.Duration;

public class ConfigLoaderImpl implements ConfigLoader {
  @Override
  public Config load(InputStream stream) {
    // TODO: load from stream
    return getDefaults();
  }

  @Override
  public Config getDefaults() {
    return new Config(
      new Config.Server("0.0.0.0", 8080),
      new Config.SonarQube(URI.create("http://localhost:9000"), "3c877121f457abb0b9b8d3f57a8aca602b6d78aa", Duration.ofMinutes(5)),
      new Config.Prometheus("/metrics")
    );
  }
}
