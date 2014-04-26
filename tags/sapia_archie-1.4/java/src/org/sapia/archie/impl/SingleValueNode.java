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
 * An instance of this class tolerates only one value for a given name.
 * 
 * @author Yanick Duchesne
 */
public class SingleValueNode extends AbstractNode {
  protected Map<NamePart, Object> _values;

  public SingleValueNode(Map<NamePart, Node> children, Map<NamePart, Object> values, NodeFactory fac)
                  throws ProcessingException {
    super(children, fac);
    _values = values;
  }
  
  public SingleValueNode(NameParser parser, Map<NamePart, Node> children, Map<NamePart, Object> values, NodeFactory fac)
                  throws ProcessingException {
    super(parser, children, fac);
    _values = values;
  }  

  /**
   * @see org.sapia.archie.Node#getValue(NamePart)
   */
  public Object getValue(NamePart name) {
    Object val = _values.get(name);

    if (val != null) {
      return onRead(name, val);
    }

    return val;
  }
  
  /**
   * @see org.sapia.archie.Node#removeValue(NamePart)
   */
  public Object removeValue(NamePart name) {
    Object val = getValue(name);
    _values.remove(name);
    return val;
  }


  /**
   * @see org.sapia.archie.Node#putValue(NamePart, Object, boolean)
   */
  public boolean putValue(NamePart name, Object value, boolean overwrite) {
    if (!overwrite && _values.containsKey(name)) {
      return false;
    } else {
      value = onWrite(name, value);
      _values.put(name, value);

      return true;
    }
  }

  /**
   * @see org.sapia.archie.Node#getValueCount()
   */
  public int getValueCount() {
    return _values.size();
  }
  
  /**
   * @see org.sapia.archie.Node#getValueNames()
   */
  public Iterator<NamePart> getValueNames() {
    return _values.keySet().iterator();
  }
  
  /**
   * @see org.sapia.archie.Node#getEntries()
   */
  public Iterator<Entry> getEntries() {
    List<Entry> entries = new ArrayList<Entry>(_values.size());

    for (Map.Entry<NamePart, Object> entry : _values.entrySet()) {
      entries.add(new Entry(entry.getKey().toString(), entry.getValue()));
    }
    return entries.iterator();
  }

  protected Object onRead(NamePart np, Object toRead) {
    return toRead;
  }

  protected Object onWrite(NamePart np, Object o) {
    return o;
  }
}
