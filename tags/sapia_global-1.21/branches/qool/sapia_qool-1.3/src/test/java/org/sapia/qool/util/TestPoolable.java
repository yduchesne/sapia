package org.sapia.qool.util;

import javax.jms.JMSException;

public class TestPoolable implements Poolable{
  
  public boolean disposed;
  public boolean valid = true;
  
  public void dispose() throws JMSException {
    disposed = true;
  }
  
  public boolean recycle() {
    return valid;
  }

}
