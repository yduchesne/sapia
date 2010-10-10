/*
 * TestPOJO.java
 *
 * Created on July 27, 2005, 2:27 PM
 *
 */

package org.sapia.soto;

/** 
 *
 * @author yduchesne
 */
public class TestPOJO implements XmlAware{
   
  boolean init, start, dispose, xmlAware;
  
  /** Creates a new instance of TestPOJO */
  public TestPOJO() {
  }
  
  public void doInit(){
    init = true;
  }
  
  public void doStart(){
    start = true;
  }
  
  public void doDispose(){
    dispose = true;
  }
  
  public void setNameInfo(String name, String prefix, String uri) {
    xmlAware = true;
  }
  
}
