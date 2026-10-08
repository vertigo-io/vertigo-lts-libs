# vertigo-lts-libs

This repository keeps "old" Vertigo modules for Long Term Support (LTS).

Artifacts are published on Maven Central (`io.vertigo`).

## Modules

| Module | Description |
|---|---|
| `vertigo-datafactory-plugin-elasticsearch_5_6` | DataFactory search plugin for ElasticSearch 5.6 (legacy) |
| `vertigo-datafactory-plugin-elasticsearch_7_17` | DataFactory search plugin for ElasticSearch 7.17, **and** the in-memory collections index plugin for Lucene 8.11 (`io.vertigo.datafactory.plugins.collections.lucene_8_11.LuceneIndexPlugin`) |
| `vertigo-account-plugin-authorization-basic` | Basic authorization plugin for `vertigo-account` |

## ES 7.17 stack (since Vertigo 4.4.0)

Since Vertigo 4.4.0, the standard datafactory is based on ElasticSearch 9 / Lucene 9.
Projects that must stay on ElasticSearch 7.17 use this LTS artifact:

```xml
<dependency>
    <groupId>io.vertigo</groupId>
    <artifactId>vertigo-datafactory-plugin-elasticsearch_7_17</artifactId>
    <version>${vertigo.version}</version>
</dependency>
```

It provides:

- the ES 7.17 search plugin `io.vertigo.datafactory.plugins.search.elasticsearch_7_17.rest.RestHLClientESSearchServicesPlugin` (declared under `plugins:` in the YAML configuration)
- the collections index plugin `io.vertigo.datafactory.plugins.collections.lucene_8_11.LuceneIndexPlugin` (Lucene 8.11.3) — **required** for in-memory list search / full-text autocomplete. The standard `collections.luceneIndex` feature is Lucene 9-based and incompatible with the Lucene 8.11.3 pin required by ES 7.17.

The ES 7.17 connector `vertigo-elasticsearch_7_17-connector` is maintained in the [vertigo-connectors](https://github.com/vertigo-io/vertigo-connectors) repository and is pulled transitively by the artifact above.

See the [ES7 LTS Migration](https://github.com/vertigo-io/vertigo-core/wiki/Vertigo-Migration-Guide#from-432-to-440) section of the Vertigo migration guide for the complete setup (Lucene 8.11.3 pin, YAML configuration).
