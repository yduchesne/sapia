package org.sapia.corus.taskmanager.core;

public class DefaultScheduler implements Scheduler{
  
  public CorusTransaction begin() {
    DefaultTransaction tx = new DefaultTransaction(new java.util.Timer(true));
    return tx;
  }
  
  public void schedule(String name, CorusTransaction tx, Schedule sched, CorusTask task) {
    DefaultTransaction defaultTx = (DefaultTransaction)tx;
    defaultTx.addTask(name, sched, task);
    
  }
  
  public static void main(String[] args) throws Exception{
    DefaultScheduler scheduler = new DefaultScheduler();
    DefaultTransaction tx = (DefaultTransaction)scheduler.begin();
    scheduler.schedule("task1", tx, Schedule.oneShot(), 
     new CorusTask(){
      
       public void execute(CorusTaskContext ctx) {
         ctx.execSequential("task1.nested1", Schedule.recurring(2000).setMaxExec(3), 
           new CorusTask(){
             public void execute(CorusTaskContext ctx) {}
           });
         ctx.execParallel("task2", Schedule.oneShot().setDelay(2000).setMaxExec(1), 
           new CorusTask(){
             public void execute(CorusTaskContext ctx) {}
           });
       }
      
     });
    tx.commit();
    tx.waitForCompletion(30000);
    System.out.println(tx.getStatus());
  }

   
}
