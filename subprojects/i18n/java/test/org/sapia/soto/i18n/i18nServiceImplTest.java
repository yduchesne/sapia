/*
 * i18nServiceImplTest.java
 * JUnit based test
 *
 * Created on June 3, 2005, 10:24 PM
 */

package org.sapia.soto.i18n;

import java.io.File;
import java.util.Locale;

import junit.framework.TestCase;

import org.sapia.soto.SotoContainer;


/**
 *
 * @author yduchesne
 */
public class i18nServiceImplTest extends TestCase {
  
  SotoContainer container;
  
  public i18nServiceImplTest(String testName) {
    super(testName);
  }

  protected void setUp() throws Exception {
    container = new SotoContainer();
    container.load(new File("etc/i18n/i18nSample.xml"));
    container.start();
  }

  protected void tearDown() throws Exception {
  }

  public void testGetText() throws Exception{
    i18nService i18 = (i18nService)container.lookup(i18nService.class);
    super.assertEquals("Good morning world", i18.getText("hello", "morning", Locale.ENGLISH));
    super.assertEquals("Good evening world", i18.getText("hello", "evening", Locale.ENGLISH));    
    super.assertEquals("Bonjour le monde", i18.getText("hello", "morning", Locale.FRENCH));
    super.assertEquals("Bonsoir le monde", i18.getText("hello", "evening", Locale.FRENCH));    
  }
  
  public void testGetGroups() throws Exception{
    i18nService i18 = (i18nService)container.lookup(i18nService.class);
    String id = ((Group)i18.getGroups("hello").iterator().next()).getId();
    super.assertTrue(id.equals("morning") || id.equals("evening"));
  }

  
}
