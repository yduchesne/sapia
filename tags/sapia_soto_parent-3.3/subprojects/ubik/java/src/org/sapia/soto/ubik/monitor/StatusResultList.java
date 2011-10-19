package org.sapia.soto.ubik.monitor;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.sapia.soto.util.matcher.UriPattern;

/***
 * This instance encapsulates {@link StatusResult} instances.
 * 
 * @author yduchesne
 *
 */
public class StatusResultList {
  
  List _results = new LinkedList();
  private int  _errCount = 0;
  
  /**
   * @param result adds the given <code>StatusResult</code> to this
   * instance.
   */
  public void addResult(StatusResult result){
    _results.add(result);
    if(result.isError())_errCount++;
  }
  
  /**
   * @param results
   */
  public void addResults(Collection results){
    Iterator itr = results.iterator();
    while(itr.hasNext()){
      StatusResult res = (StatusResult)itr.next();
      addResult(res);
    }
  }
  
  public List getResults(){
    return Collections.unmodifiableList(_results);
  }
  
  public List getModifiableResults(){
    return _results;
  }
  
  public int getErrorCount(){
    return _errCount;
  }
  
  public int getResultCount(){
    return _results.size();
  }
  
  public StatusResultList filterForServiceId(String id){
    StatusResultList filtered = new StatusResultList();
    for(int i = 0; i < _results.size(); i++){
      StatusResult res = (StatusResult)_results.get(i);
      if(res.getServiceId() != null && id != null && res.getServiceId().equals(id)){
        filtered.addResult(res);
      }
    }
    return filtered;
  }
  
  public StatusResultList filterForServiceId(UriPattern id){
    StatusResultList filtered = new StatusResultList();
    for(int i = 0; i < _results.size(); i++){
      StatusResult res = (StatusResult)_results.get(i);
      if(res.getServiceId() != null && id != null && id.matches(res.getServiceId())){
        filtered.addResult(res);
      }
    }
    return filtered;
  }  
  
  public StatusResultList filterForServiceClass(String className){
    StatusResultList filtered = new StatusResultList();
    for(int i = 0; i < _results.size(); i++){
      StatusResult res = (StatusResult)_results.get(i);
      if(res.getServiceClassName() != null && className != null && 
          res.getServiceClassName().equals(className)){
        filtered.addResult(res);
      }      
    }
    return filtered;
  }
  
  public StatusResultList filterForServiceClass(UriPattern className){
    StatusResultList filtered = new StatusResultList();
    for(int i = 0; i < _results.size(); i++){
      StatusResult res = (StatusResult)_results.get(i);
      if(res.getServiceClassName() != null && className != null && 
          className.matches(res.getServiceClassName())){
        filtered.addResult(res);
      }      
    }
    return filtered;
  }    

}
