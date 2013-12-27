package org.sapia.archie.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sapia.archie.AbstractNode;
import org.sapia.archie.Entry;
import org.sapia.archie.NameParser;
import org.sapia.archie.NamePart;
import org.sapia.archie.NodeFactory;
import org.sapia.archie.ProcessingException;


/**
 * An instance of this class tolerates only one value for a given name.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class SingleValueNode extends AbstractNode {
  protected Map _values;

  public SingleValueNode(Map children, Map values, NodeFactory fac)
                  throws ProcessingException {
    super(children, fac);
    _values = values;
  }
  
  public SingleValueNode(NameParser parser, Map children, Map values, NodeFactory fac)
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
  public Iterator getValueNames() {
    return _values.keySet().iterator();
  }
  
  /**
   * @see org.sapia.archie.Node#getEntries()
   */
  public Iterator getEntries() {
    List entries = new ArrayList(_values.size());
    Iterator items = _values.entrySet().iterator();
    Map.Entry entry;
    while(items.hasNext()){
      entry = (Map.Entry)items.next();
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
