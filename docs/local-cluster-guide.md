# SpliceEngine Local Cluster Guide

This guide explains how to start a local SpliceEngine cluster for development and testing, with a focus on the new **Apache HBase 2.x** profile (`apache-hbase-2`).

For build instructions, see the [Build Guide](build-guide.md).

---

## Prerequisites

1. **Build the project first.** The cluster startup scripts expect compiled artifacts to be present.

   ```bash
   # Build core + the platform you want to run
   mvn clean install -Pcore,apache-hbase-2 -DskipTests
   ```

2. **Ensure `nc` (netcat) is available** — the startup script uses it to probe ZooKeeper and the Splice JDBC port.

   ```bash
   # macOS
   brew install netcat

   # Ubuntu/Debian
   sudo apt-get install netcat

   # CentOS/RHEL
   sudo yum install nc
   ```

---

## Option A: One-Command Startup (Recommended)

The top-level `start-splice-cluster` script handles everything: kills any stale processes, builds the project, then starts ZooKeeper, YARN, Kafka, the HBase master + region server, and additional region servers.

### Apache HBase 2.x (new)

```bash
./start-splice-cluster -p apache-hbase-2
```

### Apache HBase 1.x (legacy open-source)

```bash
./start-splice-cluster -p apache-hbase
```

### Other Distributions

```bash
# Cloudera CDH 5.8.3 (default if no -p flag is given)
./start-splice-cluster

# HDP 2.6.4
./start-splice-cluster -p hdp2.6.4

# In-memory (no HBase, fastest)
./start-splice-cluster -p mem
```

### Common Flags

| Flag | Description |
|:-----|:------------|
| `-p <profile>` | Platform profile to use (default: `cdh5.8.3`) |
| `-s <n>` | Number of *extra* region servers to start (adds to the default 2) |
| `-b` | **Skip the build step** (use existing artifacts) |
| `-l` | **Skip the clean step** (reuse existing ZK/HBase data) |
| `-c` | Enable the chaos monkey (random task failures) |
| `-k` | **Kill** all running Splice/ZooKeeper/YARN/Kafka processes |
| `-h` | Print help |

### Examples

```bash
# Start with 4 region servers total, skip rebuild
./start-splice-cluster -p apache-hbase-2 -s 2 -b

# Kill all running processes
./start-splice-cluster -k
```

---

## Option B: Step-by-Step Manual Startup

For more control, you can start each component individually. All commands are run from the `platform_it` directory.

> **Replace `<PROFILE>` with the desired profile** throughout this section, e.g., `apache-hbase-2`, `cdh5.8.3`, etc.

### Step 1 — Build (if not already done)

```bash
mvn clean install -Pcore,<PROFILE> -DskipTests
```

### Step 2 — Start ZooKeeper

```bash
cd platform_it
mvn exec:exec -P<PROFILE>,spliceZoo
```

Wait until you see ZooKeeper respond to `ruok`:

```bash
echo ruok | nc localhost 2181   # should print: imok
```

### Step 3 — Start YARN

In a new terminal:

```bash
cd platform_it
mvn exec:java -P<PROFILE>,spliceYarn
```

### Step 4 — Start Kafka (optional)

Kafka is required for certain streaming features. In a new terminal:

```bash
cd platform_it
mvn exec:exec -P<PROFILE>,spliceKafka
```

### Step 5 — Start HBase Master + First Region Server

In a new terminal:

```bash
cd platform_it
mvn exec:exec -P<PROFILE>,spliceFast
```

Wait until port 1527 is open:

```bash
echo exit | nc localhost 1527
```

### Step 6 — Start Additional Region Servers (optional)

For each extra region server, use an incrementing `memberNumber`:

```bash
# Second region server (port 1528)
cd platform_it
mvn exec:exec -P<PROFILE>,spliceClusterMember -DmemberNumber=1

# Third region server (port 1529)
cd platform_it
mvn exec:exec -P<PROFILE>,spliceClusterMember -DmemberNumber=2
```

### Step 7 — Connect with the SQL Shell

From the project root:

```bash
./sqlshell.sh
```

Or, using `rlwrap` for command-history support:

```bash
cd splice_machine && rlwrap mvn exec:java
```

---

## Apache HBase 2.x — Key Differences

The `apache-hbase-2` profile was introduced to support **Apache HBase 2.6.4** on **Hadoop 3.3.6**.

### Version Matrix

| Component | apache-hbase (legacy) | apache-hbase-2 (new) |
|:----------|:----------------------|:---------------------|
| HBase | 1.2.0 | **2.6.4** |
| Hadoop | 2.6.0 | **3.3.6** |
| ZooKeeper | 3.4.6 | **3.8.4** |
| Hive | 1.1.0 | **2.3.9** |

### Versioned Source Directories

When the `apache-hbase-2` profile is active, Maven picks up HBase-version-specific source overrides from directories named after the `envHbase` property (`hbase2.6.0`). This name reflects the HBase major/minor version line targeted by those sources, not the exact patch version (2.6.4):

```
hbase_storage/hbase2.6.0/src/
hbase_pipeline/hbase2.6.0/src/
hbase_sql/hbase2.6.0/src/
```

These directories contain HBase 2.x-compatible implementations of APIs that changed between HBase 1.x and 2.x (e.g., `RegionObserver` coprocessor interface, compaction controller, cluster metrics API).

### JVM Flags for HBase 2.x

HBase 2.x reorganized its internal class layout. If you encounter `NoClassDefFoundError` at runtime, ensure the following are on the classpath:
- `hbase-protocol-<version>.jar` — provides the `org.apache.hadoop.hbase.protobuf.*` package (including `ProtobufUtil`, `ClientProtos`, `ResponseConverter`) that HBase 1.x used to export transitively via `hbase-client` but HBase 2.x does not
- `hbase-client-<version>.jar`
- `hbase-common-<version>.jar`

Maven handles this automatically when building with `-Papache-hbase-2`.

---

## Log Files

When using `start-splice-cluster`, log files are written to the `platform_it/` directory:

| Log File | Component |
|:---------|:----------|
| `platform_it/zoo.log` | ZooKeeper |
| `platform_it/yarn.log` | YARN |
| `platform_it/kafka.log` | Kafka |
| `platform_it/splice.log` | HBase master + primary region server |
| `platform_it/spliceRegionSvr2.log` | Second region server |
| `platform_it/spliceRegionSvr3.log` | Third region server |
| `platform_it/spliceMem.log` | In-memory server (mem profile only) |

To tail the main splice log:

```bash
tail -f platform_it/splice.log
```

---

## Stopping the Cluster

### Using the start-splice-cluster script

```bash
./start-splice-cluster -k
```

### Manually

```bash
# Kill all Splice + ZooKeeper + YARN + Kafka processes
kill $(ps -ef | awk '/SpliceTestPlatform|SpliceSinglePlatform|SpliceTestClusterParticipant|OlapServerMaster|spliceYarn|SpliceTestYarnPlatform|ZooKeeper|TestKafkaCluster/ && !/awk/ {print $2}')
```

---

## Troubleshooting

### ZooKeeper Does Not Start

- Check `platform_it/zoo.log` for details.
- Make sure port `2181` is not already in use: `lsof -i :2181`.

### HBase Does Not Start

- Check `platform_it/splice.log`.
- Ensure ZooKeeper is running (`echo ruok | nc localhost 2181` should return `imok`).

### JDBC Port 1527 Not Open

- Check `platform_it/splice.log` for stack traces.
- Make sure YARN started successfully (`platform_it/yarn.log`).

### `ClassNotFoundException` / `NoClassDefFoundError` (HBase 2.x)

- Confirm you built with `-Papache-hbase-2` (not `-Papache-hbase`).
- Run `mvn clean install -Pcore,apache-hbase-2 -DskipTests` to rebuild all artifacts.
- Check that `hbase-protocol-2.6.4.jar` is present in `platform_it/target/dependency/`.

### Stale Data Causing Startup Failures

```bash
# Remove HBase and ZooKeeper data directories, then restart
rm -rf platform_it/target/hbase platform_it/target/zookeeper
./start-splice-cluster -p apache-hbase-2 -b   # skip rebuild
```

---

## In-Memory Mode (No HBase)

For the fastest iteration loop, run the in-memory storage architecture. No ZooKeeper, YARN, or HBase are needed.

```bash
# Build
mvn clean install -Pcore,mem -DskipTests

# Start
cd mem_sql && mvn exec:java
```

Or use the script:

```bash
./start-splice-cluster -p mem
```

Then connect:

```bash
./sqlshell.sh
```
