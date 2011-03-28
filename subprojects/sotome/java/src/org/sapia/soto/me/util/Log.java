/**
 * 
 */
package org.sapia.soto.me.util;

import javolution.text.TextBuilder;

import org.sapia.soto.me.MIDletEnv;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public class Log {

  public static final String PROPERTY_SOTO_DEBUG = "soto.debug";
  
  /** Indicates is the debug is enabled or not. */
  private static boolean _isDebugEnabled = false;
  
  private static long _referenceTime;

  /**
   * Initializes the log according to the properties taken out of the MIDlet environment passed in.
   * 
   * @param aMIDletEnv The MIDlet environment.
   */
  public static void initialize(MIDletEnv aMIDletEnv) {
    String sotoDebugValue = aMIDletEnv.getProperty(PROPERTY_SOTO_DEBUG);
    _isDebugEnabled = (sotoDebugValue != null && sotoDebugValue.toLowerCase().equals("true"));
    _referenceTime = System.currentTimeMillis();
  }
  
  public static boolean isDebug() {
    return _isDebugEnabled;
  }
  
  public static void debug(String aMessage) {
    if (_isDebugEnabled) {
      TextBuilder text = new TextBuilder();
      formatText(text, "DEBUG", aMessage);
      System.out.println(text.toString());
      System.out.flush();
    }
  }
  
  public static void debug(String aModuleName, String aMessage) {
    if (_isDebugEnabled) {
      TextBuilder text = new TextBuilder();
      formatText(text, "DEBUG", aModuleName, aMessage);
      System.out.println(text.toString());
      System.out.flush();
    }
  }
  
  public static void info(String aMessage) {
    TextBuilder text = new TextBuilder();
    formatText(text, "INFO", aMessage);
    System.out.println(text.toString());
    System.out.flush();
  }

  public static void info(String aModuleName, String aMessage) {
    TextBuilder text = new TextBuilder();
    formatText(text, "INFO", aModuleName, aMessage);
    System.out.println(text.toString());
    System.out.flush();
  }
  
  public static void warn(String aMessage) {
    TextBuilder text = new TextBuilder();
    formatText(text, "WARN", aMessage);
    System.out.println(text.toString());
    System.out.flush();
  }

  public static void warn(String aModuleName, String aMessage) {
    TextBuilder text = new TextBuilder();
    formatText(text, "WARN", aModuleName, aMessage);
    System.out.println(text.toString());
    System.out.flush();
  }
  
  public static void warn(String aMessage, Throwable anError) {
    TextBuilder text = new TextBuilder();
    formatText(text, "WARN", aMessage);
    System.out.println(text.toString());
    System.out.flush();
  }

  public static void warn(String aModuleName, String aMessage, Throwable anError) {
    TextBuilder text = new TextBuilder();
    formatText(text, "WARN", aModuleName, aMessage);
    System.out.println(text.toString());
    System.out.flush();
  }
  
  public static void error(String aMessage) {
    TextBuilder text = new TextBuilder();
    formatText(text, "ERROR", aMessage);
    System.err.println(text.toString());
    System.err.flush();
  }
  
  public static void error(String aModuleName, String aMessage) {
    TextBuilder text = new TextBuilder();
    formatText(text, "ERROR", aModuleName, aMessage);
    System.err.println(text.toString());
    System.err.flush();
  }

  public static void error(String aMessage, Throwable anError) {
    error(aMessage);
    if (anError != null) {
      anError.printStackTrace();
    }
  }

  public static void error(String aModuleName, String aMessage, Throwable anError) {
    error(aModuleName, aMessage);
    if (anError != null) {
      anError.printStackTrace();
    }
  }
  
  public static void fatal(String aMessage) {
    TextBuilder text = new TextBuilder();
    formatText(text, "FATAL", aMessage);
    System.err.println(text.toString());
    System.err.flush();
  }
  
  public static void fatal(String aModuleName, String aMessage) {
    TextBuilder text = new TextBuilder();
    formatText(text, "FATAL", aModuleName, aMessage);
    System.err.println(text.toString());
    System.err.flush();
  }
  
  public static void fatal(String aMessage, Throwable anError) {
    fatal(aMessage);
    if (anError != null) {
      anError.printStackTrace();
    }
  }
  
  public static void fatal(String aModuleName, String aMessage, Throwable anError) {
    fatal(aModuleName, aMessage);
    if (anError != null) {
      anError.printStackTrace();
    }
  }

  /**
   * Formats a text to be logged.
   * 
   * @param aText The text builder in which to put text.
   * @param aLevel The level of severity of the message.
   * @param aMessage The message.
   */
  private static void formatText(TextBuilder aText, String aLevel, String aMessage) {
    aText.append(System.currentTimeMillis() - _referenceTime).
          append(" [").
          append(Thread.currentThread().getName()).
          append("] * ").append(aLevel).append(" * ").
          append(" SotoMe.Log - ").
          append(aMessage);
  }

  /**
   * Formats a text to be logged.
   * 
   * @param aText The text builder in which to put text.
   * @param aLevel The level of severity of the message.
   * @param aModuleName The module name that is logging a message.
   * @param aMessage The message.
   */
  private static void formatText(TextBuilder aText, String aLevel, String aModuleName, String aMessage) {
    aText.append(System.currentTimeMillis() - _referenceTime).
          append(" [").
          append(Thread.currentThread().getName()).
          append("] * ").append(aLevel).append(" * ").
          append(" SotoMe.").append(aModuleName).append(" - ").
          append(aMessage);
  }
}
