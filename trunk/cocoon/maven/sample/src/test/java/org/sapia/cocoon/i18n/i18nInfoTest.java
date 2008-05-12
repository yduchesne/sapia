package org.sapia.cocoon.i18n;

import junit.framework.TestCase;

public class i18nInfoTest extends TestCase {

  public i18nInfoTest(String arg0) {
    super(arg0);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }
  
  public void testParse(){
    i18nInfo info = i18nInfo.parse("index.html");
    assertTrue(info.getLocale() == null);
    assertEquals("index", info.getSrc());
    assertEquals("html", info.getExtension());
    assertEquals("index.html", info.toString());
  }
  
  public void testParseLocalized(){
    i18nInfo info = i18nInfo.parse("index_fr.html");
    assertEquals("fr", info.getLocale());
    assertEquals("index", info.getSrc());
    assertEquals("html", info.getExtension());
    assertEquals("index_fr.html", info.toString());    
  }  

}
