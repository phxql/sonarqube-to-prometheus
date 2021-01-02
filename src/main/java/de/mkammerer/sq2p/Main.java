package de.mkammerer.sq2p;

import de.mkammerer.sq2p.config.Config;
import de.mkammerer.sq2p.config.ConfigException;
import de.mkammerer.sq2p.config.ConfigLoader;
import de.mkammerer.sq2p.config.impl.ConfigLoaderImpl;
import de.mkammerer.sq2p.infrastructure.http.HttpClient;
import de.mkammerer.sq2p.infrastructure.http.HttpClientFactory;
import de.mkammerer.sq2p.infrastructure.thread.NamedThreadFactory;
import de.mkammerer.sq2p.metric.MetricService;
import de.mkammerer.sq2p.metric.impl.MetricServiceImpl;
import de.mkammerer.sq2p.schedule.Scheduler;
import de.mkammerer.sq2p.server.ServerHandler;
import de.mkammerer.sq2p.sonarqube.SonarQubeService;
import de.mkammerer.sq2p.sonarqube.connector.impl.SonarQubeConnectorImpl;
import de.mkammerer.sq2p.sonarqube.impl.SonarQubeServiceImpl;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Main {
  public static void main(String[] args) {
    int exitCode = 0;
    LOGGER.info("Started");
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("scheduler"));
    try {
      new Main().run(args, scheduler);
    } catch (ConfigException e) {
      LOGGER.error("Error while loading config: {}", e.getMessage());
      exitCode = 2;
    } catch (Exception e) {
      LOGGER.error("Unhandled exception, please report this as an issue", e);
      exitCode = 1;
    } finally {
      scheduler.shutdownNow();
      LOGGER.info("Stopped");
    }

    System.exit(exitCode);
  }

  private void run(String[] args, ScheduledExecutorService executorService) throws Exception {
    Config config = loadConfig();
    LOGGER.info("Using config: {}", config);

    SonarQubeService sonarQube = getSonarQubeService(config);
    PrometheusMeterRegistry meterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    MetricService metricService = getMetricService(meterRegistry, config);
    setupScheduler(executorService, config, sonarQube, metricService);
    startServer(config, meterRegistry);
  }

  private void setupScheduler(ScheduledExecutorService executorService, Config config, SonarQubeService sonarQube, MetricService metricService) {
    Scheduler scheduler = new Scheduler(sonarQube, metricService);

    executorService.scheduleWithFixedDelay(scheduler::run, 0, config.getSonarQube().getScrapeInterval().toSeconds(), TimeUnit.SECONDS);
  }

  private MetricService getMetricService(MeterRegistry meterRegistry, Config config) {
    // Use 3 times the SonarQube scrape interval for metric expiry
    return new MetricServiceImpl(meterRegistry, config.getSonarQube().getScrapeInterval().multipliedBy(3));
  }

  private SonarQubeService getSonarQubeService(Config config) {
    HttpClient httpClient = HttpClientFactory.create();

    return new SonarQubeServiceImpl(new SonarQubeConnectorImpl(httpClient, config.getSonarQube()));
  }

  private void startServer(Config config, PrometheusMeterRegistry meterRegistry) throws Exception {
    String hostname = config.getServer().getHostname();
    int port = config.getServer().getPort();
    LOGGER.debug("Starting server on {}:{}...", hostname, port);

    Server server = new Server(new QueuedThreadPool(config.getServer().getMaxThreads(), config.getServer().getMinThreads()));
    ServerConnector connector = new ServerConnector(server);
    connector.setHost(hostname);
    connector.setPort(port);
    server.addConnector(connector);
    server.setHandler(new ServerHandler(config.getPrometheus(), meterRegistry));

    server.start();
    LOGGER.info("Server running on {}:{}", hostname, port);

    server.join();
  }

  private Config loadConfig() throws IOException, ConfigException {
    ConfigLoader configLoader = new ConfigLoaderImpl();

    Path configFile = Paths.get("config.toml").toAbsolutePath();
    if (!Files.exists(configFile)) {
      throw new ConfigException(String.format("Config file '%s' doesn't exist!", configFile));
    }

    LOGGER.debug("Config file '{}' found, loading it", configFile);
    try (InputStream stream = Files.newInputStream(configFile)) {
      return configLoader.load(stream);
    }
  }
}
