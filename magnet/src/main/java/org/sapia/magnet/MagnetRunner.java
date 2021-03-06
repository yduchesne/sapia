package org.sapia.magnet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.sapia.corus.interop.client.InteropClient;
import org.sapia.corus.interop.client.ProtocolAlreadySetException;
import org.sapia.corus.interop.http.HttpProtocol;
import org.sapia.magnet.domain.Launcher;
import org.sapia.magnet.domain.Magnet;


/**
 * Main class that executes a magnet file. A magnet can be executed either using the <code>magnet</code>
 * script at the command line or programatically by calling the <code>runFile()</code> method.<p>
 * 
 * The execution of a magnet is seperated into three phases:
 * <ol>
 *   <li><b>Parsing:</b> parse the magnet xml file to generate an object representation of the magnet definition.</li>
 *   <li><b>Rendering:</b> pre-processing of the magnet objects that includes variable interpolation, script execution
 *                         and ordering definition of the overloaded entities.</li> 
 *   <li><b>Execution:</b> Execute each launcher of every magnet, wainting between each launcher if a wait time is
 *                         specified in a launcher.</li>
 * </ol>
 *
 * @author Jean-Cedric Desrochers
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html" target="sapia-license">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class MagnetRunner {

  /////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////  CLASS ATTRIBUTES  ///////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /** Defines the default magnet configuration filename. */
  public static final String DEFAULT_MAGNET_FILENAME = "magnet.xml";

  /** Defines the logger instance for this class. */
  private static Logger _theLogger;
  
  /** The log level used by the runner. */
  private static Level _theLogLevel;
  
  /** The filename in which to log. */
  private static String _theLogFilename;

  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////  STATIC METHODS  ////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Main method to execute this MagnetRunner. See the class javadoc for
   * details on the options and arguments.
   *
   * @param args The options and arguments to use to run the magnet.
   */
  public static void main(String[] args) {
    try {
      String aMagnetFilename = DEFAULT_MAGNET_FILENAME;
      String aProfile = "";
      ArrayList<String> otherArgs = new ArrayList<String>();
      _theLogLevel = Level.OFF;

      if (args.length == 0) {
        version();
        usage();
        return;
        
      } else {
        for (int i = 0; i < args.length; i++) {
          String anArgument = args[i];

          if (anArgument.equals("-help") || anArgument.equals("-h")) {
            usage();
            return;

          } else if (anArgument.equals("-version") || anArgument.equals("-v")) {
            version();
            return;

          } else if (anArgument.equals("-magnetfile") ||
                  anArgument.equals("-file") || anArgument.equals("-f")) {
            aMagnetFilename = args[++i];

          } else if (anArgument.equals("-logfile") ||
                  anArgument.equals("-log")) {
            _theLogFilename = args[++i];

          } else if (anArgument.equals("-debug")) {
            _theLogLevel = Level.DEBUG;

          } else if (anArgument.equals("-info")) {
            _theLogLevel = Level.INFO;

          } else if (anArgument.equals("-warn")) {
            _theLogLevel = Level.WARN;

          } else if (anArgument.equals("-profile") || anArgument.equals("-p")) {
            aProfile = args[++i];

          } else if (anArgument.startsWith("-")) {
            System.out.println("Unknown option: " + anArgument);
            usage();
            return;
            
          } else {
            otherArgs.add(anArgument);
          }
        }
        
        // Process the other arguments to be used as ${magnet.args.1}
        int i = 0;
        StringBuffer allArgs = new StringBuffer(); 
        for (Iterator<String> it = otherArgs.iterator(); it.hasNext(); ) {
          String anArg = it.next();
          System.setProperty("magnet.args." + ++i, anArg);
          allArgs.append(anArg).append(" ");
        }
        
        if (i > 0) {
          System.setProperty("magnet.args.*", allArgs.toString());
        }
      }
      
      // Run the magnet
      runFile(aMagnetFilename, aProfile);
      
    } catch (MagnetException me) {
      Log.error(Log.extactMessage(me));
      System.exit(1);

    } catch (RuntimeException re) {
      Log.error("System error running the magnet - " + re.getMessage());
      System.exit(1);
    }
  }

  /**
   * Runs the magnet specified by the input stream for the profile passed in.
   *
   * @param aMagnetStream The magnet configuration input stream.
   * @param aProfile The profile of the magnet to execute.
   * @exception MagnetException If an error occurs running the magnet input stream.
   */
  public static void run(InputStream aMagnetStream, String aProfile) throws MagnetException {
    // Configure log4j
    PatternLayout aLayout = new PatternLayout("%r [%t] %p %c %x - %m%n");
    Appender anAppender = null;

    if (_theLogFilename == null) {
      anAppender = new ConsoleAppender(aLayout);
    } else {
      try {
        anAppender = new FileAppender(aLayout, _theLogFilename);
      } catch (IOException ioe) {
        Log.warn(ioe.getMessage());
        Log.warn("Unable to set logfile to '" + _theLogFilename + "' - setting log to the console");
        anAppender = new ConsoleAppender(aLayout);
      }
    }
    Logger aRootLogger = LogManager.getLogger("org.sapia");
    aRootLogger.setAdditivity(false);
    aRootLogger.addAppender(anAppender);
    aRootLogger.setLevel(_theLogLevel);
    _theLogger = Logger.getLogger(MagnetRunner.class);

    // Add the executed profile into the system property
    System.setProperty("magnet.profile.name", aProfile);
    
    // Start the interop process
    try {
      Log.info("MAGNET - Starting Corus interop module...");
      InteropClient.getInstance().setProtocol(new HttpProtocol());
    } catch (ProtocolAlreadySetException pae){
      Log.warn("Corus interop link already initialized " + pae.getMessage());
    } catch (MalformedURLException mue) {
      Log.error("Unable to start the Corus interop module - " + mue.getMessage(), mue);
    } catch (RuntimeException re) {
      Log.error("System error starting the Corus interop module - " + re.getMessage(), re);
    }

    // Parse the magnet input stream
    if (_theLogger.isInfoEnabled()) {
      _theLogger.info("Parsing the magnet input stream...");
    }
    MagnetParser aMagnetParser = new MagnetParser();
    List<Magnet> someMagnets = aMagnetParser.parse(aMagnetStream);

    // Render the magnet
    if (_theLogger.isInfoEnabled()) {
      _theLogger.info("Rendering the magnets...");
    }
    MagnetRenderer aMagnetRenderer = new MagnetRenderer();
    aMagnetRenderer.render(someMagnets, aProfile);

    // Logging the system properties
    _theLogger.info(Log.formatProperties(System.getProperties()));

    // Execute the launchers of each magnet
    for (Iterator<Magnet> it = someMagnets.iterator(); it.hasNext(); ) {
      Magnet aMagnet = it.next();
      Log.info("\n----------------------------------------------\nMAGNET - Executing the magnet " + aMagnet.getName());

      for (Iterator<Launcher> someLaunchers = aMagnet.getLaunchers().iterator(); someLaunchers.hasNext(); ) {
        Launcher aLauncher = someLaunchers.next();
        aLauncher.execute(aProfile);
        
        if (aLauncher.getLaunchHandler().geWaitTime() > 0) {
          try {
            if (aLauncher.getLaunchHandler().geWaitTime() > 1) {
              Log.info("MAGNET - Waiting for " + (aLauncher.getLaunchHandler().geWaitTime()/1000f) + " second...");
            } else {
              Log.info("MAGNET - Waiting for " + (aLauncher.getLaunchHandler().geWaitTime()/1000f) + " seconds...");
            }
            Thread.sleep(aLauncher.getLaunchHandler().geWaitTime());
          } catch (InterruptedException ie) {
          }
        }
      }
    }
    Log.info("MAGNET - All launchers executed");
    
  }

  /**
   * Runs the magnet specified by the configuration filename for the profile
   * passed in.
   *
   * @param aMagnetFilename The magnet configuration filename.
   * @param aProfile The profile of the magnet to execute.
   * @exception MagnetException If an error occurs running the magnet file.
   */
  public static void runFile(String aMagnetFilename, String aProfile) throws MagnetException {
    File aMagnetFile = new File(aMagnetFilename);

    if (!aMagnetFile.exists()) {
      throw new MagnetException("The magnet file " + aMagnetFilename + " does not exist.");
    } else if (aMagnetFile.isDirectory()) {
      throw new MagnetException("The magnet file " + aMagnetFilename + " is a directory.");
    }

    try {
      StringBuffer aBuffer = new StringBuffer();
      aBuffer.append("MAGNET - Starting magnet with file [").append(aMagnetFile);

      if (aProfile == null || aProfile.length()== 0) {
         aBuffer.append("] using the default profile...");
      } else {
         aBuffer.append("] using the profile '").append(aProfile).append("'");
      }
      version();
      Log.info(aBuffer.toString());

      FileInputStream anInputStream = new FileInputStream(aMagnetFile);
      run(anInputStream, aProfile);

    } catch (FileNotFoundException fnfe) {
      throw new MagnetException("Could not find magnet file " + aMagnetFilename);
    }
  }

  /**
   * Prints out on the standard output the usage of the main method of this class.
   */
  public static void usage() {
    StringBuffer aBuffer = new StringBuffer("\n");
    aBuffer.append("Usage: magnet [vm options...] [options...] [args...]\n").
            append("VM Options:\n").
            append("    Options that affect the Java VM that will be started. The Vm options\n").
            append("    can be one or many of the following:\n").
            append("    \n").
            append("    -javahome <path>\n").
            append("            to define the home of the java runtime this option overrides\n").
            append("            the JAVA_HOME environement variable\n").
            append("    \n").
            append("    -client to start java with the 'client' VM\n").
            append("    \n").
            append("    -server to start java with the 'server' VM\n").
            append("    \n").
            append("    -X<option>\n").
            append("            to start java with non-standard options\n").
            append("    \n").
            append("    -D<name>=<value>\n").
            append("            to set a system property\n").
            append("\n").
            append("Options:\n").
            append("    Options that will define the behavior of the magnet runtime on the Java\n").
            append("    VM is started. The magnet options can be one or more of the following:\n").
            append("    \n").
            append("    -help | -h\n").
            append("            print this message\n").
            append("    \n").
            append("    -version\n").
            append("            print the version information and exit\n").
            append("    \n").
            append("    -logfile | -log <file>\n").
            append("            use the given file to log\n").
            append("    \n").
            append("    -debug  print debugging information\n").
            append("    -info   print information that can help to diagnose\n").
            append("    -warn   print warning and error information\n").
            append("    \n").
            append("    -file | -f <file>\n").
            append("            use the given magnet configuration file\n").
            append("    \n").
            append("    -profile | -p <name>\n").
            append("            the name of the profile to execute in the magnet. If the profile\n").
            append("            is not provided, only the launchers with a default profile are\n").
            append("            executed.\n").
            append("\n").
            append("Args:\n").
            append("    The application arguments that can be passed to magnet at every execution.\n").
            append("    Each argument will be assigned to a system property of the name magnet.args.n\n").
            append("    where the 'n' is replaced by the number of the argument in the list. The\n").
            append("    additionnal system property magnet.args.* contains the entire list of arguments\n").
            append("    as a whole String.\n").
            append("\n").
            append("Examples:\n").
            append("\n").
            append("    To start a server VM that will run the magnet TimeServer.xml using the test profile: \n").
            append("            magnet -server -file TimeServer.xml -profile test\n").
            append("    \n").
            append("    To start a VM using an alternate java home:\n").
            append("            magnet -javahome /opt/jdk1.4 -file TimeServer.xml -profile test\n").
            append("    \n").
            append("    To start a VM with specific memory setting and a VM argument:\n").
            append("            magnet -Xms8m -Xmx256m -Dfoo=bar -f TransactionServer.xml -p prod\n").
            append("    \n").
            append("    To run a magnet file to which application arguments '10', '50' and '125' are passed:\n").
            append("            magnet -f MetricConversion.xml -p weight 10 50 125\n").
            append("\n\n");    

    Log.info(aBuffer.toString());
  }

  /**
   * Prints out on the standard output the version information of this class.
   */
  public static void version() {
    StringBuffer aBuffer = new StringBuffer("\n");
    aBuffer.append("Sapia Magnet version 2.0");

    Log.info(aBuffer.toString());
  }
}
