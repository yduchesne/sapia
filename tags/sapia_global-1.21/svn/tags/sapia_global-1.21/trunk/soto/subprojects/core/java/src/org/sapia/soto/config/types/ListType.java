/*
 * ListType.java
 *
 * Created on June 28, 2005, 2:41 PM
 */

package org.sapia.soto.config.types;

import java.util.ArrayList;
import java.util.List;
import org.sapia.util.xml.confix.ObjectCreationCallback;
import org.sapia.util.xml.confix.ObjectHandlerIF;

/**
 * Evaluates to a <code>java.util.Map</code>.
 * @author yduchesne
 */
public class ListType implements ObjectCreationCallback, ObjectHandlerIF{
  
  private List _list = new ArrayList();
  
  /** Creates a new instance of ListType */
  public ListType() {
  }
  
  public Object onCreate(){
    return _list;
  }
  
  public void handleObject(String name, Object value){
    _list.add(value);
  }
  
}
