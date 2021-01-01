package de.mkammerer.sq2p.sonarqube;

import lombok.Value;

@Value
public class Measure {
  Project project;
  Metric metric;
  double value;
}
