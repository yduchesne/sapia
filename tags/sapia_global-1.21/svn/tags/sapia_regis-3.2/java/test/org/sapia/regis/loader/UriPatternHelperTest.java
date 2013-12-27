package org.sapia.regis.loader;

import java.util.HashMap;

import junit.framework.TestCase;

public class UriPatternHelperTest extends TestCase {

  
  public void testMatches(){
    int[] pattern = UriPatternHelper.compilePattern("test-*");
    assertTrue(UriPatternHelper.match(new HashMap(), "test-1", pattern));
    assertTrue(UriPatternHelper.match(new HashMap(), "test-01", pattern));    
    assertTrue(UriPatternHelper.match(new HashMap(), "test-01-kjdkjdk", pattern));    
    assertTrue(!UriPatternHelper.match(new HashMap(), "01-test", pattern));    
  }
}
