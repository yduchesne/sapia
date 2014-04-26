package org.sapia.archie.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sapia.archie.AbstractNode;
import org.sapia.archie.Entry;
import org.sapia.archie.NameParser;
import org.sapia.archie.NamePart;
import org.sapia.archie.Node;
import org.sapia.archie.NodeFactory;
import org.sapia.archie.ProcessingException;


/**
 * An instance of this class tolerates multiple values under the same name.
 * 
 * @author Yanick Duchesne
 */
public class MultiValueNode extends AbstractNode {
  protected  Map<NamePart, List<Object>>  _valueLists;

  protected MultiValueNode(Map<NamePart, Node> children,  Map<NamePart, List<Object>> values, NodeFactory fac)
                    throws ProcessingException {
    super(children, fac);
    _valueLists = values;
  }

  protected MultiValueNode(NameParser parser, Map<NamePart, Node> children,  Map<NamePart, List<Object>> values, NodeFactory fac)
  throws ProcessingException {
    super(parser, children, fac);
    _valueLists = values;
  }

  /**
   * This method calls onSelect() and onRead() successively, before returning
   * the a value.
   *
   * @see #onSelect(List)
   * @see #onRead(NamePart, Object)
   * 
   * @see org.sapia.archie.Node#getValue(NamePart)
   */
  public Object getValue(NamePart name) {
    List<Object> values = _valueLists.get(name);

    if (values == null) {
      return null;
    }
    
    Object selected = onSelect(values);
    if(selected != null){
      selected = onRead(name, selected);
    }
    return selected;
  }
  
  /**
   * @see org.sapia.archie.Node#removeValue(org.sapia.archie.NamePart)
   */
  public Object removeValue(NamePart name) {
    Object val = getValue(name);
    _valueLists.remove(name);
    return val;
  }
  
  /**
   * @see org.sapia.archie.Node#getEntries()
   */
  public Iterator<Entry> getEntries() {
    List<Entry> entries = new ArrayList<Entry>();
    for (Map.Entry<NamePart, List<Object>> entry : _valueLists.entrySet()) {
      for(int i = 0; i < entry.getValue().size(); i++){
        entries.add(new Entry(entry.getKey().toString(), entry.getValue().get(i)));
      }
    }
    return entries.iterator();
  }
  
  /**
   * @see org.sapia.archie.Node#putValue(NamePart, Object, boolean)
   */
  public boolean putValue(NamePart name, Object value, boolean overwrite) {
    List<Object> values = _valueLists.get(name);

    if (values == null) {
      values = new ArrayList<>();
      _valueLists.put(name, values);
    }

    if (!overwrite && (values.size() != 0)) {
      return false;
    }

    Object toBind = onWrite(name, value);
    if(toBind != null){
      values.add(toBind);
    }
    return true;
  }

  /**
   * @see org.sapia.archie.Node#getValueNames()
   */
  public Iterator<NamePart> getValueNames() {
    return _valueLists.keySet().iterator();
  }

  /**
   * @see org.sapia.archie.Node#getValueCount()
   */
  public int getValueCount() {
    return _valueLists.size();
  }
  
  /**
   * @see org.sapia.archie.Node#getChildrenCount()
   */
  public int getChildrenCount() {
    return _valueLists.size();
  }

  /**
   * @see org.sapia.archie.Node#getChildrenNames()
   */
  public Iterator<NamePart> getChildrenNames() {
    return _valueLists.keySet().iterator();
  }

  /**
   * Selects a given value, from the given list, and returns it.
   * This template method is internally called by <code>getValue(...)</code>.
   * 
   * @param values the list of values to select from.
   * @return an item from the given list of values.
   * 
   * @see #getValue(NamePart)
   */
  protected Object onSelect(List<Object> values) {
    if (values.size() > 0) {
      Object toReturn = values.remove(0);
      values.add(toReturn);

      return toReturn;
    }

    return null;
  }
  
  /**
   * This method is internally called by <code>getValue(...)</code>.
   *  
   * @param np the name under which the selected object is bound.
   * @param selected the selected object.
   * @return the object to return to the application (the selected object,
   * or a substitute).
   * 
   * @see #getValue(NamePart)
   */
  protected Object onRead(NamePart np, Object selected){
    return selected;
  }
  
  /**
   * Called prior to bind the given object to the given 
   * name part.
   * 
   * @param np the <code>NamePart</code> under which the given object
   * is to be bound.
   * @param toBind the object to bind.
   * @return the object to bind (can be substituted).
   */
  protected Object onWrite(NamePart np, Object toBind){
    return toBind;
  }

  static class ValueIterator implements Iterator<Object> {
    Iterator<List<Object>> _lists;
    Iterator<Object> _current;

    ValueIterator(Iterator<List<Object>> lists) {
      _lists = lists;
    }

    public boolean hasNext() {
      if (_current != null) {
        if (_current.hasNext()) {
          return true;
        } else if (_lists.hasNext()) {
          _current = _lists.next().iterator();

          return _current.hasNext();
        } else {
          return false;
        }
      } else if (_lists.hasNext()) {
        _current = _lists.next().iterator();

        return hasNext();
      }

      return false;
    }

    public Object next() {
      if (_current == null) {
        if (hasNext()) {
          return _current.next();
        }
      }

      return _current.next();
    }

    public void remove() {
    }
  }
}
