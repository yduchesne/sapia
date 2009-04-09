package org.sapia.qool;

import org.sapia.qool.Debug.Level;

import junit.framework.TestCase;

public class DebugTest extends TestCase {

  public DebugTest(String name) {
    super(name);
  }

  public void testIsLoggable(){
    Debug d = Debug.createInstanceFor(this, Level.ERROR);
    assertTrue(!d.getLevel().isLoggable(Level.DEBUG));
    assertTrue(!d.getLevel().isLoggable(Level.INFO));
    assertTrue(!d.getLevel().isLoggable(Level.WARNING));
    assertTrue(d.getLevel().isLoggable(Level.ERROR));
  }
}
