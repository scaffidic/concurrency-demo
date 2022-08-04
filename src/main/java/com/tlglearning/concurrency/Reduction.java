package com.tlglearning.concurrency;

import java.util.LinkedList;
import java.util.List;

public class Reduction implements Computation {

  private static final int NUM_THREADS = 4;
  private final Object lock = new Object();
  private double logSum;

  @Override
  public double arithmeticMean(int[] data) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public double geometricMean(int[] data) {
    int slice = data.length / NUM_THREADS;
    List<Thread> workers = new LinkedList<>();
    for (int i = 0; i < NUM_THREADS; i++) {
      workers.add(spawn(data, i * slice, (i + 1) * slice));
    }
    for (Thread worker : workers) {
      try {
        worker.join();
      } catch (InterruptedException ignored) {
        //Ignore this exception
      }
    }
    return Math.exp(logSum / data.length); //int will auto get widened
  }

  private Thread spawn(int[] data, int startIndex, int endIndex) {
    Runnable work = () -> {
      double logSubtotal = 0;    //local variable inside lambda. if inside method, lambda cant modify local var in method
      for (int i = startIndex; i < endIndex; i++) {
        logSubtotal += Math.log(data[i]);    // adding to subtotal
      }
     synchronized (lock) {         // only allows one total at a time; no stepping on toes
        logSum += logSubtotal;
     }
    };
    Thread worker = new Thread(work);
    worker.start();
    return worker;
  }

}
