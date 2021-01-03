package de.mkammerer.sq2p.sonarqube.connector.impl;

import com.fasterxml.jackson.databind.JsonNode;
import de.mkammerer.sq2p.config.Config;
import de.mkammerer.sq2p.infrastructure.http.BasicAuth;
import de.mkammerer.sq2p.infrastructure.http.HttpClient;
import de.mkammerer.sq2p.sonarqube.Branch;
import de.mkammerer.sq2p.sonarqube.Measure;
import de.mkammerer.sq2p.sonarqube.Metric;
import de.mkammerer.sq2p.sonarqube.MetricType;
import de.mkammerer.sq2p.sonarqube.Project;
import de.mkammerer.sq2p.sonarqube.connector.SonarQubeConnector;
import de.mkammerer.sq2p.sonarqube.connector.impl.dto.BranchesDto;
import de.mkammerer.sq2p.sonarqube.connector.impl.dto.MeasuresDto;
import de.mkammerer.sq2p.sonarqube.connector.impl.dto.MetricsDto;
import de.mkammerer.sq2p.sonarqube.connector.impl.dto.ProjectsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class SonarQubeConnectorImpl implements SonarQubeConnector {
  private final HttpClient httpClient;
  private final Config.SonarQube config;

  @Override
  public Set<Project> fetchProjects() throws SonarQubeConnectorException {
    // TODO: Support more than 500 projects
    URI url = httpClient.combineUrl(config.getUrl().toString(), "/api/components/search?qualifiers=TRK&ps=500");

    ProjectsDto projects;
    try {
      projects = httpClient.get(BasicAuth.ofToken(config.getToken()), url, ProjectsDto.class);
    } catch (IOException e) {
      throw new SonarQubeConnectorException(String.format("Failed to fetch projects from %s", url), e);
    }

    return projects.getComponents().stream()
      .map(ProjectsDto.ComponentsDto::toProject)
      .collect(Collectors.toUnmodifiableSet());
  }

  @Override
  public Set<Metric> fetchMetrics() throws SonarQubeConnectorException {
    // TODO: Support more than 500 metrics
    URI url = httpClient.combineUrl(config.getUrl().toString(), "/api/metrics/search?ps=500");

    MetricsDto metrics;
    try {
      metrics = httpClient.get(BasicAuth.ofToken(config.getToken()), url, MetricsDto.class);
    } catch (IOException e) {
      throw new SonarQubeConnectorException(String.format("Failed to fetch metrics from %s", url), e);
    }

    return metrics.getMetrics().stream()
      .filter(m -> !m.isHidden())
      .filter(m -> MetricType.isSupported(m.getType()))
      .map(MetricsDto.MetricDto::toMetric)
      .collect(Collectors.toUnmodifiableSet());
  }

  @Override
  public Set<Branch> fetchBranches(Project project) throws SonarQubeConnectorException {
    URI url = httpClient.combineUrl(config.getUrl().toString(), String.format(
      "/api/project_branches/list?project=%s",
      httpClient.encodeQueryParameter(project.getId())
    ));

    BranchesDto branches;
    try {
      branches = httpClient.get(BasicAuth.ofToken(config.getToken()), url, BranchesDto.class);
    } catch (IOException e) {
      throw new SonarQubeConnectorException(String.format("Failed to fetch branches from %s", url), e);
    }

    return branches.getBranches().stream()
      .map(BranchesDto.BranchDto::toBranch)
      .collect(Collectors.toUnmodifiableSet());
  }

  @Override
  public Set<Measure> fetchMeasures(Project project, Branch branch, Set<Metric> metrics) throws SonarQubeConnectorException {
    String metricsValue = metrics.stream().map(Metric::getId).collect(Collectors.joining(","));

    URI url = httpClient.combineUrl(config.getUrl().toString(), String.format(
      "/api/measures/component?component=%s&branch=%s&metricKeys=%s",
      httpClient.encodeQueryParameter(project.getId()),
      httpClient.encodeQueryParameter(branch.getId()),
      httpClient.encodeQueryParameter(metricsValue)
    ));

    MeasuresDto measures;
    try {
      measures = httpClient.get(BasicAuth.ofToken(config.getToken()), url, MeasuresDto.class);
    } catch (IOException e) {
      throw new SonarQubeConnectorException(String.format("Failed to fetch measures from %s", url), e);
    }

    Map<String, Metric> metricsIndex = indexMetrics(metrics);

    Set<Measure> result = new HashSet<>();
    for (JsonNode measure : measures.getComponent().getMeasures()) {
      String metricName = measure.get("metric").asText();
      Metric metric = metricsIndex.get(metricName);
      if (metric == null) {
        LOGGER.warn("Got measure for unknown metric '{}'", metric);
        continue;
      }

      LOGGER.trace("Found {} ({}): {}", metric.getId(), metric.getType(), measure);

      if (!metric.getType().isSupported()) {
        LOGGER.debug("Skipping not supported metric {} ({})", metric.getId(), metric.getType());
        continue;
      }

      double value = metric.getType().parseValue(measure);

      LOGGER.debug("Mapped {} -> {}", metric.getId(), value);
      result.add(new Measure(project, branch, metric, value));
    }

    return result;
  }

  private Map<String, Metric> indexMetrics(Set<Metric> metrics) {
    Map<String, Metric> result = new HashMap<>(metrics.size());

    for (Metric metric : metrics) {
      result.put(metric.getId(), metric);
    }

    return result;
  }
}
