# Avro Deserialize

Code for Deserializing Millions Of Messages Per Second Per Core article.

The code shows the evolution of deserialization pipeline for Avro messages
 and is roughly separated into 4 parts:
1) Baseline implementation using Apache Avro deserializer (`tsdb.benchmark_01`);
2) A manually written parser which parses message by a fixed schema using byte operations on
 source byte array (`tsdb.benchmark_02`);
3) An improvement of manually written parses which does string interning - instead of parsing
 string fields it matches string payload to unique id such that same payloads has same id
 and different strings has different ids. It has two sub versions - one stores strings in a HashMap,
 while another uses array (`tsdb.benchmark_03`);
4) A concurrent version of the parser, which uses concurrent string interner to
 have a synchronized view on string ids (`tsdb.benchmark_04`).

The parser (with slight changes) is powering deserialization pipeline in Agoda's internal
distributed high-load time-series database. `tsdb.deserialize.InterningAvroDeserializerArrayTags`
 is the final version of the deserializer with `tsdb.interner.ConcurrentStringInterner` used for string interning.

### How to run benchmark

JDK11+ is required. Install [SBT](https://www.scala-sbt.org/) to run benchmarks without IDE, or install Scala plugin and import as SBT project.

Following command will run all benchmarks for 10 forks, 4 threads, 10 warmup iteration, 30 measure iterations, "Fail on error" enabled and 1 second per warmup/measure iteration:

`sbt 'jmh:run -f 10 -t 4 -wi 10 -i 30 -w 1 -r 1 -foe true'`

With these settings and given 5 benchmarks the whole run will take ~35 minutes. It is advised to not run anything else besides benchmark on the machine and provide sufficient cooling, otherwise performance numbers might drift or be too noisy.