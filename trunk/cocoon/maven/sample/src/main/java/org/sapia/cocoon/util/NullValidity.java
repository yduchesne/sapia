package org.sapia.cocoon.util;

import org.apache.excalibur.source.SourceValidity;

public class NullValidity implements SourceValidity{
  
  static final long serialVersionUID = 1L;
  
  public int isValid() {
    return SourceValidity.INVALID; 
  }
  public int isValid(SourceValidity arg0) {
    return SourceValidity.INVALID;
  }    
}
