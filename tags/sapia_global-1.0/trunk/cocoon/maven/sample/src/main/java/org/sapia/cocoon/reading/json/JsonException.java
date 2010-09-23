package org.sapia.cocoon.reading.json;

public class JsonException {
  
  private String message;
  private String className;
  
  public JsonException(Throwable t){
    this.message = t.getMessage();
    this.className = t.getClass().getName();
  }

  public String getMessage() {
    return message;
  }
  
  public String getClassName() {
    return className;
  }

}
