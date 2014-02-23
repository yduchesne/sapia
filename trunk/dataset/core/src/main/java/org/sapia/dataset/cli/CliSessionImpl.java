package org.sapia.dataset.cli;

import groovy.lang.GroovyShell;

import org.sapia.dataset.io.ConsoleOutput;

/**
 * Holds data giving access to the command-line runtime.
 * 
 * @author yduchesne
 *
 */
public class CliSessionImpl implements CliSession {

  private GroovyShell   shell;
  private ConsoleOutput output;
  private ErrorBuffer   errors  = new ErrorBuffer();
  
  CliSessionImpl(GroovyShell shell, ConsoleOutput output) {
    this.shell  = shell;
    this.output = output;
  }
  
  @Override
  public void message(String msg) {
    output.println(">> " + msg);
  }
  
  @Override
  public GroovyShell getShell() {
    return shell;
  }
  
  @Override
  public ConsoleOutput getOutput() {
    return output;
  }
  
  @Override
  public ErrorBuffer getErrorBuffer() {
    return errors;
  }
  
  @Override
  public void exit() {
    System.exit(0);
  }

}
