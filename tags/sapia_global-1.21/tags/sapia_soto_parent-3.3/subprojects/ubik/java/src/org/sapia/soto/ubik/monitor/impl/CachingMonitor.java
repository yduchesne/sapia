package org.sapia.soto.ubik.monitor.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.sapia.soto.ubik.monitor.Monitor;
import org.sapia.soto.ubik.monitor.StatusResult;
import org.sapia.soto.ubik.monitor.StatusResultList;

/**
 * An instance of this class wraps a {@link Monitor} and provides
 * caching of the results.
 * 
 * @author yduchesne
 *
 */
public class CachingMonitor implements Monitor{
  
  private Map _statusForClass = new TreeMap();
  private Map _statusForId    = new TreeMap();
  
  private long _interval = 30000;
  private Monitor _delegate;
  private long _lastCheck = 0; 
  
  public CachingMonitor(Monitor toWrap, long refreshIntervalMillis){
    _interval = refreshIntervalMillis;
    _delegate = toWrap;
  }
  
  public CachingMonitor(Monitor toWrap){
    _delegate = toWrap;
  }
  
  public synchronized StatusResultList getStatusForClass(String className) {
    if(className == null){
      return getAll();
    }
    if(isDue()){
      fill();
    }
    StatusResultList lst = (StatusResultList)_statusForClass.get(className);
    if(lst == null){
      lst = new StatusResultList();
    }
    return lst;
  }
  
  public synchronized StatusResultList getStatusForId(String serviceId) {
    if(serviceId == null){
      return getAll();
    }
    if(isDue()){
      fill();
    }
    StatusResultList lst = (StatusResultList)_statusForId.get(serviceId);
    if(lst == null){
      lst = new StatusResultList();
    }
    return lst;
  }
  
  private StatusResultList getAll(){
    if(isDue()){
      fill();
    }
    StatusResultList toReturn = new StatusResultList();
    Iterator resultLists = _statusForClass.values().iterator();
    while(resultLists.hasNext()){
      toReturn.addResults(((StatusResultList)resultLists.next()).getResults());
    }
    resultLists = _statusForId.values().iterator();
    while(resultLists.hasNext()){
      toReturn.addResults(((StatusResultList)resultLists.next()).getResults());
    }
    return toReturn;
  }
  
  private void fill(){
    _lastCheck = System.currentTimeMillis();
    _statusForClass.clear();
    _statusForId.clear();
    List results = _delegate.getStatusForClass(null).getResults();
    for(int i = 0; i < results.size(); i++){
      StatusResult res = (StatusResult)results.get(i);
      StatusResultList lst;
      if(res.getServiceId() != null){
        lst = (StatusResultList)_statusForId.get(res.getServiceId());
        if(lst == null){
          lst = new StatusResultList();
          _statusForId.put(res.getServiceId(), lst);
        }
        lst.addResult(res);
      }
      else{
        lst = (StatusResultList)_statusForClass.get(res.getServiceClassName());
        if(lst == null){
          lst = new StatusResultList();
          _statusForClass.put(res.getServiceClassName(), lst);
        }
        lst.addResult(res);
      }
    }
  }
  
  private boolean isDue(){
    return System.currentTimeMillis() - _lastCheck > _interval;
  }
}
