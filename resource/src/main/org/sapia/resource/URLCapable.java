package org.sapia.resource;

import java.io.IOException;
import java.net.URL;

/**
 * This interface is implemented by {@link Resource} instances that
 * are "capable" of returning a {@link URL} corresponding to their underlying
 * resource.
 * 
 * @author yduchesne
 *
 */
public interface URLCapable {

  /**
   * @return the <code>URL</code> corresponding to this instance.
   * @throws IOException if an error occurs.
   */
  public URL getURL() throws IOException;
}
