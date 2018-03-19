package com.nokia.pats.interviews;

import java.util.LinkedList;
import java.util.List;

public class Combination {
  public static void listAll(List candidate, String  prefix) {
    if (candidate.isEmpty()) {
      System.out.println(prefix);
    }
    for (int i = 0; i < candidate.size(); i++) {
      List temp = new LinkedList(candidate);
      listAll(temp, prefix + temp.remove(i));
    }
  }
}
