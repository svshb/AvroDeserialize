# Avro Deserialize

Code for Deserializing Millions Of Messages Per Second Per Core article.

### How to run benchmark

JDK11+ is required. Install [SBT](https://www.scala-sbt.org/) to run benchmarks without IDE, or install Scala plugin and import as SBT project.

Following command will run all benchmarks for 10 forks, 4 threads, 10 warmup iteration, 30 measure iterations, "Fail on error" enabled and 1 second per warmup/measure iteration:

`sbt 'jmh:run -f 10 -t 4 -wi 10 -i 30 -w 1 -r 1 -foe true'`

With these settings and given 5 benchmarks the whole run will take ~35 minutes. It is advised to not run anything else besides benchmark on the machine and provide sufficient cooling, otherwise performance numbers might drift or be too noisy.