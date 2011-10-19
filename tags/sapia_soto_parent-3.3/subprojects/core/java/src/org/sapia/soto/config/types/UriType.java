package org.sapia.soto.config.types;

import java.net.URI;
import java.net.URISyntaxException;

import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

public class UriType implements ObjectCreationCallback {

  private String _value;

  public void setValue(String value) {
    _value = value;
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    try{
      return new URI(_value);
    }catch(URISyntaxException e){
      throw new ConfigurationException("Could not parse URI: " + _value, e);
    }
  }

}
