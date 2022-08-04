package com.tlglearning.concurrency;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ForkJoin implements Computation {

  private static final int FORK_THRESHOLD = 10_000_000;
  private final Object lock = new Object();
  private double logSum;

  @Override
  public double arithmeticMean(int[] data) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public double geometricMean(int[] data) {
    Worker worker = new Worker(data, 0, data.length);
    ForkJoinPool pool = new ForkJoinPool();
    pool.invoke(worker);
    return Math.exp(logSum / data.length); //int will auto get widened
  }

  private class Worker extends RecursiveAction {

    private final int[] data;
    private final int startIndex;
    private final int endIndex;

    private Worker(int[] data, int startIndex, int endIndex) {
      this.data = data;
      this.startIndex = startIndex;
      this.endIndex = endIndex;
    }

    @Override
    protected void compute() {
      if (endIndex - startIndex <= FORK_THRESHOLD) {
        double logSubtotal = 0;
        for (int i = startIndex; i < endIndex; i++) {
          logSubtotal += Math.log(data[i]);    // adding to subtotal
        }
        synchronized (lock) {         // only allows one total at a time; no stepping on toes
          logSum += logSubtotal;
        }

      } else {
        int midpoint = (startIndex + endIndex) / 2;
        invokeAll(new Worker(data, startIndex, midpoint), new Worker(data, midpoint, endIndex));
      }
    }
  }

}
