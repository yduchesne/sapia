package org.sapia.util.xml.confix;


// Import of Sapia's utility classes
// ---------------------------------
import org.sapia.util.CompositeRuntimeException;

// Import of Sun's JDK classes
// ---------------------------
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.lang.reflect.Constructor;

import java.util.Properties;


/**
 * A factory that instantiates <code>ConfixProcessorIF</code> instances. The
 * class of the Confix processor to use must be specified through the following
 * property: <code>org.sapia.xml.ConfixProcessor</code>.
 * <p>
 * The property is searched:
 *
 * <ol>
 *   <li>In the system properties.
 *   <li>In a "sapia.properties" file in $JAVA_HOME/lib.
 *   <li>In a "sapia.properties" file that is loaded as a resource.
 * </ol>
 *
 * If none of the methods above worked, the factory return a <code>SAXProcessor</code>.
 *
 * @see org.sapia.util.xml.confix.SAXProcessor
 * @author JC Desrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class ConfixProcessorFactory {
  /////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////  CLASS ATTRIBUTES  ///////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /** Defines the system property that defines the processor class name. */
  public static final String PROCESSOR_CLASS_PROPERTY = "org.sapia.xml.ConfixProcessor";

  /** Defines the properties filename that contains the processor class name. */
  public static final String PROCESSOR_CLASS_FILENAME = "sapia.properties";

  /** Defines the resource name that contains the processor class name. */
  public static final String PROCESSOR_CLASS_RESOURCE = "META-INF/services/" +
    PROCESSOR_CLASS_PROPERTY;

  /** Defines the default Confix processor class. */
  public static final String DEFAULT_PROCESSOR_CLASS = "org.sapia.util.xml.confix.SAXProcessor";

  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////  INSTANCE ATTRIBUTES  /////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /** The class of the confix processor of this factory. */
  private Class _theProcessorClass;

  /////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////  CONSTRUCTORS  /////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Creates a new ConfixProcessorFactory instance.
   */
  protected ConfixProcessorFactory(Class aClass) {
    _theProcessorClass = aClass;
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////  STATIC METHODS  ////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Factory method that creates a new <CODE>ConfixProcessorIF</CODE> instance.
   *
   * @return The new <CODE>ConfixProcessorIF</CODE> instance.
   */
  public static ConfixProcessorFactory newFactory() {
    Class aProcessorClass = retrieveClassFromSystemProperties();

    if (aProcessorClass == null) {
      aProcessorClass = retrieveClassFromJavaHome();

      if (aProcessorClass == null) {
        aProcessorClass = retrieveClassFromResource();

        if (aProcessorClass == null) {
          aProcessorClass = loadClassFromName(DEFAULT_PROCESSOR_CLASS);
        }
      }
    }

    return new ConfixProcessorFactory(aProcessorClass);
  }

  /**
   * Returns the <CODE>Class</CODE> object of the Confix processor
   * to use from the system properties.
   *
   * @return The found class object or null if no class is found or if
   *         the class passed in does not implement the interface
   *         <CODE>ConfixProcessorIF</CODE>
   */
  private static Class retrieveClassFromSystemProperties() {
    String aClassName = System.getProperty(PROCESSOR_CLASS_PROPERTY);

    return loadClassFromName(aClassName);
  }

  /**
   * Returns the <CODE>Class</CODE> object of the Confix processor
   * to use from the file <CODE>$JAVA_HOME/jre/lib/sapia.properties</CODE>.
   *
   * @return The found class object or null if no class is found or if
   *         the class passed in does not implement the interface
   *         <CODE>ConfixProcessorIF</CODE>
   */
  private static Class retrieveClassFromJavaHome() {
    StringBuffer aFileName = new StringBuffer(System.getProperty("java.home"));
    aFileName.append(File.separator).append("lib").append(File.separator)
             .append(PROCESSOR_CLASS_FILENAME);

    File aFile = new File(aFileName.toString());

    if (aFile.exists()) {
      FileInputStream anInput = null;

      try {
        Properties someProperties = new Properties();
        anInput = new FileInputStream(aFile);
        someProperties.load(anInput);

        String aClassName = someProperties.getProperty(PROCESSOR_CLASS_PROPERTY);

        return loadClassFromName(aClassName);
      } catch (IOException ioe) {
        StringBuffer aBuffer = new StringBuffer();
        aBuffer.append(
          "ConfixProcessorFactory: WARNING unable to read the file ").append(aFileName.toString());
        System.out.println(aBuffer.toString());

        return null;
      } finally {
        if (anInput != null) {
          try {
            anInput.close();
          } catch (IOException ioe) {
          }
        }
      }
    } else {
      return null;
    }
  }

  /**
   *
   */
  private static Class retrieveClassFromResource() {
    InputStream    anInput = null;
    BufferedReader aReader = null;

    try {
      anInput = Thread.currentThread().getContextClassLoader()
                      .getResourceAsStream(PROCESSOR_CLASS_RESOURCE);

      if (anInput != null) {
        try {
          aReader = new BufferedReader(new InputStreamReader(anInput, "UTF-8"));
        } catch (UnsupportedEncodingException uee) {
          aReader = new BufferedReader(new InputStreamReader(anInput));
        }

        String aClassName = aReader.readLine();

        return loadClassFromName(aClassName);
      } else {
        return null;
      }
    } catch (IOException ioe) {
      StringBuffer aBuffer = new StringBuffer();
      aBuffer.append(
        "ConfixProcessorFactory: WARNING unable to read the resource ").append(PROCESSOR_CLASS_RESOURCE);
      System.out.println(aBuffer.toString());

      return null;
    } finally {
      if (aReader != null) {
        try {
          aReader.close();
        } catch (IOException ioe) {
        }
      }

      if (anInput != null) {
        try {
          anInput.close();
        } catch (IOException ioe) {
        }
      }
    }
  }

  /**
   * Returns the <CODE>Class</CODE> object of the Confix processor
   * to use from the system properties.
   *
   * @return The found class object or null if no class is found or if
   *         the class passed in does not implement the interface
   *         <CODE>ConfixProcessorIF</CODE>
   */
  private static Class loadClassFromName(String aClassName) {
    if ((aClassName != null) && (aClassName.length() > 0)) {
      try {
        Class aClass = Thread.currentThread().getContextClassLoader().loadClass(aClassName);

        if (ConfixProcessorIF.class.isAssignableFrom(aClass)) {
          return aClass;
        } else {
          StringBuffer aBuffer = new StringBuffer();
          aBuffer.append("ConfixProcessorFactory: WARNING found the class ")
                 .append(aClassName).append(" but it is not a ConfixProcessorIF");
          System.out.println(aBuffer.toString());

          return null;
        }
      } catch (ClassNotFoundException cnfe) {
        StringBuffer aBuffer = new StringBuffer();
        aBuffer.append(
          "ConfixProcessorFactory: WARNING unable to load the class name ")
               .append(aClassName);
        System.out.println(aBuffer.toString());

        return null;
      }
    } else {
      return null;
    }
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////  MUTATOR METHODS  ///////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Factory method that creates a new <CODE>ConfixProcessorIF</CODE> instance.
   *
   * @return The new <CODE>ConfixProcessorIF</CODE> instance.
   */
  public ConfixProcessorIF createProcessor(ObjectFactoryIF aFactory) {
    try {
      Constructor aConstructor = _theProcessorClass.getConstructor(new Class[] {
            ObjectFactoryIF.class
          });

      return (ConfixProcessorIF) aConstructor.newInstance(new Object[] { aFactory });
    } catch (Exception e) {
      throw new CompositeRuntimeException("Unable to create a Confix processor",
        e);
    }
  }
}
