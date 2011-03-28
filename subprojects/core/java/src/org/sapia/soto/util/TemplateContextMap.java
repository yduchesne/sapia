package org.sapia.soto.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.sapia.util.text.TemplateContextIF;

public class TemplateContextMap extends HashMap{

  TemplateContextIF _ctx;
  private List _overridenKeys;
  
  public TemplateContextMap(TemplateContextIF ctx){
    _ctx = ctx;
    _overridenKeys = new ArrayList();
  }
  
  public Object get(Object key){
    if (_overridenKeys.contains(key)) {
      return super.get(key);
    } else if (key instanceof String) {
      Object val = _ctx.getValue((String)key);
      if(val == null){
        val = super.get(key);
      }
      return val;
    }
    else{
      return super.get(key);
    }
  }
  
  public Object put(Object key, Object value, boolean isOverridingAncestor) {
    if (isOverridingAncestor) {
      _overridenKeys.add(key);
    }
    return super.put(key, value);
  }
  
}
