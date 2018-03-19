package com.nokia.pats.interviews;

import java.util.LinkedList;
import java.util.List;

public class PrimeNumber {
  /*pe prime which less than limit*/
  public static List<Integer> getPrimeNumberByScreeningMethod(int limit) {
    boolean label[] = new boolean[limit + 1];
    for (int i = 1; i <= limit; ++i) {
      label[i] = true;
    }
    for (int i = 2; i <= limit; ++i) {
      if (label[i]) {
        for (int j = i + i; j <= limit;) {
          if (0 == j % i) {
            label[j] = false;
            j += i;
          }
        }
      }
    }
    List<Integer> result = new LinkedList<>();
    for (int i = 2; i < 101; ++i) {
      if (label[i]) {
        result.add(i);
      }
    }
    return result;
  }

  /*
  * require the prime number by sqrt
  * */

  public static List<Integer> getPrimeNumberBySqrt(int limit) {
    int k = 0;
    List<Integer> result = new LinkedList<>();
    for (int i = 2; i <= limit; ++i) {
      k = (int)Math.sqrt(i);
      int j;
      for (j = 2; j <= k; ++j) {
        if (0 == i%j) {
          break;
        }

      }
      if (j > k) {
        result.add(i);
      }
    }
    return result;
  }
}
