package de.mkammerer.sq2p.sonarqube;

import lombok.Value;

@Value
public class Measure {
  Project project;
  Branch branch;
  Metric metric;
  double value;
}
