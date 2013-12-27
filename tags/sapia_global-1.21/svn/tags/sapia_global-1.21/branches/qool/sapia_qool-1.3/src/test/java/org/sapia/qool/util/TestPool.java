package org.sapia.qool.util;

import javax.jms.JMSException;

public class TestPool extends AbstractPool<TestPoolable, TestPoolConfig>{

  
  @Override
  protected TestPoolable doCreate(TestPoolConfig config) throws JMSException {
    return new TestPoolable();
  }
}
