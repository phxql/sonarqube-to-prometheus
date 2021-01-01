package de.mkammerer.sq2p.config;

import java.io.InputStream;

public interface ConfigLoader {
  Config load(InputStream stream);

  Config getDefaults();
}
