Benchmarks of selecting top-k elements from a list of integers comparing
[Guava's Ordering class][guava] vs the [JDK PriorityQueue implementation][jdk-pq].

This benchmark was inspired by [Selecting top k items from a list efficiently in Java/Groovy][inspiration] 
which benchmarked several algorithms/implementations without being clear on the benchmark strategy used.

These benchmarks use [JMH](http://openjdk.java.net/projects/code-tools/jmh/).

[guava]: http://google.github.io/guava/releases/18.0/api/docs/com/google/common/collect/Ordering.html#leastOf(java.lang.Iterable, int)
[jdk-pq]: http://docs.oracle.com/javase/8/docs/api/java/util/PriorityQueue.html
[inspiration]: http://www.michaelpollmeier.com/selecting-top-k-items-from-a-list-efficiently-in-java-groovy/

# Selecting top-k with Guava Ordering

```java
return Ordering.natural().leastOf(numbers, k);
```

# Selecting top-k with PriorityQueue

```java
final PriorityQueue<Integer> queue = new PriorityQueue<>(numbers);

List<Integer> first = new ArrayList<>();
for (int i = 0; i < k; i++) {
  first.add(queue.poll());
}
return first;
```

# Results

Using the default JMH settings:

```
# JMH 1.9.2 (released 49 days ago)
# VM invoker: /Library/Java/JavaVirtualMachines/jdk1.8.0_40.jdk/Contents/Home/jre/bin/java
# VM options: <none>
# Warmup: 20 iterations, 1 s each
# Measurement: 20 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Parameters: (k = 5, size = 10000)

Benchmark                                (k)  (size)   Mode  Cnt      Score     Error  Units
PriorityQueueBenchmark.guavaOrdering       5   10000  thrpt  200  23379.739 ± 963.162  ops/s
PriorityQueueBenchmark.jdkPriorityQueue    5   10000  thrpt  200   5878.596 ± 172.701  ops/s
```

The Guava method is almost 4 times faster than using a PriorityQueue.
