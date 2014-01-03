package org.sapia.regis.bean;

public class InvalidPropertyException extends RuntimeException{

  public InvalidPropertyException(String msg){
    super(msg);
  }
}
