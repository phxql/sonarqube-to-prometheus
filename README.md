# SonarQube to Prometheus

Exports SonarQube metrics to Prometheus.

By default, it exposes the metrics on `:8080/metrics`.

## Configuration

Configuration is loaded from `config.toml` from the current working directory.

```toml
TODO: document
```

## Metric mapping

### Levels

The metric type `LEVEL` is mapped as follows:

* `OK` -> 0.0
* `WARN` -> 1.0
* `ERROR` -> 2.0

## License

[AGPLv3](https://www.gnu.org/licenses/agpl-3.0.txt)
