package de.mkammerer.sq2p.sonarqube.connector.impl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.mkammerer.sq2p.sonarqube.Metric;
import de.mkammerer.sq2p.sonarqube.MetricType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricsDto {
  @JsonProperty("metrics")
  private List<MetricDto> metrics;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class MetricDto {
    @JsonProperty("key")
    private String key;
    @JsonProperty("type")
    private String type;
    @JsonProperty("hidden")
    private boolean hidden;

    public Metric toMetric() {
      return new Metric(key, MetricType.parse(type));
    }
  }
}
