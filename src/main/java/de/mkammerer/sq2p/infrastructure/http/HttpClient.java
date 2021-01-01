package de.mkammerer.sq2p.infrastructure.http;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.net.URI;

public interface HttpClient {
  <T> T get(@Nullable BasicAuth basicAuth, URI url, Class<T> responseClass) throws IOException;

  URI combineUrl(String base, String path);

  String encodeQueryParameter(String value);
}
