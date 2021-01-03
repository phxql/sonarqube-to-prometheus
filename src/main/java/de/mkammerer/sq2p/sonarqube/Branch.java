package de.mkammerer.sq2p.sonarqube;

import lombok.Value;

import java.time.Instant;

@Value
public class Branch {
  String id;
  Instant lastAnalysis;
}
