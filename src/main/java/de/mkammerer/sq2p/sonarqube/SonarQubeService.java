package de.mkammerer.sq2p.sonarqube;

import java.util.Set;

public interface SonarQubeService {
  Set<Project> fetchProjects() throws SonarQubeException;

  Set<Metric> fetchMetrics() throws SonarQubeException;

  Set<Measure> fetchMeasure(Project project, Set<Metric> metrics) throws SonarQubeException;
}
