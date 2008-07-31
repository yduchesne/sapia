package org.sapia.corus.taskmanager.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.sapia.corus.taskmanager.core.Schedule.IntervalType;

public class DefaultTransaction implements CorusTransaction{
  
  public enum Status{
    
    PENDING,
    COMMITTED,
    ABORTED,
    FAILED,
    SUCCEEDED;
  }
  
  private Timer _timer;
  private List<TimerTaskContext> _pendingTasks = Collections.synchronizedList(new ArrayList<TimerTaskContext>(3));
  private List<TimerTaskContext> _runningTasks = Collections.synchronizedList(new ArrayList<TimerTaskContext>(3));
  private List<TimerTaskContext> _completedTasks = Collections.synchronizedList(new ArrayList<TimerTaskContext>(3));
  private volatile Status _status = Status.PENDING;
  
  DefaultTransaction(Timer timer){
    _timer = timer;
  }
  
  public synchronized void commit() {
    if(_status != Status.PENDING){
      throw new IllegalStateException(_status.name());
    }
    
    for(TimerTaskContext t:_pendingTasks){
      _runningTasks.add(t);
      schedule(t);
    }
    _pendingTasks.clear();
    
    _status = Status.COMMITTED;
  }
  
  public void abort(){
    synchronized(_pendingTasks){
      _pendingTasks.clear();
    }
    synchronized(_runningTasks){
      for(TimerTaskContext t:_runningTasks){
        t.stop();
      }
    }
  }
  
  public synchronized void waitForCompletion(long timeout) throws InterruptedException{
    while(_status != Status.PENDING && _status != Status.SUCCEEDED && _status != Status.FAILED){
      wait(timeout);
    }
  }
  
  public Status getStatus(){
    return _status;
  }
  
  void addTask(String name, Schedule s, CorusTask t){
    TimerTaskContext ctx = new TimerTaskContext(name, this, s, t);
    if(_status == Status.PENDING){
      synchronized(_pendingTasks){
        _pendingTasks.add(ctx);
      }
    }
    else if(_status == Status.COMMITTED){
      synchronized(_runningTasks){
        _runningTasks.add(ctx);
        schedule(ctx);
      }      
    }
    else{
      throw new IllegalStateException(_status.name());
    }
  }
  
  synchronized void notifyCompleted(){
    notifyAll();
  }
  
  private void schedule(TimerTaskContext ctx){
    if(ctx._schedule.getType() == IntervalType.FIXED_DELAY){
      _timer.schedule(new TimerTaskAdapter(ctx), ctx._schedule.getDelay(), ctx._schedule.getInterval());
    }
    else{
      _timer.scheduleAtFixedRate(new TimerTaskAdapter(ctx), ctx._schedule.getDelay(), ctx._schedule.getInterval());
    }
  }
  
  /////////////////// INNER CLASSES /////////////////// 
  
  // task context
  class TimerTaskContext implements CorusTaskContext{

    DefaultTransaction _tx;    
    Schedule _schedule;
    CorusTask _task;
    boolean _stopped;
    int _execCount;
    List<TimerTaskContext> _nestedTasks = new ArrayList<TimerTaskContext>();
    String _name;
    
    public TimerTaskContext(String name, DefaultTransaction tx, Schedule schedule, CorusTask task) {
      _name = name;
      _tx = tx;
      _schedule = schedule;
      _task = task;
    }
    
    public String getTaskName(){
      return _name;
    }
    
    public CorusTransaction getTransaction() {
      return _tx;
    }
    
    public void stop(){
      _stopped = true;
      synchronized(_runningTasks){
        _runningTasks.remove(this);
      }
      synchronized(_completedTasks){
        _completedTasks.add(this);
      }
      synchronized(_nestedTasks){
        synchronized(_runningTasks){
          for(TimerTaskContext t:_nestedTasks){
            _runningTasks.add(t);
            _tx.schedule(t);
          }
          if(_runningTasks.size() == 0){
            if(_status != Status.FAILED){
              _tx._status = Status.SUCCEEDED;
            }
            synchronized(_tx){
              _tx.notifyAll();
            }
          }          
        }
      }
    }
    
    public synchronized CorusTaskContext execSequential(String name, Schedule sched, CorusTask task) {
      synchronized(_tx){
        if(_tx._status != Status.PENDING && _tx._status != Status.COMMITTED){
          throw new IllegalStateException();
        }
      }
      TimerTaskContext nested = new TimerTaskContext(name, _tx, sched, task);
      synchronized(_nestedTasks){
        _nestedTasks.add(nested);
      }
      return this;
    }
    
    public CorusTaskContext execParallel(String name, Schedule sched, CorusTask task) {
      addTask(name, sched, task);
      return this;
    }
    
    void increment(){
      _execCount++;
    }
    
  }
  
  static class TimerTaskAdapter extends TimerTask{
    
    private TimerTaskContext _ctx;
    
    public TimerTaskAdapter(TimerTaskContext context){
      _ctx = context;
    }
    
    @Override
    public void run() {
      if(_ctx._execCount >= _ctx._schedule.getMaxExec() && _ctx._schedule.getMaxExec() > 0){
        System.out.println("Stopping: " + _ctx.getTaskName());        
        super.cancel();        
        _ctx.stop();
      }
      else{
        System.out.println("Executing: " + _ctx.getTaskName());
        _ctx._task.execute(_ctx);
        _ctx.increment();
        if(_ctx._stopped){
          super.cancel();
        }        
      }
    }
    
  }
  
  

}
