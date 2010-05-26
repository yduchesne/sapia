package org.sapia.util.lang;

import java.util.Map;

import junit.framework.TestCase;

public class ConstantTest extends TestCase {

  public ConstantTest(String arg0) {
    super(arg0);
  }
  
  public void testGetConstantsFrom(){
    Map constants = Constant.getConstantsFrom(TestConstants.class);
    assertEquals(3, constants.size());
    assertEquals(TestConstants.CONSTANT_1, constants.get(TestConstants.CONSTANT_1.getName()));
    assertEquals(TestConstants.CONSTANT_2, constants.get(TestConstants.CONSTANT_2.getName()));    
    assertEquals(TestConstants.CONSTANT_3, constants.get("CONSTANT_3"));    
  }
  
  public static class TestConstants{
    
    public static final Constant CONSTANT_1 = new Constant("CONSTANT_1", 0);
    
    public static final Constant CONSTANT_2 = new Constant("CONSTANT_2", 1);    

    public static final Constant CONSTANT_3 = new Constant(null, 3);
    
  }

}
