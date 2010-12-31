package org.sapia.regis;

import java.util.Properties;

public interface RegistryFactory {
  
  /**
   * @param props the <code>Properties</code> to used to connect to the desired
   * <code>Registry</code>.
   * @return a <code>Registry</code>.
   * @throws Exception if a problem occurs while attempting to connect.
   */
  public Registry connect(Properties props) throws Exception;

}
