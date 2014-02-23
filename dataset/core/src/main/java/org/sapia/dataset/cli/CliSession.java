package org.sapia.dataset.cli;

import groovy.lang.GroovyShell;

import org.sapia.dataset.io.ConsoleOutput;

/**
 * Specifies a command-line session.
 * 
 * @author yduchesne
 *
 */
public interface CliSession {

  /**
   * Terminates this session.
   */
  public void exit();
  
  /**
   * @return this instance's {@link ConsoleOutput}.
   */
  public ConsoleOutput getOutput();
  
  /**
   * @return this instance's {@link GroovyShell}.
   */
  public GroovyShell getShell();
  
  /**
   * @param msg displays the given message, preceding it with the prompt symbol.
   */
  public void message(String msg);
  
  /**
   * @return this instance's error buffer.
   */
  public ErrorBuffer getErrorBuffer();
}
