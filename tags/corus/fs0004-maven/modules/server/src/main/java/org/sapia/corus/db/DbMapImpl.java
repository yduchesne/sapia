package org.sapia.corus.db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.sapia.corus.db.persistence.ClassDescriptor;
import org.sapia.corus.db.persistence.Record;
import org.sapia.corus.db.persistence.Template;
import org.sapia.corus.db.persistence.TemplateMatcher;

import jdbm.JDBMEnumeration;
import jdbm.JDBMHashtable;


/**
 * A {@link DbMap} implementation on top of JDMB.
 * 
 * @author Yanick Duchesne
 */
public class DbMapImpl<K, V> implements DbMap<K, V> {
  
  private JDBMHashtable _hashtable;
  private ClassDescriptor<V> _classDescriptor;

  DbMapImpl(Class<K> keyType, Class<V> valueType, JDBMHashtable hashtable) {
    _hashtable = hashtable;
    _classDescriptor = new ClassDescriptor<V>(valueType);
  }

  @Override
  public void close() {
    try {
      _hashtable.dispose();
    } catch (IOException e) {
      // noop;
    }
  }

  @SuppressWarnings(value="unchecked")
  @Override
  public V get(K key) {
    try {
      Record<V> rec = (Record<V>)_hashtable.get(key);
      if(rec == null){
        return null;
      }
      else{
        return rec.toObject(_classDescriptor);
      }
    } catch (IOException e) {
      throw new IORuntimeException(e);
    }
  }

  @Override
  public Iterator<K> keys() {
    try {
      return new KeyIterator(_hashtable.keys());
    } catch (IOException e) {
      throw new IORuntimeException(e);
    }
  }

  @Override
  public void put(K key, V value) {
    try {
      Record<V> r = Record.createFor(_classDescriptor, value);
      _hashtable.put(key, r);
    } catch (IOException e) {
      throw new IORuntimeException(e);
    }
  }

  @Override
  public void remove(K key) {
    try {
      _hashtable.remove(key);
    } catch (IOException e) {
      throw new IORuntimeException(e);
    }
  }
  
  @Override
  public Iterator<V> values() {
    try {
      return new RecordIterator(_hashtable.values());
    } catch (IOException e) {
      throw new IORuntimeException(e);
    }
  }

  @Override
  public org.sapia.corus.db.Matcher<V> createMatcherFor(V template) {
    return new TemplateMatcher<V>(new Template<V>(_classDescriptor, template));
  }
  
  @Override
  @SuppressWarnings(value="unchecked")
  public Collection<V> values(Matcher<V> matcher) {
    try {
      Collection<V> result = new ArrayList<V>();
      JDBMEnumeration enumeration = _hashtable.values();
      while(enumeration.hasMoreElements()){
        Record<V> rec = (Record<V>)enumeration.nextElement();
        V obj = rec.toObject(_classDescriptor);
        if(matcher.matches(obj)){
          result.add(obj);
        }
      }
      return result;
    } catch (IOException e) {
      throw new IORuntimeException(e);
    }
  }

  @Override
  public void clear() {
    Iterator<K> keys = keys();    
    while(keys.hasNext()){
      this.remove(keys.next());
    }
  }
  
  /*//////////////////////////////////////////////////
                    INNER CLASSES
  //////////////////////////////////////////////////*/

  class RecordIterator implements Iterator<V> {
    private JDBMEnumeration _enum;

    RecordIterator(JDBMEnumeration anEnum) {
      _enum = anEnum;
    }

    public boolean hasNext() {
      try {
        return _enum.hasMoreElements();
      } catch (IOException e) {
        throw new IORuntimeException(e);
      }
    }

    @SuppressWarnings(value="unchecked")
    public V next() {
      try {
        Record<V> record = (Record<V>)_enum.nextElement();
        return record.toObject(_classDescriptor);
      } catch (IOException e) {
        throw new IORuntimeException(e);
      }
    }

    public void remove() {
      throw new UnsupportedOperationException("remove()");
    }
  }
  
  class KeyIterator implements Iterator<K> {
    private JDBMEnumeration _enum;

    KeyIterator(JDBMEnumeration anEnum) {
      _enum = anEnum;
    }

    public boolean hasNext() {
      try {
        return _enum.hasMoreElements();
      } catch (IOException e) {
        throw new IORuntimeException(e);
      }
    }

    @SuppressWarnings(value="unchecked")
    public K next() {
      try {
        return (K)_enum.nextElement();
      } catch (IOException e) {
        throw new IORuntimeException(e);
      }
    }

    public void remove() {
      throw new UnsupportedOperationException("remove()");
    }
  }
}
