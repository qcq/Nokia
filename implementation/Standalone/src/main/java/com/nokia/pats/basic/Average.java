package com.nokia.pats.basic;

import org.apache.commons.lang3.tuple.Pair;

import java.text.DecimalFormat;
import java.util.List;

public class Average {
  private Pair<Float, Float> limits = Pair.of(-1000f, 1000f);
  private DecimalFormat decimalFormat = new DecimalFormat("#.##");

  public String calculateAverage(List<String> data) {
    short counter = 0;
    float sum = 0f;
    Float temp;
    for (String item : data) {
      try {
        if ((-1 != item.indexOf('.') && (item.length() -  item.indexOf('.') <= 3) || -1 == item
            .indexOf('.')) &&  null != (temp = Float.valueOf(item))) {
          if (temp < limits.getLeft() || temp > limits.getRight()) {
            throw new NumberFormatException();
          }
          counter++;
          sum += Float.valueOf(item);
        } else {
          throw new NumberFormatException();
        }
      } catch (Exception e) {
        System.out.println("ERROR: " + item + " is not a legal number");
        continue;
      }
    }
    if (0 == counter) {
      System.out.println("The average of 0 numbers is Undefined");
      return "";
    }

    System.out.println("The average of " + counter + " numbers is " + decimalFormat.format(sum / counter));
    return decimalFormat.format(sum / counter);
  }
}
