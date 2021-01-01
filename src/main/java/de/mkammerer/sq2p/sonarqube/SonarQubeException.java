package de.mkammerer.sq2p.sonarqube;

public class SonarQubeException extends Exception {
  public SonarQubeException(String message, Throwable cause) {
    super(message, cause);
  }
}
