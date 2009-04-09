package org.sapia.qool;

import org.sapia.qool.Debug.Level;

import junit.framework.TestCase;

public class DebugTest extends TestCase {

  public DebugTest(String name) {
    super(name);
  }

  public void testIsLoggable(){
  
    Debug.setGlobalLevel(Level.DEBUG);
    
    Debug d = Debug.createInstanceFor(this, Level.DEBUG);
    assertTrue(d.isLoggable(Level.DEBUG));
    assertTrue(d.isLoggable(Level.INFO));
    assertTrue(d.isLoggable(Level.WARNING));
    assertTrue(d.isLoggable(Level.ERROR));
    
    Debug.setGlobalLevel(Level.ERROR);
  
    assertTrue(!d.isLoggable(Level.DEBUG));
    assertTrue(!d.isLoggable(Level.INFO));
    assertTrue(!d.isLoggable(Level.WARNING));
    assertTrue(d.isLoggable(Level.ERROR));
  }
}
