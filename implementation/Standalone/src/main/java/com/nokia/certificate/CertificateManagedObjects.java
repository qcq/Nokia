package com.nokia.certificate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

public class CertificateManagedObjects {
  private static final Logger LOG = LoggerFactory.getLogger(CertificateManagedObjects.class);

  public static String getThumbPrint(X509Certificate cert) {
    if (null == cert) {
      LOG.warn("can not retrieve the fingerprint with null Certificate.");
      return "";
    }
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] der = cert.getEncoded();
      md.update(der);
      byte[] digest = md.digest();
      return byteArrayToHexString(digest);
    } catch (NoSuchAlgorithmException | CertificateEncodingException e) {
      LOG.error("The certificate exist error with exception {}.", e);
      return "";
    }
  }

  private static String byteArrayToHexString(byte[] bytes) {
    if (null == bytes || 0 == bytes.length) {
      return null;
    }

    StringBuilder stringBuilder = new StringBuilder("");
    for (byte item : bytes) {
      String hexValue = Integer.toHexString(item & 0xFF).toUpperCase();
      if (hexValue.length() < 2) {
        stringBuilder.append(0);
      }
      stringBuilder.append(hexValue);
      stringBuilder.append(":");
    }
    return stringBuilder.substring(0, stringBuilder.length() - 1);
  }

}
