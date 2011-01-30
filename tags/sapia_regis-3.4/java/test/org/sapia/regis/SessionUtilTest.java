package org.sapia.regis;

import org.sapia.regis.local.LocalRegisSession;

import junit.framework.TestCase;

public class SessionUtilTest extends TestCase {

  public SessionUtilTest(String arg0) {
    super(arg0);
  }

  
  public void testJoin(){
    SessionUtil.join(new LocalRegisSession());
    assertTrue(SessionUtil.isJoined());
  }
  
  public void testUnjoin(){
    SessionUtil.join(new LocalRegisSession());
    SessionUtil.unjoin();
    assertTrue(!SessionUtil.isJoined());
  }  
  
  public void testClose(){
    SessionUtil.join(new LocalRegisSession());
    SessionUtil.close();
    assertTrue(!SessionUtil.isJoined());    
  }
  

}
