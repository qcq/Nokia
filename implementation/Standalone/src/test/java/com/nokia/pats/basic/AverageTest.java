package com.nokia.pats.basic;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import java.util.Arrays;
import java.util.List;

public class AverageTest {
  private Average average;

  @Before
  public void setUp() {
    average = new Average();
  }

  @Test
  public void should_equal() {
    List<String> data = Arrays.asList("5", "-3.2", "aaa", "9999", "2.3.4", "7.123", "2.35");
    average.calculateAverage(data);
    Assert.assertEquals("1.38", average.calculateAverage(data));
  }
}
