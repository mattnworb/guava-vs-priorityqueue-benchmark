Benchmarks of selecting top-k elements from a list of integers comparing
[Guava's Ordering class][guava] vs the [JDK PriorityQueue implementation][jdk-pq].

This benchmark was inspired by [Selecting top k items from a list efficiently in Java/Groovy][inspiration] 
which benchmarked several algorithms/implementations without being clear on the benchmark strategy used.

These benchmarks use [JMH](http://openjdk.java.net/projects/code-tools/jmh/).

[guava]: http://google.github.io/guava/releases/18.0/api/docs/com/google/common/collect/Ordering.html#leastOf(java.lang.Iterable,%20int)
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

# Why?

The [algorithm used by the Ordering class (at least as of version 18)][algorithm] is:

1. Create a buffer of size `2 * k`
2. Iterate over the input and only fill the buffer with values less than the
   max seen so far (or greater than the max, depending on the desired ordering)
3. When the buffer gets full: 
  - find the median value in the buffer using [quickselect][]
  - rearrange the values in the buffer around the median
  - then discard (i.e. ignore, by resetting the `bufferSize` pointer) the last
    k elements
4. After iteration is complete, sort and return the first `k` items in the buffer

As the comments in the Ordering class mention, this results in a O(n) algorithm
(where `n` is size of input list) that passes over the input only once and
needs just O(k) memory (beyond the original inputs). The "find median and
partition" operation needs to be done `n / k` times.

TODO: comparison with what `java.util.PriorityQueue` does

[algorithm]: https://github.com/google/guava/blob/v18.0/guava/src/com/google/common/collect/Ordering.java#L666
[quickselect]: https://en.wikipedia.org/wiki/Quickselect
