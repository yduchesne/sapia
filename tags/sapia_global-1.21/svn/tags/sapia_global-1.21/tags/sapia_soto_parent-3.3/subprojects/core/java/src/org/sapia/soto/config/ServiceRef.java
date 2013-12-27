package org.sapia.soto.config;

import org.sapia.soto.Debug;
import org.sapia.soto.NotFoundException;
import org.sapia.soto.SotoContainer;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 * This class implements the <code>soto:serviceRef</code> tag.
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
public class ServiceRef implements ObjectCreationCallback {
  private String        _id;
  private String        _class;
  private SotoContainer _cont;

  /**
   * Constructor for ServiceRef.
   */
  public ServiceRef(SotoContainer container) {
    _cont = container;
  }

  /**
   * Sets the ID of the service to which this instance refers.
   */
  public void setId(String id) {
    _id = id;
  }
  
  /**
   * Sets the class of the service to which this instance refers.
   */
  public void setClass(String className){
    _class = className;
  }
  
  /**
   * @see #setClass(String)
   */
  public void setInstanceOf(String className){
    setClass(className);
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    try {
      Object toReturn;
      if(_id != null){
        toReturn = _cont.lookup(_id);
      }
      else{
        try{
          toReturn = _cont.lookup(Class.forName(_class));
        }catch(Exception e){
          throw new ConfigurationException("Could not lookup service for class: "
              + _class);
        }
      }
      if(Debug.DEBUG){
        Debug.debug("ServiceRef resolved to " + toReturn  + " for: " + _id);
      }
      return toReturn;
    } catch(NotFoundException e) {
      if(Debug.DEBUG){
        Debug.debug(e);
      }
      throw new ConfigurationException("Could not retrieve service", e);
    }
  }
}
