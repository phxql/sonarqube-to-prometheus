package de.mkammerer.sq2p.schedule;

import de.mkammerer.sq2p.config.Config;
import de.mkammerer.sq2p.metric.MetricService;
import de.mkammerer.sq2p.sonarqube.Branch;
import de.mkammerer.sq2p.sonarqube.Measure;
import de.mkammerer.sq2p.sonarqube.Metric;
import de.mkammerer.sq2p.sonarqube.Project;
import de.mkammerer.sq2p.sonarqube.SonarQubeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class Scheduler {
  private final SonarQubeService sonarQubeService;
  private final MetricService metricService;
  private final Config config;

  public void run() {
    LOGGER.info("Running ...");
    try {
      Set<Metric> metrics = filterMetrics(sonarQubeService.fetchMetrics());
      LOGGER.debug("Metrics to scrape: {}", metrics);

      Set<Project> projects = sonarQubeService.fetchProjects();
      for (Project project : projects) {
        if (!isProjectIncluded(project)) {
          LOGGER.debug("Skipping project '{}'", project.getId());
          continue;
        }

        LOGGER.info("Found project '{}' (named '{}')", project.getId(), project.getName());

        Set<Branch> branches = sonarQubeService.fetchBranches(project);
        for (Branch branch : branches) {
          if (!isBranchIncluded(branch)) {
            LOGGER.debug("Skipping branch '{}'", branch.getId());
            continue;
          }

          LOGGER.info("Found branch '{}', last analysis: {}", branch.getId(), branch.getLastAnalysis());

          Set<Measure> measures = sonarQubeService.fetchMeasure(project, branch, metrics);
          for (Measure measure : measures) {
            metricService.updateMeasure(measure);
          }
        }
      }
    } catch (Exception e) {
      LOGGER.error("Unhandled exception in scheduler thread, please report this as an issue", e);
    } finally {
      LOGGER.info("Done");
    }
  }

  private Set<Metric> filterMetrics(Set<Metric> metrics) {
    return metrics.stream()
      .filter(this::isMetricIncluded)
      .collect(Collectors.toUnmodifiableSet());
  }

  private boolean isProjectIncluded(Project project) {
    Set<String> include = config.getProjects().getInclude();
    Set<String> exclude = config.getProjects().getExclude();

    // if the include list is non-empty, the project id has to be in it
    if (!include.isEmpty()) {
      return include.contains(project.getId());
    }

    // Otherwise check the exclude list if the project id is excluded
    return !exclude.contains(project.getId());
  }

  private boolean isMetricIncluded(Metric metric) {
    Set<String> include = config.getMetrics().getInclude();
    Set<String> exclude = config.getMetrics().getExclude();

    // if the include list is non-empty, the metric id has to be in it
    if (!include.isEmpty()) {
      return include.contains(metric.getId());
    }

    // Otherwise check the exclude list if the metric id is excluded
    return !exclude.contains(metric.getId());
  }

  private boolean isBranchIncluded(Branch branch) {
    Set<String> include = config.getBranches().getInclude();
    Set<String> exclude = config.getBranches().getExclude();

    // if the include list is non-empty, the branch id has to be in it
    if (!include.isEmpty()) {
      return include.contains(branch.getId());
    }

    // Otherwise check the exclude list if the branch id is excluded
    return !exclude.contains(branch.getId());
  }
}
