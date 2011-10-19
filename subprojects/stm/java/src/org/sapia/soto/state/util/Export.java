package org.sapia.soto.state.util;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ClassUtils;

import org.sapia.soto.state.Result;
import org.sapia.soto.state.Scope;
import org.sapia.soto.state.Step;

/**
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class Export implements Step {
  private String _from;
  private String _to;
  private String _key, _attribute;
  private String _expKey;

  /**
   * @see org.sapia.soto.state.Step#getName()
   */
  public String getName() {
    return ClassUtils.getShortClassName(getClass());
  }

  public void setFrom(String from) {
    _from = from;
  }

  public void setTo(String to) {
    _to = to;
  }

  public void setKey(String key) {
    int idx = key.indexOf('.');
    if(idx > 0){
      _key = key.substring(0, idx);
      if(++idx < key.length()){
        _attribute = key.substring(idx);
      }
    }
    else{
      _key = key;
    }
  }

  public void setExportKey(String expKey) {
    _expKey = expKey;
  }

  /**
   * @see org.sapia.soto.state.Executable#execute(org.sapia.soto.state.Result)
   */
  public void execute(Result st) {
    if(_from == null) {
      throw new IllegalStateException("'from' not specified");
    }

    if(_to == null) {
      throw new IllegalStateException("'to' not specified");
    }

    if(_key == null && _from==null) {
      throw new IllegalStateException("'key' not specified");
    }
    
    Object toExport=null;
    if(_key!=null){
    	//export a specific key from a scope
    	toExport = st.getContext().get(_key, _from);
    }else{
    	//export the scope
    	toExport = st.getContext().getScopes().get(_from);
    }
    
    if(_attribute != null && toExport != null){
      try{
    	  
        toExport = PropertyUtils.getProperty(toExport, _attribute);
      }catch(Exception e){
        st.error("Could not access property: " + _attribute + " on " + toExport, e);
        return;
      }
    }

    if(toExport != null) {
      Scope scope = (Scope) st.getContext().getScopes().get(_to);

      if(scope != null) {
        if(_expKey == null) {
          scope.putVal(_key, toExport);
        } else {
          scope.putVal(_expKey, toExport);
        }
      }
    }
  }
}
