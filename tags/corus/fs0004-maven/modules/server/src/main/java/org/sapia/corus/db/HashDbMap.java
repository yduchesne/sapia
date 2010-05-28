package org.sapia.corus.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.sapia.corus.db.persistence.ClassDescriptor;
import org.sapia.corus.db.persistence.Record;
import org.sapia.corus.db.persistence.Template;
import org.sapia.corus.db.persistence.TemplateMatcher;


/**
 * @author Yanick Duchesne
 *
 */
public class HashDbMap<K, V> implements DbMap<K, V> {
  private Map<K, Record<V>> _map = new HashMap<K, Record<V>>();
  private ClassDescriptor<V> _classDescriptor;
  
  public HashDbMap(ClassDescriptor<V> cd) {
    _classDescriptor = cd;
  }

  public void close() {
  }

  public V get(K key) {
    Record<V> record = _map.get(key);
    if(record != null){
      return record.toObject(_classDescriptor);
    }
    else{
      return null;
    }
  }

  public Iterator<K> keys() {
    return _map.keySet().iterator();
  }

  public void put(K key, V value) {
    _map.put(key, Record.createFor(_classDescriptor, value));
  }

  public void remove(K key) {
    _map.remove(key);
  }

  public Iterator<V> values() {
    return new RecordIterator(_map.values().iterator());
  }
  
  public org.sapia.corus.db.Matcher<V> createMatcherFor(V template) {
    return new TemplateMatcher<V>(new Template<V>(_classDescriptor, template));
  }
 
  public Collection<V> values(Matcher<V> matcher) {
    Collection<V> result = new ArrayList<V>();
    Iterator<Record<V>> iterator = _map.values().iterator();
    while(iterator.hasNext()){
      Record<V> rec = iterator.next();
      V obj = rec.toObject(_classDescriptor);
      if(matcher.matches(obj)){
        result.add(obj);
      }
    }
    return result;
  }
  
  public void clear() {
    _map.clear();
  }
  
  class RecordIterator implements Iterator<V>{

    private Iterator<Record<V>> delegate;
    
    public RecordIterator(Iterator<Record<V>> delegate) {
      this.delegate = delegate;
    }
    
    @Override
    public V next() {
      Record<V> rec = delegate.next();
      return rec.toObject(_classDescriptor);
    }
    
    @Override
    public boolean hasNext() {
      return delegate.hasNext();
    }
    
    @Override
    public void remove() {
      delegate.remove();
    }
  }
}
