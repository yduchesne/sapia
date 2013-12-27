package org.sapia.soto.activemq.space;

import junit.framework.TestCase;

public class SpaceUtilsTest extends TestCase {

  public void testCreateSelectorStringFor() throws Exception{
    assertEquals("JMSType='org.sapia.soto.activemq.space.SpaceUtilsTest$Entry' AND intg='0' AND txt='Foo'", new SpaceUtils().createSelectorStringFor(new Entry()));
  }
  
  static final class Entry{
    public Integer intg = new Integer(0);
    public String txt = "Foo"; 
    private String priv = "Private";
  }

}
