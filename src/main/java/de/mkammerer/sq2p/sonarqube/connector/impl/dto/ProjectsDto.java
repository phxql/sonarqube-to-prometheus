package de.mkammerer.sq2p.sonarqube.connector.impl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.mkammerer.sq2p.sonarqube.Project;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectsDto {
  @JsonProperty("components")
  List<ComponentsDto> components;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ComponentsDto {
    @JsonProperty("name")
    private String name;
    @JsonProperty("key")
    private String key;

    public Project toProject() {
      return new Project(key, name);
    }
  }
}
