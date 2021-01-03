package de.mkammerer.sq2p.sonarqube;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum MetricType {
  INT {
    @Override
    public boolean isSupported() {
      return true;
    }

    @Override
    public double parseValue(JsonNode node) {
      return DoubleParser.parse(node);
    }
  },
  FLOAT {
    @Override
    public boolean isSupported() {
      return true;
    }

    @Override
    public double parseValue(JsonNode node) {
      return DoubleParser.parse(node);
    }
  },
  PERCENT {
    @Override
    public boolean isSupported() {
      return true;
    }

    @Override
    public double parseValue(JsonNode node) {
      return DoubleParser.parse(node);
    }
  },
  BOOL,
  STRING,
  MILLISEC {
    @Override
    public boolean isSupported() {
      return true;
    }

    @Override
    public double parseValue(JsonNode node) {
      return DoubleParser.parse(node);
    }
  },
  DATA,
  LEVEL {
    @Override
    public boolean isSupported() {
      return true;
    }

    @Override
    public double parseValue(JsonNode node) {
      String value = ValueFinder.find(node);

      switch (value) {
        case "OK":
          return 0;
        case "WARN":
          return 1;
        case "ERROR":
          return 2;
        default:
          throw new IllegalStateException(String.format("Unexpected value: '%s'", value));
      }
    }
  },
  DISTRIB,
  RATING {
    @Override
    public boolean isSupported() {
      return true;
    }

    @Override
    public double parseValue(JsonNode node) {
      return DoubleParser.parse(node);
    }
  },
  WORK_DUR {
    @Override
    public boolean isSupported() {
      return true;
    }

    @Override
    public double parseValue(JsonNode node) {
      return DoubleParser.parse(node);
    }
  };

  private static final Set<String> VALUES = Stream.of(values()).map(Enum::name).collect(Collectors.toUnmodifiableSet());

  public static MetricType parse(String value) {
    return valueOf(value);
  }

  public static boolean isSupported(String type) {
    return VALUES.contains(type);
  }

  public boolean isSupported() {
    return false;
  }

  public double parseValue(JsonNode node) {
    return -1;
  }

  private static final class DoubleParser {
    private DoubleParser() {
      // Static class
    }

    static double parse(JsonNode node) {
      String value = ValueFinder.find(node);
      return Double.parseDouble(value);
    }
  }

  private static final class ValueFinder {
    private ValueFinder() {
      // Static class
    }

    static String find(JsonNode node) {
      JsonNode valueNode = node.get("value");
      if (valueNode != null) {
        return valueNode.textValue();
      }

      JsonNode periodNode = node.get("period");
      if (periodNode != null) {
        return periodNode.get("value").textValue();
      }

      throw new IllegalArgumentException(String.format("No value found in node '%s'", node));
    }

  }
}
