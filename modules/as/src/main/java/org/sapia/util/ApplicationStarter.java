package org.sapia.util;


// Import of Sun's JDK classes
// ---------------------------
import java.io.File;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import java.util.ArrayList;
import java.util.StringTokenizer;


/**
 * <P>
 * The application starter allows to execute the <CODE>main(String[])</CODE> method of a
 * java class from a child classloader of the system classloader. This delegation of the
 * execution of a java class to a child classloader allows an application to be independant
 * in the classes that it loads (because they are not loaded by the system classloader)
 * and give the opportunity to other java application to be loaded by other child classloader
 * of the system classloader without any clash of class name.
 * </P>
 * <P>
 * The application starter can be called with different options:
 * <UL>
 *   <LI><B>-ashelp</B> This option prints a help message on the standard output and exit.</LI>
 *   <LI><B>-asdebug</B> This options will print on the standard output some debugging
 *       information about the execution of the application starter.</LI>
 *   <LI><B>-ascp {resources}</B> This option defines the resources that will be used by
 *       the child classloader to execute the main class. Each resource can be an absolute
 *       file name or a URL and each resource need to be seperated by the system path separator.</LI>
 *   <LI><B>class</B> The name of the java class to execute from the child classloader.</LI>
 *   <LI><B>arguments...</B> The list of application arguments that is passed to the execute class.</LI>
 * </UL>
 * </P>
 * <P>
 * Here is two examples of the usage of the application starter. The first example will show
 * the online help message of the application starter and exit. The second example would create
 * a new classloader that would contain the file C:\test.jar as only resource, create a new thread
 * and, from that new thread, call the method <CODE>main(String[])</CODE> on the class MyMainClass
 * passing the two arguments 'foo' and 'bar'.
 * <UL>
 *   <LI><CODE>java org.sapia.util.ApplicationStarter -ashelp</CODE></LI>
 *   <LI><CODE>java org.sapia.util.ApplicationStarter -asdebug -ascp C:\test.jar MyMainClass foo bar</CODE></LI>
 * </UL>
 * </P>
 * <P>
 * Finally the application starter can be called programmatically using the <CODE>start</CODE> method.
 * </P>
 *
 * @author Jean-Cedric Desrochers
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class ApplicationStarter implements Runnable {
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////  INSTANCE ATTRIBUTES  /////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /** The main class to execute. */
  private Class _theMainClass;

  /** The array of arguments to pass to the main method. */
  private String[] _theArguments;

  /////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////  CONSTRUCTORS  /////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Creates a new ApplicationStarter with the passed in arguments.
   *
   * @param aMainClass The class on which to call the <CODE>main()</CODE> method.
   * @param someArguments The array of argument to pass to the main class.
   */
  protected ApplicationStarter(Class aMainClass, String[] someArguments) {
    _theMainClass   = aMainClass;
    _theArguments   = someArguments;
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////  STATIC METHODS  ////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Main method of the ApplicationStarter class. It first parses the arguments passed in
   * and then it creates a classloader for the main class to execute. Finally, it starts
   * of a new thread that will call the <CODE>main(String[])</CODE> method of the class.
   *
   * @param args The arguments that define the options of the application starter.
   */
  public static void main(String[] args) {
    try {
      boolean  isDebug                = false;
      String   anApplicationClasspath = null;
      String   aClassName             = null;
      String[] someArguments          = new String[0];

      // Parse the arguments
      for (int i = 0; i < args.length; i++) {
        if (args[i].equals("-ashelp")) {
          usage();
          System.exit(0);
        } else if (args[i].equals("-asdebug")) {
          isDebug = true;
        } else if (args[i].equals("-ascp")) {
          anApplicationClasspath = args[++i];
        } else if (aClassName == null) {
          aClassName = args[i];
        } else {
          if (someArguments.length == 0) {
            someArguments = new String[args.length - i];
          }

          someArguments[someArguments.length - (args.length - i)] = args[i];
        }
      }

      start(anApplicationClasspath, aClassName, someArguments, isDebug);
    } catch (ApplicationStarterException ase) {
      ase.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * This start method allow the ApplicationStarter to start a new application programmatically
   * passing in the required arguments.
   *
   * @param aClasspath The list of resources that defines the classpath use to start the application.
   *        Each resource of the classpath must be seperated by the system path seperator defined by
   *        the <CODE>path.separator</CODE> system proprety and by the static attribute
   *        <CODE>java.io.File.pathSeparator</CODE>.
   * @param aClassName The qualified name of the class that contains the <CODE>main()</CODE> method to start.
   * @param someArguments The arguments that would be passed to the <CODE>main()</CODE> method of the class.
   * @param isDebug Indicates of it start the application in debug mode or not.
   * @exception ApplicationStarterException If an error occurs while starting the application.
   */
  public static void start(String aClasspath, String aClassName,
    String[] someArguments, boolean isDebug) throws ApplicationStarterException {
    try {
      // Logging the configuration
      if (isDebug) {
        logState(aClasspath, aClassName, someArguments);
      }

      // Create a class loader for the classpath
      ClassLoader aClassLoader = createClassLoaderFor(aClasspath, isDebug);

      // Logging the classloader
      if (isDebug) {
        System.out.println("\nCreated the classloader " + aClassLoader);
      }

      // Loading the main class and invoke the main class
      Class              aMainClass = aClassLoader.loadClass(aClassName);
      ApplicationStarter aStarter = new ApplicationStarter(aMainClass,
          someArguments);

      // Create a new thread instance and start it
      Thread aThread = new Thread(aStarter);
      aThread.setName(aClassName.substring(aClassName.lastIndexOf(".") + 1));
      aThread.setContextClassLoader(aClassLoader);
      aThread.start();
    } catch (ClassNotFoundException cnfe) {
      if (!isDebug) {
        logState(aClasspath, aClassName, someArguments);
      }

      StringBuffer aMessage = new StringBuffer();
      aMessage.append("Unable to load the class ").append(aClassName)
              .append(" - ").append(cnfe.getMessage());
      throw new ApplicationStarterException(aMessage.toString(), cnfe);
    } catch (RuntimeException re) {
      if (!isDebug) {
        logState(aClasspath, aClassName, someArguments);
      }

      StringBuffer aMessage = new StringBuffer();
      aMessage.append("System error starting the class").append(aClassName);
      throw new ApplicationStarterException(aMessage.toString(), re);
    }
  }

  /**
   * Prints out to the standard output the options passed to this application starter.
   *
   * @param anApplicationClasspath The classpath to use to load the main class.
   * @param aClassName The name of the main class to execute.
   * @param someArguments The application arguments to pass to the main class in
   *        the <CODE>main(String[])</CODE> method.
   */
  private static void logState(String anApplicationClasspath,
    String aClassName, String[] someArguments) {
    StringBuffer aBuffer = new StringBuffer(
        "\nSAPIA Application Starter 1.0\n=============================");
    aBuffer.append("\nStarting application with this configuration:");
    aBuffer.append("\n  appClasspath: ").append(anApplicationClasspath)
           .append("\n  className   : ").append(aClassName);

    if (aClassName != null) {
      aBuffer.append("\n  threadName  : ").append(aClassName.substring(aClassName.lastIndexOf(
            ".") + 1));
    }

    aBuffer.append("\n  appArguments: ");

    if (someArguments.length == 0) {
      aBuffer.append("[]");
    } else {
      for (int i = 0; i < someArguments.length; i++) {
        aBuffer.append("[").append(someArguments[i]).append("] ");
      }
    }

    System.out.println(aBuffer.toString());
  }

  /**
   * Prints out on the standard output the usage of the main method of this class.
   */
  public static void usage() {
    StringBuffer aBuffer = new StringBuffer("SAPIA Application Starter 1.0\n");
    aBuffer.append(
      "Usage: java org.sapia.util.ApplicationStarter [options] {class} [args...]\n")
           .append("Options:\n").append("    -ashelp\t\tprint this message\n")
           .append("    -asdebug\t\tprint debugging information\n")
           .append("    -ascp <resources>\tthe classpath of the classloader that will execute the main class\n")
           .append("Example: java org.sapia.util.ApplicationStarter -ashelp\n")
           .append("Example: java org.sapia.util.ApplicationStarter -asdebug -ascp test.jar MyMainClass foo bar\n");

    System.out.println(aBuffer.toString());
  }

  /**
   * Creates a class loader using the resources specified in the classpath string passed in. If the
   * classpath passed in is null or empty, it returns the system classloader. Otherwise it returns
   * the created class loader with the system class loader as its parent.
   *
   * @param aClasspath The classpath string (can be null);
   * @return The created class loader or the system class loader if the classpath passed in is null or empty.
   */
  private static ClassLoader createClassLoaderFor(String aClasspath,
    boolean isDebug) {
    ClassLoader aClassLoader;

    if ((aClasspath == null) || (aClasspath.length() == 0)) {
      aClassLoader = ClassLoader.getSystemClassLoader();
    } else {
      try {
        ArrayList someURLs = new ArrayList();
        String    aHomeDir = System.getProperty("user.dir");

        for (StringTokenizer st = new StringTokenizer(aClasspath,
              File.pathSeparator); st.hasMoreTokens();) {
          String aToken = st.nextToken();

          if (!aToken.startsWith("file:/") || !aToken.startsWith("http:/")) {
            File aFile = new File(aToken);

            if (aFile.exists()) {
              aToken = aFile.toURL().toExternalForm();
            } else {
              aFile = new File(aHomeDir, aToken);

              if (aFile.exists()) {
                aToken = aFile.toURL().toExternalForm();
              }
            }
          }

          URL anURL = new URL(aToken);
          someURLs.add(anURL);
        }

        if (isDebug) {
          for (int i = 0; i < someURLs.size(); i++) {
            System.out.println(((URL) someURLs.get(i)).toExternalForm());
          }
        }

        aClassLoader = URLClassLoader.newInstance((URL[]) someURLs.toArray(
              new URL[0]), ClassLoader.getSystemClassLoader());
      } catch (MalformedURLException mue) {
        throw new RuntimeException(
          "Unable to create a class loader using the classpath: " + aClasspath);
      }
    }

    return aClassLoader;
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////  INTERACE IMPLEMENTATION  ///////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Run method of the Runnable interface that calls the main method of the main class.
   */
  public void run() {
    try {
      Method aMainMethod = _theMainClass.getDeclaredMethod("main",
          new Class[] { String[].class });
      aMainMethod.invoke(_theMainClass, new Object[] { _theArguments });
    } catch (NoSuchMethodException nsme) {
      StringBuffer aBuffer = new StringBuffer();
      aBuffer.append("Method main(String[]) not found on class ")
             .append(_theMainClass.getName()).append(" - ").append(nsme.getMessage());
      throw new RuntimeException(aBuffer.toString());
    } catch (InvocationTargetException ite) {
      StringBuffer aBuffer = new StringBuffer();
      aBuffer.append("Error calling main method on class ")
             .append(_theMainClass.getName()).append(" - ");

      if (ite.getTargetException() != null) {
        aBuffer.append(ite.getTargetException().getMessage());
      } else {
        aBuffer.append(ite.getMessage());
      }

      throw new RuntimeException(aBuffer.toString());
    } catch (IllegalAccessException iae) {
      StringBuffer aBuffer = new StringBuffer();
      aBuffer.append(
        "main(String[]) method was found but is not accessible on class ")
             .append(_theMainClass.getName()).append(" - ").append(iae.getMessage());
      throw new RuntimeException(aBuffer.toString());
    }
  }
}
