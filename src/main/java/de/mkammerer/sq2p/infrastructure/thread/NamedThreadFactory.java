package de.mkammerer.sq2p.infrastructure.thread;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.ThreadFactory;

@RequiredArgsConstructor
public class NamedThreadFactory implements ThreadFactory {
  private final String name;

  @Override
  public Thread newThread(Runnable r) {
    Thread thread = new Thread(r, name);

    if (!thread.isDaemon()) {
      thread.setDaemon(true);
    }

    return thread;
  }
}
