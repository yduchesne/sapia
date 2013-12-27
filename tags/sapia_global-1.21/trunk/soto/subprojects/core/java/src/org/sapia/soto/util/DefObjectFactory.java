package org.sapia.soto.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.CreationStatus;
import org.sapia.util.xml.confix.ObjectCreationException;
import org.sapia.util.xml.confix.ReflectionFactory;

/**
 * An object factory that creates objects based on definitions.
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
public class DefObjectFactory extends ReflectionFactory {
  private Map _defs = new HashMap();
  private List _packages = new ArrayList();
  private ObjectFactoryDelegate _delegate; 

  /**
   * Constructor for ReflectObjectFactory.
   */
  public DefObjectFactory() {
    super(new String[0]);
  }

  /**
   * Adds the given definition to this instance.
   * 
   * @param def
   *          a <code>Def</code> instance.
   */
  public void addDef(Def def) throws ConfigurationException {
    if(_defs.get(def.getName()) != null) {
      throw new ConfigurationException("Definition already declared: "
          + def.getName() + ", " + def.getClazz());
    }

    _defs.put(def.getName(), def);
  }

  /**
   * Adds the given package name to this instance. 
   */
  public void addPackage(String pckg){
    _packages.add(pckg);
  }
  
  public void setDelegate(ObjectFactoryDelegate delegate) throws ConfigurationException{
    _delegate = delegate;
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectFactoryIF#newObjectFor(String, String,
   *      String, Object)
   */
  public CreationStatus newObjectFor(String prefix, String uri, String name,
      Object parent) throws ObjectCreationException {
    if((prefix == null) || (prefix.length() == 0)) {
      return super.newObjectFor(prefix, uri, name, parent);
    }

    Def def = (Def) _defs.get(name);

    // attempt package matching
    if(def == null){
      for(int i = 0; i < _packages.size(); i++){
        Class clazz = findClass(name, (String)_packages.get(i));
        if(clazz != null){
          return doCreate(clazz, name);
        }
      }
    }

    if(def == null) {
      Iterator defs = _defs.values().iterator();
      while(defs.hasNext()){
        def = (Def)defs.next();
        if(def.matches(name)){
          return doCreate(def, name);
        }
      }
      
      throw new ObjectCreationException("Unknown element: " + name);
    } else {
      return doCreate(def, name);
    }
  }
  
  private CreationStatus doCreate(Def def, String name) throws ObjectCreationException{
    if(_delegate != null){
      try{
        return CreationStatus.create(_delegate.newInstance(def.getClazz()));
      }catch(Exception e){
        throw new ObjectCreationException("Could not create instance of " + def.getClazz()
            + " with :" + _delegate);
      }
    }
    else{
      try {
        return doCreate(Class.forName(def.getClazz()), name);
      } catch(ClassNotFoundException e) {
        throw new ObjectCreationException(
            "Could not create object for " + name, e);
      }
    }
  }
  
  private CreationStatus doCreate(Class clazz, String name) throws ObjectCreationException{
    try {
      return CreationStatus.create(clazz.newInstance());
    } catch(Exception e) {
      throw new ObjectCreationException(
          "Could not create object for " + name, e);
    }
  }  
  
  private Class findClass(String name, 
                          String packageName){
    StringBuffer buf = new StringBuffer(packageName).append(".")
    		.append(Character.toUpperCase(name.charAt(0)))
			.append(name.substring(1));
    try{
      return Class.forName(buf.toString());
    }catch(Throwable e){
      return null;
    }
  }
  
  
}
