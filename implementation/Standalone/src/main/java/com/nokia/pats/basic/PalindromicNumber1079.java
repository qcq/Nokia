package com.nokia.pats.basic;

import java.math.BigInteger;

public class PalindromicNumber1079 {
  public static StringBuilder stringBuilder = new StringBuilder();

  public static void changeToPalindromicNumber(String number, int count) {
    BigInteger a = new BigInteger(number);
    if (isPalindromicNumber(number)) {
      System.out.println(number + " is a palindromic number.");
      return;
    }
    String reversedNumber = reverseTheNumber(number);
    String result = a.add(new BigInteger(reversedNumber)).toString();
    System.out.println(number + " + " + reversedNumber + " = " + result);
    count--;

    if (isPalindromicNumber(result)) {
      System.out.println(result + " is a palindromic number.");
      return;
    }

    if (0 == count){
      System.out.println("Not found in 10 iterations.");
      return;
    }

    changeToPalindromicNumber(result, count);
  }

  public static String reverseTheNumber(String number) {
    return stringBuilder.replace(0, stringBuilder.length(), number).reverse().toString();
  }

  public static boolean isPalindromicNumber(String number) {
    if (isNullOrEnpty(number)) {
      return false;
    }
    for (int start = 0, end = number.length() - 1; start < number.length() / 2; ++start, --end) {
      if (number.charAt(start) != number.charAt(end)) {
        return false;
      }
    }
    return true;
  }

  public static boolean isNullOrEnpty(String number) {
    return null == number || number.isEmpty();
  }
}
