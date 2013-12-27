package org.sapia.soto.activemq.space;

import org.codehaus.activespace.Space;

public class TestReader implements Runnable{

  Space space;
  TestEntry entry;
  
  TestReader(Space space){
    this.space = space;
  }
  
  public void run() {
    entry = (TestEntry)space.take(10000);
    doNotify();
  }
  
  synchronized void doNotify(){
    notify();
  }
  
  
  
}
