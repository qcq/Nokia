package com.nokia.exception;

public class ManageObjectException extends Exception {
  public ManageObjectException(String message) {
    super(message);
  }

  public ManageObjectException(String message, Exception exception) {
    super(message, exception);
  }
}
