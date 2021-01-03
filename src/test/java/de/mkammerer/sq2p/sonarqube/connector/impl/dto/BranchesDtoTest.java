package de.mkammerer.sq2p.sonarqube.connector.impl.dto;

import de.mkammerer.sq2p.sonarqube.Branch;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class BranchesDtoTest {
  @Test
  void test() {
    BranchesDto.BranchDto dto = new BranchesDto.BranchDto("name", "2021-01-03T10:14:27+0100");

    Branch branch = dto.toBranch();

    assertThat(branch.getId()).isEqualTo("name");
    assertThat(branch.getLastAnalysis()).isEqualTo(Instant.parse("2021-01-03T09:14:27Z"));
  }
}