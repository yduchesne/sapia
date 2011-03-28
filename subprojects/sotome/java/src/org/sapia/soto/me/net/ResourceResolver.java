package org.sapia.soto.me.net;

import java.io.IOException;


/**
 *
 * @author Jean-Cédric Desrochers
 */
public interface ResourceResolver {

  /**
   * Resolve the resource identified by the URI passed in. The succesful resolution will
   * result in the creation of a {@link Resource} object. 
   * 
   * @param aResourceUri The URI of the resource to resolve.
   * @return The created resource object.
   * @throws IOException If an error occurs resolving the resource.
   */
  public Resource resolveResource(String aResourceUri) throws IOException;

  /**
   * Resolve the resource identified by the URI passed in relative to the root resource. The succesful
   * resolution will result in the creation of a {@link Resource} object. 
   *
   * @param aRootResource The root resource from which the absolute path is derived.
   * @param aResourceUri The URI of the resource to resolve.
   * @return The created resource object.
   * @throws IOException If an error occurs resolving the resource.
   */
  public Resource resolveResource(Resource aRootResource, String aResourceUri) throws IOException;

}
