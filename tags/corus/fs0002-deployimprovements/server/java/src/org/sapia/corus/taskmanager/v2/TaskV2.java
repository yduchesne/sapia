package org.sapia.corus.taskmanager.v2;

public abstract class TaskV2 {

  private volatile boolean aborted;
  private volatile int executionCount, maxExecution;
  private String name;

  public TaskV2() {
  }
  
  public TaskV2(String name) {
    this.name = name;
  }
  
  public String getName() {
    if(name == null){ 
      name = getClass().getSimpleName(); 
    }
    return name;
  }
  
  public void setMaxExecution(int maxExecution) {
    this.maxExecution = maxExecution;
  }
  
  boolean isAborted() {
    return aborted;
  }
  
  boolean isMaxExecutionReached(){
    return maxExecution > 0 && executionCount >= maxExecution;
  }
  
  public abstract Object execute(TaskExecutionContext ctx) throws Throwable;
  
  protected void abort(TaskExecutionContext ctx){
    aborted = true;
  }
  
  protected void onMaxExecutionReached(TaskExecutionContext ctx) throws Throwable{}

  protected int getExecutionCount() {
    return executionCount;
  }
  
  protected int getMaxExecution() {
    return maxExecution;
  }
  
  void incrementExecutionCount(){
    if(executionCount == Integer.MAX_VALUE){
      executionCount = 0;
    }
    executionCount++;
  }
  
}
