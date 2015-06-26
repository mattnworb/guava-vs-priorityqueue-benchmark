package com.mattnworb.benchmarks;

import com.google.common.collect.Ordering;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@State(Scope.Benchmark)
public class PriorityQueueBenchmark {

  /** Size of number list to generate */
  @Param("10000")
  int size;

  /** Number of items to choose from queue/list, i.e. top-k from queue */
  @Param("5")
  int k;

  List<Integer> numbers;


  @Setup
  public void setUp() {
    numbers = ThreadLocalRandom.current().ints(size)
        // seems pretty silly that Collectors.toList() can't be used with IntStream.collect(..):
        .collect(
            () -> new ArrayList<>(size),
            ArrayList::add,
            ArrayList::addAll
        );
  }

  @Benchmark
  public List<Integer> guavaOrdering() {
    return Ordering.natural().leastOf(numbers, k);
  }

  @Benchmark
  public List<Integer> jdkPriorityQueue() {
    final PriorityQueue<Integer> queue = new PriorityQueue<>(numbers);

    List<Integer> first = new ArrayList<>();
    for (int i = 0; i < k; i++) {
      first.add(queue.poll());
    }
    return first;
  }

  /**
   * A separate benchmark of using PriorityQueue to see what the difference is in building a List
   * ourselves (as above) versus stream() and limit() - should expect no significant difference.
   */
  @Benchmark
  public List<Integer> jdkPriorityQueue_RemoveWithStream() {
    final PriorityQueue<Integer> queue = new PriorityQueue<>(numbers);

    return queue.stream()
        .limit(k)
        .collect(Collectors.toList());
  }
}
