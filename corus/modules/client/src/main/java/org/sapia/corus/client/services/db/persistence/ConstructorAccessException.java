package org.sapia.corus.client.services.db.persistence;

public class ConstructorAccessException extends RuntimeException {

  static final long serialVersionUID = 1L;

  public ConstructorAccessException(String msg, Throwable cause) {
    super(msg, cause);
  }

}
