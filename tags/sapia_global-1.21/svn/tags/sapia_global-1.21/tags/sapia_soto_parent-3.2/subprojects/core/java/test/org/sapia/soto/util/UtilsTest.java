package org.sapia.soto.util;

import junit.framework.TestCase;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.sapia.util.text.MapContext;
import org.sapia.util.text.SystemContext;
import org.sapia.util.text.TemplateException;

/**
 * @author Yanick Duchesne 30-Sep-2003
 */
public class UtilsTest extends TestCase {
  /**
   * Constructor for UtilsTest.
   * 
   * @param arg0
   */
  public UtilsTest(String arg0) {
    super(arg0);
  }

  public void testIsRelativePath() {
    String path = "/absolute";
    super.assertTrue("Path is absolute", !Utils.isRelativePath(path));
    path = "relative";
    super.assertTrue("Path is relative", Utils.isRelativePath(path));
    path = "file:/relative";
    super.assertTrue("Path is not relative", !Utils.isRelativePath(path));
    path = "./etc/config";
    super.assertTrue("Path is not relative", !Utils.isRelativePath(path));
  }

  public void testGetRelativePath() {
    String base = "base";
    String path = "relative";
    super.assertEquals("base" + File.separator + "relative", Utils
        .getRelativePath(base, path, false));

    base = "root/base.xml";
    super.assertEquals("root" + File.separator + "relative", Utils
        .getRelativePath(base, path, true));

    base = "base";
    super.assertEquals("base" + File.separator + "relative", Utils
        .getRelativePath(base, path, true));
  }

  public void testChopLocalUrl() {
    String path = "resource:/org/sapia/soto\\included.xml";
    super.assertEquals("/org/sapia/soto\\included.xml", Utils.chopScheme(path));
  }

  public void testChopNetworkUrl() {
    String path = "http://org/sapia/soto\\included.xml";
    super.assertEquals("/org/sapia/soto\\included.xml", Utils.chopScheme(path));
  }

  public void testChopRelativePath() {
    String path = "org/sapia/soto\\included.xml";
    super.assertEquals("org/sapia/soto\\included.xml", Utils.chopScheme(path));
  }

  public void testChopAbsolutePath() {
    String path = "/org/sapia/soto\\included.xml";
    super.assertEquals("/org/sapia/soto\\included.xml", Utils.chopScheme(path));
  }
  
  public void testReplaceVarsWithProperties() throws TemplateException{
    Properties props = new Properties();
    Map vars = new HashMap();
    for(int i = 0; i < 5; i++){
      props.setProperty(""+i, "${var_"+i+"}");
      vars.put("var_"+i, "val_"+i);
    }
    props = Utils.replaceVars(new MapContext(vars, new SystemContext(), true), props);
    for(int i = 0; i < 5; i++){
      super.assertEquals("val_"+i, props.getProperty(""+i));
    }    
  }
}
