package de.mkammerer.sq2p.metric.impl;

import de.mkammerer.sq2p.metric.MetricService;
import de.mkammerer.sq2p.sonarqube.Measure;
import de.mkammerer.sq2p.sonarqube.MetricType;
import de.mkammerer.sq2p.util.AtomicDouble;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@RequiredArgsConstructor
public class MetricServiceImpl implements MetricService {
  /**
   * Contains already known metrics.
   */
  private final ConcurrentMap<MeasureKey, AtomicDouble> metrics = new ConcurrentHashMap<>();

  private final MeterRegistry meterRegistry;

  @Override
  public void updateMeasure(Measure measure) {
    AtomicDouble gauge = metrics.computeIfAbsent(MeasureKey.of(measure), ignore -> {
      // Gets called if the gauge is not already registered
      Tags tags = Tags.of(
        "project", measure.getProject().getId(),
        "metricType", measure.getMetric().getType().toString()
      );

      return meterRegistry.gauge(measure.getMetric().getId(), tags, new AtomicDouble(measure.getValue()));
    });

    assert gauge != null; // Can't happen, as our supplier never returns null
    // At this point, the gauge is either a new or an existing one and we can set a value
    gauge.set(measure.getValue());
  }

  @Value
  private static class MeasureKey {
    String projectId;
    String metricId;
    MetricType metricType;

    public static MeasureKey of(Measure measure) {
      return new MeasureKey(measure.getProject().getId(), measure.getMetric().getId(), measure.getMetric().getType());
    }
  }
}
