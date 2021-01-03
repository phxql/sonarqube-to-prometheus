package de.mkammerer.sq2p.sonarqube.connector.impl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.mkammerer.sq2p.sonarqube.Branch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchesDto {
  // Example: 2021-01-03T10:14:27+0100
  private static final DateTimeFormatter DATE_TIME_PARSER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

  @JsonProperty("branches")
  private List<BranchDto> branches;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class BranchDto {
    @JsonProperty("name")
    private String name;
    @JsonProperty("analysisDate")
    private String analysisDate;

    public Branch toBranch() {
      return new Branch(name, OffsetDateTime.parse(analysisDate, DATE_TIME_PARSER).toInstant());
    }
  }
}
