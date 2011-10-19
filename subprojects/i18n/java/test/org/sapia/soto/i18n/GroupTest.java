/*
 * GroupTest.java
 * JUnit based test
 *
 * Created on June 3, 2005, 9:08 PM
 */

package org.sapia.soto.i18n;

import java.util.Locale;

import junit.framework.TestCase;


/**
 *
 * @author yduchesne
 */
public class GroupTest extends TestCase {
  
  public GroupTest(String testName) {
    super(testName);
  }

  protected void setUp() throws Exception {
  }

  protected void tearDown() throws Exception {
  }

  public void testSingleLang() throws Exception{
    Group entries = new Group();
    entries.setId("test");
    Entry entry = entries.createEntry();
    entry.setLa(Locale.ENGLISH.getLanguage());
    entries.onCreate();
    super.assertTrue(entries.lookup(Locale.ENGLISH) != null);
  }
  
  public void testMultiLang() throws Exception{
    Group entries = new Group();
    entries.setId("test");
    
    Entry deflt = entries.createEntry();
    Entry english = entries.createEntry();
    english.setLa(Locale.ENGLISH.getLanguage());    
    entries.onCreate();
    super.assertEquals(english, entries.lookup(Locale.ENGLISH));
    super.assertEquals(deflt, entries.lookup(Locale.FRENCH));    
  }  
  
  public void testCountry() throws Exception{
    Group entries = new Group();
    entries.setId("test");
    
    Entry english = entries.createEntry();
    english.setLa(Locale.ENGLISH.getLanguage());    
    Entry englishUS = entries.createEntry();    
    englishUS.setLa(Locale.ENGLISH.getLanguage());        
    englishUS.setCo(Locale.US.getCountry());            
    entries.onCreate();
    
    super.assertEquals(english, entries.lookup(Locale.ENGLISH));
    super.assertEquals(englishUS, entries.lookup(Locale.US));    
  }    
  
  
  public void testVariant() throws Exception{
    Group entries = new Group();
    entries.setId("test");
    
    Locale locale1 = new Locale("la", "co", "var1");
    Locale locale2 = new Locale("la", "co", "var2");    
    
    Entry entry1 = entries.createEntry();
    entry1.setLa(locale1.getLanguage());
    entry1.setCo(locale1.getCountry());
    entry1.setVa(locale1.getVariant());    
    
    Entry entry2 = entries.createEntry();
    entry2.setLa(locale2.getLanguage());
    entry2.setCo(locale2.getCountry());
    entry2.setVa(locale2.getVariant());        
    
    entries.onCreate();
    
    super.assertEquals(entry1, entries.lookup(locale1));    
    super.assertEquals(entry2, entries.lookup(locale2));        
  }   
  
}
