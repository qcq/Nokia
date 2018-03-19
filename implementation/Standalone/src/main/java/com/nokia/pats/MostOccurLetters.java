package com.nokia.pats;

import com.google.common.base.Strings;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MostOccurLetters {
  private MostOccurLetters() {

  }

  public static List<Map.Entry<String, Integer>> countTheMostOccurLetters(String letters) {
    if (Strings.isNullOrEmpty(letters)) {
      return Collections.emptyList();
    }
    Map<String, Integer> lettersByCount = new HashMap<>();
    for (char c : letters.toCharArray()) {
      lettersByCount.put(String.valueOf(c),
            lettersByCount.getOrDefault(String.valueOf(c), 0) + 1);
    }
    List<Map.Entry<String, Integer>> result = lettersByCount.entrySet().stream().sorted(
          Comparator.comparing(Map.Entry<String, Integer>::getValue).reversed()).collect(Collectors.toList());
    return result.stream().filter(entry ->
          entry.getValue() == result.get(0).getValue()).collect(Collectors.toList());
  }
}
