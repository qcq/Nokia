package com.nokia.exception;

public class CertificateManagerException extends Exception {
  public CertificateManagerException(String message) {
    super(message);
  }

  public CertificateManagerException(Exception exception) {
    super(exception.getMessage(), exception);
  }
}
