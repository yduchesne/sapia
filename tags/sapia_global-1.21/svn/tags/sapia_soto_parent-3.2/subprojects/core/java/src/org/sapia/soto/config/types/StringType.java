package org.sapia.soto.config.types;

import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

public class StringType  implements ObjectCreationCallback {

  private String _value;

  public void setValue(String value) {
    _value = value;
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    return _value;
  }
}
