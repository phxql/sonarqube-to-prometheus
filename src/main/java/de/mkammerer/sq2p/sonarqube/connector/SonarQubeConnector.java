package de.mkammerer.sq2p.sonarqube.connector;

import de.mkammerer.sq2p.sonarqube.Measure;
import de.mkammerer.sq2p.sonarqube.Metric;
import de.mkammerer.sq2p.sonarqube.Project;
import de.mkammerer.sq2p.sonarqube.connector.impl.SonarQubeConnectorException;

import java.util.Set;

public interface SonarQubeConnector {
  Set<Project> fetchProjects() throws SonarQubeConnectorException;

  Set<Metric> fetchMetrics() throws SonarQubeConnectorException;

  Set<Measure> fetchMeasures(Project project, Set<Metric> metrics) throws SonarQubeConnectorException;
}
