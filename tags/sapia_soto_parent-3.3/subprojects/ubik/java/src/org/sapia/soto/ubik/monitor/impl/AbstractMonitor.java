package org.sapia.soto.ubik.monitor.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.sapia.soto.ubik.monitor.Monitor;
import org.sapia.soto.ubik.monitor.MonitorAgent;
import org.sapia.soto.ubik.monitor.Status;
import org.sapia.soto.ubik.monitor.StatusResult;
import org.sapia.soto.ubik.monitor.StatusResultList;

/**
 * This class provides logic that is common to most 
 * Monitor implementations.
 * 
 * @author yduchesne
 *
 */
public abstract class AbstractMonitor implements Monitor{
  
  private List _agents   = Collections.synchronizedList(new ArrayList());

  /**
   * @param agent a <code>MonitorAgent</code> to monitor.
   */
  public void addAgent(MonitorAgent agent){
    addAgent(new AgentBinding(agent));
  }
  
  protected void addAgent(AgentBinding binding){
    synchronized (_agents) {
      if(!_agents.contains(binding)){
        _agents.add(binding);
      }
    }    
  }
  
  /**
   * Adds all the agents in the given collection to this instance.
   * 
   * @param agents a <code>Collection</code> of <code>MonitorAgent</code>.
   */
  public void addAllAgents(Collection agents){
    Iterator itr = agents.iterator();
    while(itr.hasNext()){
      MonitorAgent agent = (MonitorAgent)itr.next();
      addAgent(agent);
    }
  }
  
  protected void clearAgents(){
    _agents.clear();
  }
  
  protected void doGetStatus(
      String idOrClass, 
      StatusResultList resultList, 
      boolean isId) {

    synchronized (_agents) {
      for (int i = 0; i < _agents.size(); i++) {
        AgentBinding binding = (AgentBinding)_agents.get(i);
        MonitorAgent agent = binding.getAgent();
        try {
          
          Status stat = null;
          if(binding.hasStatus()){
            stat = binding.getStatus();
          }else{
            stat = agent.checkStatus();
          }
          if(idOrClass == null){
            doProcessStatus(resultList, stat, binding);
            continue;
          }
          else if(isId && stat.getServiceId() == null){
            binding.setStatus(stat);
            continue;
          }
          else if(isId && !stat.getServiceId().equals(idOrClass)){
            binding.setStatus(stat);
            continue;
          }
          else if(!isId && !stat.getServiceClassName().equals(idOrClass)){
            binding.setStatus(stat); 
            continue;
          }          
          doProcessStatus(resultList, stat, binding);          
        } catch (RemoteException e) {
          _agents.remove(i--);
          e.printStackTrace();
        } catch (Exception e) {
          _agents.remove(i--);
          e.printStackTrace();
        } 
      }
    }
  } 
  
  private void doProcessStatus(StatusResultList resultList, Status stat, AgentBinding binding){
    if(binding.hasStatus()){
      binding.clear();
    }
    if(stat.isError()){
      resultList.addResult(new StatusResult(stat.getError(), stat.getServiceId(), 
          stat.getServiceClassName()).setProperties(stat.getResultProperties()));
    }
    else{
      resultList.addResult(new StatusResult(stat.getServiceId(), stat.getServiceClassName())
      .setProperties(stat.getResultProperties()));
    }
  }  

  protected static class AgentBinding{
    
    Status _cached;
    MonitorAgent _agent;
    
    protected AgentBinding(MonitorAgent agent){
      _agent = agent;
    }
    
    Status getStatus(){
      return _cached;
    }
    
    MonitorAgent getAgent(){
      return _agent;
    }
    
    boolean hasStatus(){
      return _cached != null;
    }
    
    void setStatus(Status status){
      _cached = status;
    }
    
    void clear(){
      _cached = null;
    }
    
    public int hashCode() {
      return _agent.hashCode();
    }
    
    public boolean equals(Object obj) {
      if(obj instanceof AgentBinding){
        return _agent.equals((AgentBinding)obj);
      }
      else{
        return _agent.equals(obj);
      }
    }
  }
}
