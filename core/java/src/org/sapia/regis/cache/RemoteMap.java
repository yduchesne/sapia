package org.sapia.regis.cache;

import java.rmi.Remote;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class RemoteMap implements Map, Remote{
  
  Map _delegate;
  
  RemoteMap(Map delegate){
    _delegate = delegate;
  }

  public void clear() {
    _delegate.clear();
  }

  public boolean containsKey(Object key) {
    return _delegate.containsKey(key);
  }

  public boolean containsValue(Object value) {
    return _delegate.containsValue(value);
  }

  public Set entrySet() {
    Iterator entries = _delegate.entrySet().iterator();
    HashSet entrySet = new HashSet();
    while(entries.hasNext()){
      Map.Entry entry = (Map.Entry)entries.next();
      entrySet.add(new SerializableEntry(entry.getKey(), entry.getValue()));
    }
    return entrySet;
  }

  public Object get(Object key) {
    return _delegate.get(key);
  }

  public boolean isEmpty() {
    return _delegate.isEmpty();
  }

  public Set keySet() {
    return new HashSet(_delegate.keySet());
  }

  public Object put(Object arg0, Object arg1) {
    return _delegate.put(arg0, arg1);
  }

  public void putAll(Map arg0) {
    _delegate.putAll(arg0);
  }

  public Object remove(Object key) {
    return _delegate.remove(key);
  }

  public int size() {
    return _delegate.size();
  }

  public Collection values() {
    return new HashSet(_delegate.values());
  }
  
}
