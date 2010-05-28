package org.sapia.corus.db.persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * An instance of this class is used to perform matching operations dynamically,
 * in a query-by-example fashion.
 * 
 * @author yduchesne
 *
 */
public class Template<T> {

  ClassDescriptor<T> descriptor;
  Object instance;
  Map<String, FieldValue> fieldValuesByName = new HashMap<String, FieldValue>();

  public Template(ClassDescriptor<T> desc, Object instance){
    this.descriptor = desc;
    this.instance = instance;
    analyze();
  }
  
  /**
   * @param record a {@link Record} to match.
   * @return <code>true</code> if this instance's state matches the given record.
   */
  public boolean matches(Record<T> record){
    for(FieldValue fv:fieldValuesByName.values()){
      Object toMatch = record.getValueAt(fv.getDescriptor().getIndex());
      if(!fv.matches(toMatch)){
        return false;
      }
    }
    return true;
  }

  /**
   * @param record an {@link Object} to match.
   * @return <code>true</code> if this instance's state matches the given object.
   */
  public boolean matches(Object o){
    for(FieldValue fv:fieldValuesByName.values()){
      FieldDescriptor fd = descriptor.getFieldForName(fv.getDescriptor().getName());
      Object otherValue = fd.invokeAccessor(o);
      if(!fv.matches(otherValue)){
        return false;
      }
    }
    return true;
  }
  
  private void analyze(){
    for(FieldDescriptor fd:descriptor.getFields()){
      Object value = fd.invokeAccessor(instance);
      FieldValue fv = new FieldValue(fd, value);
      fieldValuesByName.put(fd.getName(), fv);
    }
  }
}
