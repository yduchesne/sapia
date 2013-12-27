package org.sapia.soto.util;

import java.util.ArrayList;
import java.util.List;

import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 * Models a namespaces - that encapsulates definitions.
 * 
 * @see org.sapia.soto.util.Def
 * 
 * @author Yanick Duchesne
 *         <dl>
 *         <dt><b>Copyright: </b>
 *         <dd>Copyright &#169; 2002-2003 <a
 *         href="http://www.sapia-oss.org">Sapia Open Source Software </a>. All
 *         Rights Reserved.</dd>
 *         </dt>
 *         <dt><b>License: </b>
 *         <dd>Read the license.txt file of the jar or visit the <a
 *         href="http://www.sapia-oss.org/license.html">license page </a> at the
 *         Sapia OSS web site</dd>
 *         </dt>
 *         </dl>
 */
public class Namespace implements EnvAware, ObjectCreationCallback{
  private String                   _prefix;
  private ObjectFactoryDelegate    _delegate;
  private String                   _delegateClass, _delegateId;
  private List                     _defs = new ArrayList(5);
  private List                     _packages = new ArrayList(2);
  private Env                      _env;
  
  /**
   * Constructor for Defs.
   */
  public Namespace() {
  }
  
  public void setEnv(Env env) {
    _env = env;
  }

  /**
   * Sets the XML namespace prefix to which this instance corresponds.
   * 
   * @param prefix
   *          a namespace prefix.
   */
  public void setPrefix(String prefix) {
    _prefix = prefix;
  }

  public String getPrefix() {
    return _prefix;
  }
  
  public Def addDef() {
    Def def = new Def();

    _defs.add(def);

    return def;
  }
  
  public List getDefs() {
    return _defs;
  }
  
  public void addPackage(String pckg){
    _packages.add(pckg);
  }
  
  public List getPackages() {
    return _packages;
  }
  
  public void setDelegateInterface(String delegateClass){
    _delegateClass = delegateClass;
  }  
  
  public void setDelegateId(String delegateId){
    _delegateId = delegateId;
  }
  
  public void setDelegate(ObjectFactoryDelegate delegate){
    _delegate = delegate;
  }
  
  public ObjectFactoryDelegate getDelegate(){
    return _delegate;
  }
  
  public Object onCreate() throws ConfigurationException {
    if(_delegate == null){
      if(_delegateId != null){
        try{
          _delegate = (ObjectFactoryDelegate)_env.lookup(_delegateId);
        }catch(Exception e){
          throw new ConfigurationException("Could not lookup ObjectFactoryDelegate for: " + _delegateId);
        }
      }
      else if(_delegateClass != null) {
        try{
          _delegate = (ObjectFactoryDelegate)_env.lookup(Class.forName(_delegateClass));
        }catch(Exception e){
          throw new ConfigurationException("Could not lookup ObjectFactoryDelegate for: " + _delegateClass);
        }
      }
    }
    return this;
  }
  
}
