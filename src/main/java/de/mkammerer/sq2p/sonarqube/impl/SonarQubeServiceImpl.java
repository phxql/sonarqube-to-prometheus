package de.mkammerer.sq2p.sonarqube.impl;

import de.mkammerer.sq2p.sonarqube.Branch;
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
    LOGGER.debug("Fetching projects ...");

    Set<Project> projects;
    try {
      projects = connector.fetchProjects();
    } catch (SonarQubeConnectorException e) {
      throw new SonarQubeException("Failed to fetch projects", e);
    }

    LOGGER.debug("Fetched {} projects", projects.size());

    return projects;
  }

  @Override
  public Set<Metric> fetchMetrics() throws SonarQubeException {
    LOGGER.debug("Fetching metrics ...");

    Set<Metric> metrics;
    try {
      metrics = connector.fetchMetrics();
    } catch (SonarQubeConnectorException e) {
      throw new SonarQubeException("Failed to fetch metrics", e);
    }

    LOGGER.debug("Fetched {} metrics", metrics.size());

    return metrics;
  }

  @Override
  public Set<Measure> fetchMeasure(Project project, Branch branch, Set<Metric> metrics) throws SonarQubeException {
    LOGGER.debug("Fetching measures for project '{}', branch '{}' and {} metrics ...", project.getId(), branch.getId(), metrics.size());

    Set<Measure> measures;
    try {
      measures = connector.fetchMeasures(project, branch, metrics);
    } catch (SonarQubeConnectorException e) {
      throw new SonarQubeException(String.format("Failed to fetch measures for project '%s' and metrics '%s'", project.getId(), metrics), e);
    }

    LOGGER.debug("Fetched {} measures", measures.size());

    return measures;
  }

  @Override
  public Set<Branch> fetchBranches(Project project) throws SonarQubeException {
    LOGGER.debug("Fetching branches for project {} ...", project.getId());

    Set<Branch> branches;
    try {
      branches = connector.fetchBranches(project);
    } catch (SonarQubeConnectorException e) {
      throw new SonarQubeException(String.format("Failed to fetch branches for project '%s'", project.getId()), e);
    }

    LOGGER.debug("Fetched {} branches", branches.size());

    return branches;
  }
}
