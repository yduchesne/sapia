package org.sapia.util.xml.idefix.test;

import java.lang.reflect.Method;

import org.apache.log4j.BasicConfigurator;
import org.sapia.util.xml.Namespace;
import org.sapia.util.xml.idefix.PatternNamespaceFactory;

import junit.framework.TestCase;
import junit.textui.TestRunner;


/**
 * 
 *
 * @author <a href="mailto:jc@sapia-oss.org">Jean-Cedric Desrochers</a>
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html" target="sapia-license">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class PatternNamespaceFactoryTest extends TestCase {

  static {
    BasicConfigurator.configure();
  }

  public static void main(String[] args) {
    TestRunner.run(PatternNamespaceFactoryTest.class);
  }

  /**
   * Creates a new PatternNamespaceFactory instance.
   */
  public PatternNamespaceFactoryTest(String aName) {
    super(aName);
  }


  /**
   * 
   */
  public void testSimple_ClassResolution() throws Exception {
    PatternNamespaceFactory aFactory = new PatternNamespaceFactory(); 
    Namespace aNamespace = new Namespace("http://schema.sapia-oss.org/junit", "JUNIT");
    aFactory.addNamespace(PatternNamespaceFactoryTest.class, aNamespace);
    
    Namespace anotherNamespace = aFactory.getNamespaceFor(PatternNamespaceFactoryTest.class);
    assertNotNull("The namespace found is null", anotherNamespace);
    assertEquals("The namespace found is invalid", aNamespace, anotherNamespace);
  }


  /**
   * 
   */
  public void testPackageFallback_ClassResolution() throws Exception {
    PatternNamespaceFactory aFactory = new PatternNamespaceFactory(); 
    Namespace aNamespace = new Namespace("http://schema.sapia-oss.org/junit", "JUNIT");
    aFactory.addNamespace(PatternNamespaceFactoryTest.class.getPackage(), aNamespace);
    
    Namespace anotherNamespace = aFactory.getNamespaceFor(PatternNamespaceFactoryTest.class);
    assertNotNull("The namespace found is null", anotherNamespace);
    assertEquals("The namespace found is invalid", aNamespace, anotherNamespace);
  }


  /**
   * 
   */
  public void testInvalid_ClassResolution() throws Exception {
    PatternNamespaceFactory aFactory = new PatternNamespaceFactory(); 
    Namespace aNamespace = new Namespace("http://schema.sapia-oss.org/junit", "JUNIT");
    aFactory.addNamespace(PatternNamespaceFactory.class, aNamespace);
    
    Namespace anotherNamespace = aFactory.getNamespaceFor(PatternNamespaceFactoryTest.class);
    assertNull("The namespace found is invalid, ir should be null", anotherNamespace);
  }


  /**
   * 
   */
  public void testSimple_MethodResolution() throws Exception {
    PatternNamespaceFactory aFactory = new PatternNamespaceFactory(); 
    Namespace aNamespace = new Namespace("http://schema.sapia-oss.org/junit", "JUNIT");
    Method aMethod = getClass().getMethod("testSimple_MethodResolution", new Class[0]);
    aFactory.addNamespace(aMethod, aNamespace);
    
    Namespace anotherNamespace = aFactory.getNamespaceFor(aMethod);
    assertNotNull("The namespace found is null", anotherNamespace);
    assertEquals("The namespace found is invalid", aNamespace, anotherNamespace);
  }


  /**
   * 
   */
  public void testClassCallback_MethodResolution() throws Exception {
    PatternNamespaceFactory aFactory = new PatternNamespaceFactory(); 
    Namespace aNamespace = new Namespace("http://schema.sapia-oss.org/junit", "JUNIT");
    aFactory.addNamespace(PatternNamespaceFactoryTest.class, aNamespace);
    
    Method aMethod = getClass().getMethod("testClassCallback_MethodResolution", new Class[0]);
    Namespace anotherNamespace = aFactory.getNamespaceFor(aMethod);
    assertNotNull("The namespace found is null", anotherNamespace);
    assertEquals("The namespace found is invalid", aNamespace, anotherNamespace);
  }


  /**
   * 
   */
  public void testPackageCallback_MethodResolution() throws Exception {
    PatternNamespaceFactory aFactory = new PatternNamespaceFactory(); 
    Namespace aNamespace = new Namespace("http://schema.sapia-oss.org/junit", "JUNIT");
    aFactory.addNamespace(PatternNamespaceFactoryTest.class.getPackage(), aNamespace);
    
    Method aMethod = getClass().getMethod("testPackageCallback_MethodResolution", new Class[0]);
    Namespace anotherNamespace = aFactory.getNamespaceFor(aMethod);
    assertNotNull("The namespace found is null", anotherNamespace);
    assertEquals("The namespace found is invalid", aNamespace, anotherNamespace);
  }


  /**
   * 
   */
  public void testInvalid_MethodResolution() throws Exception {
    PatternNamespaceFactory aFactory = new PatternNamespaceFactory(); 
    Namespace aNamespace = new Namespace("http://schema.sapia-oss.org/junit", "JUNIT");
    aFactory.addNamespace(PatternNamespaceFactory.class, aNamespace);
    
    Method aMethod = getClass().getMethod("testInvalid_MethodResolution", new Class[0]);
    Namespace anotherNamespace = aFactory.getNamespaceFor(aMethod);
    assertNull("The namespace found is invalid, ir should be null", anotherNamespace);
  }


  /**
   * 
   */
  public void testOverloaded_MethodResolution() throws Exception {
    Namespace aSapiaNamespace = new Namespace("http://schema.sapia-oss.org/junit", "SAPIA");
    Namespace aJunitNamespace = new Namespace("http://www.junit.org/junit", "JUNIT");

    PatternNamespaceFactory aFactory = new PatternNamespaceFactory(); 
    aFactory.addNamespace(PatternNamespaceFactoryTest.class, aSapiaNamespace);
    aFactory.addNamespace(TestCase.class, aJunitNamespace);
    aFactory.addNamespace(Object.class, PatternNamespaceFactory.DEFAULT_NAMESPACE);
    
    Method aMethod = getClass().getMethod("testOverloaded_MethodResolution", new Class[0]);
    Namespace anotherNamespace = aFactory.getNamespaceFor(aMethod);
    assertNotNull("The namespace found is null", anotherNamespace);
    assertEquals("The namespace found is invalid", aSapiaNamespace, anotherNamespace);

    aMethod = getClass().getMethod("runBare", new Class[0]);
    anotherNamespace = aFactory.getNamespaceFor(aMethod);
    assertNotNull("The namespace found is null", anotherNamespace);
    assertEquals("The namespace found is invalid", aJunitNamespace, anotherNamespace);

    aMethod = getClass().getMethod("hashCode", new Class[0]);
    anotherNamespace = aFactory.getNamespaceFor(aMethod);
    assertNotNull("The namespace found is null", anotherNamespace);
    assertEquals("The namespace found is invalid", PatternNamespaceFactory.DEFAULT_NAMESPACE, anotherNamespace);
  }
}
