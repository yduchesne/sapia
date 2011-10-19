package org.sapia.soto;

/**
 * This interface can be implemented as a callback to receive naming information
 * from the container.
 *  
 * @author yduchesne
 *
 */
public interface XmlAware { 

  /**
   * @param name the local name of the corresponding this instance.
   * @param prefix the XML namespace prefix of this instance.
   * @param uri the XML uri of this instance.
   */
  public void setNameInfo(String name, String prefix, String uri);
}
