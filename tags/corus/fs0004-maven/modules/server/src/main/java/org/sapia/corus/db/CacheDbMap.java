package org.sapia.corus.db;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Yanick Duchesne
 *
 */
public class CacheDbMap<K, V> implements DbMap<K, V>{
	
	private DbMap<K, V> _db;
	private Map<K, V> _cache = new HashMap<K, V>();
	
	CacheDbMap(DbMap<K,V> cached){
		_db = cached;
	}

  public synchronized void close() {
    _db.close();
  }

  public synchronized V get(K key) {
    V toReturn = _cache.get(key);
    if(toReturn == null){
    	toReturn = _db.get(key);
    	if(toReturn != null){
    		_cache.put(key, toReturn);
    	}
    }
    return toReturn;
  }

  public Iterator<K> keys() {
    return _db.keys();
  }

  public synchronized void put(K key, V value) {
    _cache.remove(key);
		_db.put(key, value);
  }

  public synchronized void remove(K key) {
  	_cache.remove(key);
  	_db.remove(key);
  }

  public Iterator<V> values() {
    return _db.values();
  }
  
  public synchronized void clear() {
    _cache.clear();
    _db.clear();
  }
  
  @Override
  public Matcher<V> createMatcherFor(V template) {
    return _db.createMatcherFor(template);
  }
  
  @Override
  public Collection<V> values(Matcher<V> matcher) {
    return _db.values(matcher);
  }

}
