package de.mkammerer.sq2p.schedule;

import de.mkammerer.sq2p.metric.MetricService;
import de.mkammerer.sq2p.sonarqube.Measure;
import de.mkammerer.sq2p.sonarqube.Metric;
import de.mkammerer.sq2p.sonarqube.Project;
import de.mkammerer.sq2p.sonarqube.SonarQubeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class Scheduler {
  private final SonarQubeService sonarQubeService;
  private final MetricService metricService;

  public void run() {
    LOGGER.info("Running ...");
    try {
      Set<Metric> metrics = sonarQubeService.fetchMetrics();

      Set<Project> projects = sonarQubeService.fetchProjects();
      for (Project project : projects) {
        Set<Measure> measures = sonarQubeService.fetchMeasure(project, metrics);
        for (Measure measure : measures) {
          metricService.updateMeasure(measure);
        }
      }
    } catch (Exception e) {
      LOGGER.error("Unhandled exception in scheduler thread, please report this as an issue", e);
    } finally {
      LOGGER.info("Done");
    }
  }
}
