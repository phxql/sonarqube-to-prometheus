package de.mkammerer.sq2p.sonarqube.connector.impl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeasuresDto {
  @JsonProperty("component")
  private ComponentDto component;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ComponentDto {
    @JsonProperty("measures")
    private List<JsonNode> measures;
  }
}
