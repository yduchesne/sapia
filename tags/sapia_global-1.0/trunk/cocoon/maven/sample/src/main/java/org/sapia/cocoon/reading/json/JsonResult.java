package org.sapia.cocoon.reading.json;

public class JsonResult {
  
  private JsonException exception;
  
  private Object value;

  public JsonException getException() {
    return exception;
  }

  public void setException(JsonException exception) {
    this.exception = exception;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

}
