/*
 * ResultTest.java
 *
 * Created on April 10, 2005, 5:07 AM
 */

package org.sapia.soto.state;

import junit.framework.TestCase;



/**
 *
 * @author yduchesne
 */
public class ResultTest extends TestCase{
  
  /** Creates a new instance of ResultTest */
  public ResultTest(String name) {
    super(name);
  }
  
  public void testIsError(){
    Result res = new Result(null, null, null);
    res.error("test");
    super.assertTrue(res.isError());
  }
  
  public void testIsErrorHandled(){
    Result res = new Result(null, null, null);
    res.error("test");
    super.assertTrue(!res.isErrorHandled());
    res.handleError();
    super.assertTrue(res.isErrorHandled());    
    res.error("test");
    super.assertTrue(!res.isErrorHandled());        
  }  
  
  public void testHandlingError(){
    Result res = new Result(null, null, null);
    res.error("test");
    res.handlingError();
    super.assertTrue(res.isHandlingError());            
    res = res.newInstance();
    super.assertTrue(res.isHandlingError());                
  }
  
}
