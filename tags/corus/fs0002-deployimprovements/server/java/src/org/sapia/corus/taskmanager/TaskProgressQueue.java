package org.sapia.corus.taskmanager;

import java.util.Collections;
import java.util.List;

import org.sapia.corus.util.ProgressMsg;
import org.sapia.corus.util.ProgressQueue;
import org.sapia.taskman.TaskOutput;

/**
 * Implements the {@link ProgressQueue} interface on top of a {@link TaskOutput}
 * @author yduchesne
 *
 */
public class TaskProgressQueue implements ProgressQueue{

  private TaskOutput output;
  
  public TaskProgressQueue(TaskOutput output) {
    this.output = output;
  }
  
  public void addMsg(ProgressMsg msg) {
    if(msg.getStatus() == ProgressMsg.DEBUG || msg.getStatus() == ProgressMsg.VERBOSE){
      output.debug(msg.getMessage());
    } 
    else if(msg.getStatus() == ProgressMsg.INFO){
      output.info(msg.getMessage());
    } 
    else if(msg.getStatus() == ProgressMsg.WARNING){
      output.warning(msg.getMessage());
    }
    else if(msg.getStatus() == ProgressMsg.ERROR){
      if(msg.getError() != null && msg.getMessage() == null){
        output.error(msg.getError());
      }
      else if(msg.getError() != null){
        output.error(msg.getMessage(), msg.getError());
      }
      else{
        output.error(msg.getMessage());
      }
    }
  }
  
  public void close() { 
  }
  
  public void verbose(Object msg) {
    output.debug(msg);
  }
  
  public void debug(Object msg) {
    output.debug(msg);
  }
  
  public void info(Object msg) {
    output.info(msg);
  }
 
  public void warning(Object msg) {
    output.warning(msg);
  }
  
  public void error(Object msg) {
    output.error(msg);
  }
  
  public boolean hasNext() {
    return false;
  }
  
  public List fetchNext() {
    return Collections.EMPTY_LIST;
  }
  
  public boolean isClosed() {
    return false;
  }
  
  public List next() {
    return fetchNext();
  }
}
