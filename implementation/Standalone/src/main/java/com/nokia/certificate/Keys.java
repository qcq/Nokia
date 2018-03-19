package com.nokia.certificate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

@Component
public class Keys {
  private static final Logger LOG = LoggerFactory.getLogger(Keys.class);
  private static final int PRIVATE_KEY_SIZE = 2048;
  private static final String ALGORITHM_TYPE = "RSA";

  private KeyPairGenerator keyGenerator;
  private static KeyPair keyPair;

  public Keys() {
    generateKeyPair();
  }

  public static KeyPair keyPair() {
    return keyPair;
  }

  public KeyPair generateNewKeyPair() {
    LOG.debug("generate the new key pair.");
    keyPair = keyGenerator.generateKeyPair();
    return keyPair;
  }

  private void generateKeyPair() {
    if (null == keyGenerator) {
      try {
        keyGenerator = KeyPairGenerator.getInstance(ALGORITHM_TYPE);
      } catch (NoSuchAlgorithmException e) {
        LOG.error("Failed to generate KeyPair generator with exception {}.", e);
        return;
      }
      keyGenerator.initialize(PRIVATE_KEY_SIZE);
      keyPair = keyGenerator.generateKeyPair();
    }
  }
}
