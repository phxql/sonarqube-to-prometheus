# SonarQube to Prometheus

Exports SonarQube metrics to Prometheus.

## Running

```shell
java -Xmx128M -jar sonarqube-to-prometheus-*.jar
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
```

## Metric mapping

### Levels

The metric type `LEVEL` is mapped as follows:

* `OK` -> 0.0
* `WARN` -> 1.0
* `ERROR` -> 2.0

## License

[AGPLv3](https://www.gnu.org/licenses/agpl-3.0.txt)
