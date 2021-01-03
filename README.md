# SonarQube to Prometheus

Exports SonarQube metrics to Prometheus.

It scrapes the SonarQube server at a configurable interval and extracts measures for all metrics on all projects on all
branches.

## Running

```shell
java -XX:+UseSerialGC -Xms16M -Xmx128M -jar sonarqube-to-prometheus-*.jar
```

To quit, press `Ctrl+C`.

By default, it exposes the metrics on `:8080/metrics`. You have to configure at least `sonarqube.token` in the config,
see below.

### Exit codes

* `0`: Everything is fine
* `1`: Unexpected error
* `2`: Config couldn't be loaded

## Configuration

Configuration is loaded from `config.toml` from the current working directory.

Most options have sane defaults, but you must set `sonarqube.token`!

```toml
[server]
hostname = "0.0.0.0" # Address to bind to
port = 8080 # Port to bind to
min_threads = 1 # Minimum http threads
max_threads = 8 # Maximum http threads

[sonarqube]
url = "http://localhost:9000/" # URL to SonarQube
token = "xxxxx" # SonarQube authentication token (My Account / Security) 
scrape_interval = "PT1H" # How often should SonarQube be scraped? PT1M is 1 minute, PT1H is 1 hour, etc. See https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/Duration.html#parse(java.lang.CharSequence)

[prometheus]
metrics_path = "/metrics" # URL to publish prometheus metrics

[projects]
include = [] # Project ids to include
exclude = [] # Project ids to exclude

[metrics]
include = [] # Metrics to include
exclude = [] # Metrics to exclude

[branches]
include = [] # Branches to include
exclude = [] # Branches to exclude
```

### Including / excluding

If the include list is non-empty, the project id / metric has to be in the list to be included.

Otherwise the exclude list is checked for the project id / metric.

Example:

```toml
[projects]
include = ["your.project:id"]
exclude = []
```

Only the project `your.project:id` will be scraped from SonarQube, all others are ignored.

Another example:

```toml
[projects]
include = []
exclude = ["your.project:id"]
```

All projects will be scraped, except the project `your.project:id`.

## Metric mapping

### Levels

The metric type `LEVEL` is mapped as follows:

* `OK` -> 0.0
* `WARN` -> 1.0
* `ERROR` -> 2.0

## License

[AGPLv3](https://www.gnu.org/licenses/agpl-3.0.txt)
