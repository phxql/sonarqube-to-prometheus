package de.mkammerer.sq2p.metric;

import de.mkammerer.sq2p.sonarqube.Measure;

public interface MetricService {
  void updateMeasure(Measure measure);
}
