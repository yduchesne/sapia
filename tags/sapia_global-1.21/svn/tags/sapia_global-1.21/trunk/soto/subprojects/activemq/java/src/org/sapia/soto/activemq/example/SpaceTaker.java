package org.sapia.soto.activemq.example;

import org.codehaus.activespace.Space;

public class SpaceTaker {
  
  Space space;

  public Space getSpace() {
    return space;
  }

  public void setSpace(Space space) {
    this.space = space;
  }
  
  public SampleEntry take(){
    return (SampleEntry)space.take(2000);
  }

}
