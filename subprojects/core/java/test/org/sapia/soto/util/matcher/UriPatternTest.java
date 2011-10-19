/**
 * 
 */
package org.sapia.soto.util.matcher;

import junit.framework.TestCase;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public class UriPatternTest extends TestCase {

  public void testNoWildcards() {
    UriPattern pattern = UriPattern.parse("*foo*");
    assertEquals(true, pattern.matches("this is a foo bar"));
    assertEquals(false, pattern.matches("this is a snack bar"));
    assertEquals(true, pattern.matches("foo bar"));
  }

  public void testWithWildcards() {
    UriPattern pattern = UriPattern.parse("sna*foo*");
    assertEquals(false, pattern.matches("this is a foo bar"));
    assertEquals(false, pattern.matches("this is a snack bar"));
    assertEquals(true, pattern.matches("sna, this is a foo bar"));
    assertEquals(true, pattern.matches("snafoo"));
    assertEquals(false, pattern.matches("wow... sna this is a foo bar"));
  }
}
