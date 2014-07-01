package org.sapia.corus.client.services.deployer.dist;

import java.io.Serializable;

import org.sapia.console.Arg;
import org.sapia.console.CmdLine;
import org.sapia.corus.client.common.Env;

/**
 * Models a generic command-line argument.
 * 
 * @author yduchesne
 *
 */
public class GenericArg implements CmdGenerator, Serializable {
  
  private static final long serialVersionUID = 1L;
  
  private String value = "";
  
  /**
   * @param value the argument's value.
   */
  public void setValue(String value) {
    this.value = value;
  }
  
  /**
   * @return this instance's value.
   */
  public String getValue() {
    return value;
  }
  
  @Override
  public void generate(Env env, CmdLine toAppendTo) {
    toAppendTo.addArg(new Arg(value));
  }
}
