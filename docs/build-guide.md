# SpliceEngine Build Guide

This guide covers how to build SpliceEngine for all supported platforms, including the newly-added Apache HBase 2.x support.

---

## Prerequisites

### Required Software

| Tool | Version | Notes |
|:-----|:--------|:------|
| JDK | **11** | OpenJDK 11 or Eclipse Temurin 11 recommended |
| Apache Maven | **3.3.x** (3.3.9 recommended) | Jenkins CI uses 3.3.9 |
| Git | any recent version | |
| `rlwrap` | any | Optional but recommended for the SQL shell |

### Recommended Environment Variables

Add the following to your shell profile (`~/.bash_profile`, `~/.zshrc`, etc.):

```bash
# Java
export JAVA_HOME=$(/usr/libexec/java_home -v 11)     # macOS
# export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64 # Ubuntu/Debian
# export JAVA_HOME=/usr/lib/jvm/java-11-openjdk       # CentOS/RHEL

# Maven
export M2_HOME="/opt/maven/apache-maven-3.3.9"
export M2="${M2_HOME}/bin"
export MAVEN_OPTS="-Xmx4g -Djava.awt.headless=true -XX:ReservedCodeCacheSize=512m"
export PATH="${M2}:${PATH}"
```

---

## Build Profiles Overview

SpliceEngine uses Maven profiles to target different storage/platform backends.
Every build is composed of two layers:

| Layer | Description |
|:------|:------------|
| `core` | Platform-independent SQL engine, encoding libraries, SI API, pipeline API |
| *platform profile* | HBase storage + pipeline modules for a specific distribution |

> **Tip:** Pass `-DskipTests` to skip tests and speed up compilation.

### Available Platform Profiles

| Profile ID | HBase Version | Hadoop Version | Notes |
|:-----------|:--------------|:---------------|:------|
| `mem` | — | — | In-memory only; no HBase; for unit tests and quick dev iteration |
| `apache-hbase` | 1.2.0 | 2.6.0 | Open-source Apache HBase 1.2 |
| **`apache-hbase-2`** | **2.6.4** | **3.3.6** | **Open-source Apache HBase 2.x (latest)** |
| `hdp2.5.5` | 1.1.2 | 2.7.3 (HDP 2.5.5.7-1) | Hortonworks HDP 2.5.5 |
| `hdp2.6.1` | 1.1.2 | 2.7.3 (HDP 2.6.1.0-129) | Hortonworks HDP 2.6.1 |
| `hdp2.6.3` | 1.1.2 | 2.7.3 (HDP 2.6.3.0-235) | Hortonworks HDP 2.6.3 |
| `hdp2.6.4` | 1.1.2 | 2.7.3 (HDP 2.6.4.0-91) | Hortonworks HDP 2.6.4 |
| `cdh5.8.3` | 1.2.0 | 2.6.0 (CDH 5.8.3) | Cloudera CDH 5.8.3 |
| `cdh5.8.5` | 1.2.0 | 2.6.0 (CDH 5.8.5) | Cloudera CDH 5.8.5 |
| `cdh5.12.0` | 1.2.0 | 2.6.0 (CDH 5.12.0) | Cloudera CDH 5.12.0 |
| `cdh5.12.2` | 1.2.0 | 2.6.0 (CDH 5.12.2) | Cloudera CDH 5.12.2 |
| `cdh5.13.0` | 1.2.0 | 2.6.0 (CDH 5.13.0) | Cloudera CDH 5.13.0 |
| `cdh5.13.2` | 1.2.0 | 2.6.0 (CDH 5.13.2) | Cloudera CDH 5.13.2 |
| `cdh5.13.3` | 1.2.0 | 2.6.0 (CDH 5.13.3) | Cloudera CDH 5.13.3 |
| `cdh5.14.0` | 1.2.0 | 2.6.0 (CDH 5.14.0) | Cloudera CDH 5.14.0 |
| `mapr5.2.0` | 1.1.8 | 2.7.0 (MapR 5.2) | MapR 5.2 |
| `mapr6.0.0` | 1.1.8 | 2.7.0 (MapR 6.0) | MapR 6.0 |

---

## Building

### 1. Build Core Modules (required once, or when core code changes)

```bash
mvn clean install -Pcore -DskipTests
```

This compiles everything that is platform-independent: the SQL engine, encoding library, snapshot-isolation API, pipeline API, and shared test infrastructure.

### 2. Build a Platform

After the core build, compile the platform-specific modules by selecting one of the profiles above.

#### In-Memory Platform (fastest; for local dev & unit tests)

```bash
mvn clean install -Pmem -DskipTests
```

#### Apache HBase 1.2 (open-source)

```bash
mvn clean install -Papache-hbase -DskipTests
```

#### Apache HBase 2.x (latest, open-source) — New!

```bash
mvn clean install -Papache-hbase-2 -DskipTests
```

This profile targets **Apache HBase 2.6.4** on **Hadoop 3.3.6** / **ZooKeeper 3.8.4**.

#### Cloudera CDH 5.8.3 (example distribution)

```bash
mvn clean install -Pcdh5.8.3 -DskipTests
```

#### Combined Core + Platform Build (most common)

You can combine `core` and a platform profile in a single command:

```bash
# Apache HBase 2.x — full build
mvn clean install -Pcore,apache-hbase-2 -DskipTests

# CDH 5.8.3 — full build
mvn clean install -Pcore,cdh5.8.3 -DskipTests

# In-memory — full build including tests
mvn clean install -Pcore,mem
```

### 3. Build Without Cleaning (incremental rebuild)

Omit the `clean` phase when only a few files have changed:

```bash
mvn install -Pcore,apache-hbase-2 -DskipTests
```

---

## Running Tests

### Unit Tests (architecture-independent)

Unit tests annotated with `@Category(ArchitectureIndependent.class)` run automatically during any build, even without a platform profile.

### Integration Tests

Integration tests require a running local cluster (see the [Local Cluster Guide](local-cluster-guide.md)).

To run tests against the in-memory architecture:

```bash
mvn clean install -Pcore,mem
```

To run integration tests against Apache HBase 2.x:

```bash
# 1. Start the local cluster first (see local-cluster-guide.md)
# 2. Then run:
cd platform_it && mvn verify -Papache-hbase-2
```

---

## IDE Setup

### IntelliJ IDEA

1. Open IntelliJ → **File → Open** → select the top-level `pom.xml`.
2. In the Maven panel, enable the desired profiles (e.g., `core` + `apache-hbase-2`).
3. Click **Reimport**.

> Make sure the Project SDK is set to **Java 11**.

### Eclipse

1. **File → Import → Existing Maven Projects** → select the top-level directory.
2. Activate the desired Maven profiles in the project settings.

---

## Quick Start (One Command)

The `start-splice-cluster` script builds everything and starts a local cluster in one step.

For the default profile (`cdh5.8.3`):
```bash
./start-splice-cluster
```

For Apache HBase 2.x:
```bash
./start-splice-cluster -p apache-hbase-2
```

Then connect with:
```bash
./sqlshell.sh
```

See the [Local Cluster Guide](local-cluster-guide.md) for detailed startup instructions.

---

## Dependency Notes

### hbase-protocol (HBase 2.x)

Under HBase 2.x, the shaded protobuf classes (`ClientProtos`, `ProtobufUtil`, `ResponseConverter`) are no longer transitively exported by `hbase-client`. SpliceEngine's `hbase_storage`, `hbase_pipeline`, and `hbase_sql` modules therefore declare `hbase-protocol` as an explicit `provided` dependency when the `apache-hbase-2` profile is active.

### Java Compatibility

All modules are compiled with **JDK 11** (`-source 11 -target 11`). JDK 11 is the minimum required runtime version.
