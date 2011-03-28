/**
 * 
 */
package org.sapia.soto.regis;

import org.sapia.regis.loader.NullObjectImpl;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public class TestProvider implements ObjectCreationCallback {
  
  private String _name;
  private String _address;
  
  /**
   * Returns the address value.
   *
   * @return The address value.
   */
  public String getAddress() {
    return _address;
  }
  
  /**
   * Changes the value of the address.
   *
   * @param aAddress The new address value.
   */
  public void setAddress(String aAddress) {
    _address = aAddress;
  }
  
  /**
   * Returns the name value.
   *
   * @return The name value.
   */
  public String getName() {
    return _name;
  }
  
  /**
   * Changes the value of the name.
   *
   * @param aName The new name value.
   */
  public void setName(String aName) {
    _name = aName;
  }

  /* (non-Javadoc)
   * @see org.sapia.util.xml.confix.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    if (TestServer.getInstance() != null) {
      TestServer.getInstance().addProvider(this);
    }
    
    return new NullObjectImpl();
  }
}
