package org.sapia.soto.xfire.ant;

/**
 * Implements the {@link TaskLog} interface over stdout.
 * @author yduchesne
 *
 */
public class StdoutTaskLog implements TaskLog{
  
  public void debug(String msg, int level) {
    System.out.println(msg);
  }

}
