package de.mkammerer.sq2p.metric.impl;

import de.mkammerer.sq2p.metric.MetricService;
import de.mkammerer.sq2p.sonarqube.Measure;
import de.mkammerer.sq2p.sonarqube.MetricType;
import de.mkammerer.sq2p.util.AtomicDouble;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.search.MeterNotFoundException;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MetricServiceImpl implements MetricService {
  /**
   * Contains already known metrics.
   * <p>
   * If a metric expires (because it was removed from SonarQube), the metric will be unregistered from the meterRegistry.
   */
  private final ExpiringMap<MeasureKey, AtomicDouble> metrics;
  private final MeterRegistry meterRegistry;

  public MetricServiceImpl(MeterRegistry meterRegistry, Duration maxMetricLifetime) {
    this.meterRegistry = meterRegistry;
    this.metrics = ExpiringMap.builder()
      .expiration(maxMetricLifetime.toSeconds(), TimeUnit.SECONDS)
      .expirationPolicy(ExpirationPolicy.ACCESSED)
      .expirationListener(this::onMetricExpire)
      .build();
    LOGGER.debug("Metrics expire after {}", maxMetricLifetime);
  }

  @Override
  public void updateMeasure(Measure measure) {
    AtomicDouble gauge = metrics.computeIfAbsent(MeasureKey.of(measure), key ->
      // Gets called if the gauge is not already registered
      meterRegistry.gauge(key.getMetricId(), key.getTags(), new AtomicDouble(measure.getValue()))
    );

    assert gauge != null; // Can't happen, as our supplier never returns null
    // At this point, the gauge is either a new or an existing one and we can set a value
    gauge.set(measure.getValue());
  }

  private void onMetricExpire(MeasureKey key, AtomicDouble value) {
    try {
      Meter meter = meterRegistry.get(key.getMetricId()).tags(key.getTags()).meter();
      meterRegistry.remove(meter);
      LOGGER.debug("Removed expired metric {}", meter.getId());
    } catch (MeterNotFoundException e) {
      LOGGER.warn("Failed to remove non-existent meter", e);
    }
  }

  @Value
  private static class MeasureKey {
    String projectId;
    String branchId;
    String metricId;
    MetricType metricType;

    public static MeasureKey of(Measure measure) {
      return new MeasureKey(measure.getProject().getId(), measure.getBranch().getId(), measure.getMetric().getId(), measure.getMetric().getType());
    }

    public Tags getTags() {
      return Tags.of(
        "project", projectId,
        "branch", branchId,
        "metricType", metricType.toString()
      );
    }
  }
}
