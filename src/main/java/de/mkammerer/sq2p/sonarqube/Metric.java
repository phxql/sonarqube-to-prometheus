package de.mkammerer.sq2p.sonarqube;

import lombok.Value;

@Value
public class Metric {
  String id;
  MetricType type;
}
