package org.sapia.soto.xfire.ant;

/**
 * Interface meant to isolate Ant's logging framework.
 * 
 * @author yduchesne
 *
 */
public interface TaskLog {
  
  public void debug(String msg, int level);

}
