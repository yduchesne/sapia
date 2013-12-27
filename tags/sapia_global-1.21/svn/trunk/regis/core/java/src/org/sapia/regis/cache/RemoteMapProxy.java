package org.sapia.regis.cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RemoteMapProxy implements Serializable, Map{
  
  Map _cache = new HashMap();
  Map _remote;
  
  public RemoteMapProxy(Map delegate){
    if(delegate instanceof RemoteMap){
      _remote = delegate;
    }
    else{
      _remote = new RemoteMap(delegate); 
    }
  }

  public void clear() {
    _cache.clear();
    _remote.clear();
  }

  public boolean containsKey(Object key) {
    if(_cache.containsKey(key)){
      return true;
    }
    else{
      return _remote.containsKey(key);
    }
  }

  public boolean containsValue(Object value) {
    if(_cache.containsValue(value)){
      return true;
    }
    else{
      return _remote.containsValue(value);
    }
  }

  public Set entrySet() {
    return _remote.entrySet();
  }

  public Object get(Object key) {
    Object o = _cache.get(key);
    o = _remote.get(key);
    if(o != null){
      _cache.put(key, o);
    }
    return o;
  }

  public boolean isEmpty() {
    return _remote.isEmpty();
  }

  public Set keySet() {
    return _remote.keySet();
  }

  public Object put(Object arg0, Object arg1) {
    _cache.put(arg0, arg1);
    return _remote.put(arg0, arg1);
  }

  public void putAll(Map arg0) {
    _cache.putAll(arg0);
    _remote.putAll(arg0);
  }

  public Object remove(Object key) {
    _cache.remove(key);
    return _remote.remove(key);
  }

  public int size() {
    return _remote.size();
  }

  public Collection values() {
    return _remote.values();
  }

}
