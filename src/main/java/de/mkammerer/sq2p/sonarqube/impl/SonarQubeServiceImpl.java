package de.mkammerer.sq2p.sonarqube.impl;

import de.mkammerer.sq2p.sonarqube.Measure;
import de.mkammerer.sq2p.sonarqube.Metric;
import de.mkammerer.sq2p.sonarqube.Project;
import de.mkammerer.sq2p.sonarqube.SonarQubeException;
import de.mkammerer.sq2p.sonarqube.SonarQubeService;
import de.mkammerer.sq2p.sonarqube.connector.SonarQubeConnector;
import de.mkammerer.sq2p.sonarqube.connector.impl.SonarQubeConnectorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@RequiredArgsConstructor
@Slf4j
public class SonarQubeServiceImpl implements SonarQubeService {
  private final SonarQubeConnector connector;

  @Override
  public Set<Project> fetchProjects() throws SonarQubeException {
    LOGGER.info("Fetching projects ...");

    Set<Project> projects;
    try {
      projects = connector.fetchProjects();
    } catch (SonarQubeConnectorException e) {
      throw new SonarQubeException("Failed to fetch projects", e);
    }

    LOGGER.info("Fetched {} projects", projects.size());

    return projects;
  }

  @Override
  public Set<Metric> fetchMetrics() throws SonarQubeException {
    LOGGER.info("Fetching metrics ...");

    Set<Metric> metrics;
    try {
      metrics = connector.fetchMetrics();
    } catch (SonarQubeConnectorException e) {
      throw new SonarQubeException("Failed to fetch metrics", e);
    }

    LOGGER.info("Fetched {} metrics", metrics.size());

    return metrics;
  }

  @Override
  public Set<Measure> fetchMeasure(Project project, Set<Metric> metrics) throws SonarQubeException {
    LOGGER.info("Fetching measures for project {} and {} metrics ...", project.getName(), metrics.size());

    Set<Measure> measures;
    try {
      measures = connector.fetchMeasures(project, metrics);
    } catch (SonarQubeConnectorException e) {
      throw new SonarQubeException("Failed to fetch measures", e);
    }

    LOGGER.info("Fetched {} measures", measures.size());

    return measures;
  }
}
