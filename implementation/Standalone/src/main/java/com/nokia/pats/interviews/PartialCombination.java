package com.nokia.pats.interviews;

public class PartialCombination {
  public static void listAll(String candidate, String  prefix) {
    if (candidate.isEmpty()) {
      System.out.println(prefix);
    }
    int index[] = new int[candidate.length()];
    for (int i = 0; i < candidate.length(); ++i) {
      index[i] = candidate.indexOf(candidate.charAt(i));
    }
    for (int i = 0; i < candidate.length(); i++) {
      if (i == index[i]) {
        listAll(candidate.substring(1), prefix + candidate.substring(0, 1));
      }
      candidate = candidate.substring(1) + candidate.substring(0,1);
      //System.out.println("cycle" + candidate);
    }
  }
}
