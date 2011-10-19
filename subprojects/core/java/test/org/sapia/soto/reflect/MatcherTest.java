package org.sapia.soto.reflect;

import junit.framework.TestCase;

import java.util.List;

/**
 * @author Yanick Duchesne 18-Aug-2003
 */
public class MatcherTest extends TestCase {
  /**
   * Constructor for FilterTest.
   */
  public MatcherTest(String name) {
    super(name);
  }

  public void testNoMethodName() throws Exception {
    Matcher m = new Matcher();
    m.setDeclaredMethods(true);
    m.setSig("java.lang.String");

    List methods = m.scanMethods(Dog.class);
    super.assertEquals(2, methods.size());
  }

  public void testWithMethodName() throws Exception {
    Matcher m = new Matcher();
    m.setDeclaredMethods(true);
    m.setName("setName");
    m.setSig("java.lang.String");

    List methods = m.scanMethods(Dog.class);
    super.assertEquals(1, methods.size());
    m = new Matcher();
    m.setDeclaredMethods(true);
    m.setName("getName");
    m.setSig("");
    methods = m.scanMethods(Dog.class);
    super.assertEquals(1, methods.size());
  }

  public void testNoSig() throws Exception {
    Matcher m = new Matcher();
    m.setDeclaredMethods(true);
    m.setName("*Name");

    List methods = m.scanMethods(Dog.class);
    super.assertEquals(2, methods.size());
  }

  public void testNoSigNoMethod() throws Exception {
    Matcher m = new Matcher();
    m.setDeclaredMethods(true);

    List methods = m.scanMethods(Dog.class);
    super.assertEquals(9, methods.size());
  }

  public void testPublicMethods() throws Exception {
    Matcher m = new Matcher();
    m.setDeclaredMethods(true);
    m.setName("setAttribute*");
    m.setVisibility("public");

    List methods = m.scanMethods(Dog.class);
    super.assertEquals(1, methods.size());
  }

  public void testProtectedMethods() throws Exception {
    Matcher m = new Matcher();
    m.setDeclaredMethods(true);
    m.setName("setAttribute*");
    m.setVisibility("protected");

    List methods = m.scanMethods(Dog.class);
    super.assertEquals(1, methods.size());
  }

  public void testPrivateMethods() throws Exception {
    Matcher m = new Matcher();
    m.setDeclaredMethods(true);
    m.setName("setAttribute*");
    m.setVisibility("private");

    List methods = m.scanMethods(Dog.class);
    super.assertEquals(1, methods.size());
  }

  public void testPublicProtectedMethods() throws Exception {
    Matcher m = new Matcher();
    m.setDeclaredMethods(true);
    m.setName("setAttribute*");
    m.setVisibility("public, protected");

    List methods = m.scanMethods(Dog.class);
    super.assertEquals(2, methods.size());
  }

  public void testIncludeNoExclude() throws Exception {
    Matcher m = new Matcher();
    m.setName("*");
    m.setVisibility("public, protected");
    m.setIncludes("**.*Dog");

    List methods = m.scanMethods(Dog.class);
    super.assertEquals(7, methods.size());
  }

  public void testIncludeExclude() throws Exception {
    Matcher m = new Matcher();
    m.setName("*");
    m.setVisibility("public, protected");
    m.setIncludes("**.*Dog, java.lang.Object");
    m.setExcludes("**.Object");

    List methods = m.scanMethods(Dog.class);
    super.assertEquals(7, methods.size());
  }

  public void testExcludeNoInclude() throws Exception {
    Matcher m = new Matcher();
    m.setName("*");
    m.setVisibility("public, protected");
    m.setExcludes("**.Object");

    List methods = m.scanMethods(Dog.class);
    super.assertEquals(8, methods.size());
  }

  public static class Dog extends Animal {
    public String getName() {
      return null;
    }

    public void setName(String name) {
    }

    public int getAge() {
      return 0;
    }

    public void setAge(int age) {
    }

    public String getColor() {
      return null;
    }

    public void setColor(String color) {
    }

    protected void setAttributeProtected(int i) {
    }

    private void setAttributePrivate(int i) {
    }

    public void setAttributePublic(int i) {
    }
  }

  public static class Animal {
    public int compareTo(Object arg0) {
      return 0;
    }
  }
}
