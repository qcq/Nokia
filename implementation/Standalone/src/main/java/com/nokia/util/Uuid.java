package com.nokia.util;

import java.util.Random;
import java.util.UUID;

public class Uuid {
  private Random random;

  public UUID generateId() {
    this.random = new Random();
    byte[] randomBytes = new byte[16];
    this.random.nextBytes(randomBytes);
    System.out.println(randomBytes);

    long mostSigBits = 0;
    for (int i = 0; i < 8; i++) {
      mostSigBits = (mostSigBits << 8) | (randomBytes[i] & 0xff);
      System.out.println(mostSigBits);
    }

    long leastSigBits = 0;
    for (int i = 8; i < 16; i++) {
      leastSigBits = (leastSigBits << 8) | (randomBytes[i] & 0xff);
      System.out.println(leastSigBits);
    }

    return new UUID(mostSigBits, leastSigBits);
  }
}
