package org.sapia.corus.client.services.deployer.dist;

import java.io.Serializable;

import org.sapia.console.Arg;
import org.sapia.console.CmdElement;
import org.sapia.ubik.util.Strings;

/**
 * Corresponds to a VM argument.
 *  
 * @author yduchesne
 *
 */
public class VmArg implements Param, Serializable {
  
  static final long serialVersionUID = 1L;
  
  private String value;
  
  /**
   * @param value a value.
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
  public CmdElement convert() {
    if (Strings.isBlank(value)) {
      return new Arg("");
    } else {
      return new Arg(value);
    }
  }

}
