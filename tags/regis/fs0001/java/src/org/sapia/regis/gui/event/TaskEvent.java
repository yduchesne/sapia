package org.sapia.regis.gui.event;

import org.sapia.regis.gui.GlobalContext;
import org.sapia.regis.gui.task.Task;

public class TaskEvent{
  
  private Task task;
  
  public TaskEvent(Task task){
    this.task = task;
  }
  
  public void execute(){
    try {
      if(task != null){
        task.execute();
      }
    } catch (Exception e) {
      GlobalContext.getInstance().error(e);
    }
  }

}
