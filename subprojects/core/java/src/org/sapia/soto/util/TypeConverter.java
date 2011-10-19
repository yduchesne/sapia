package org.sapia.soto.util;

public interface TypeConverter {

  public Object convert(String value) throws IllegalArgumentException, RuntimeException;
}
