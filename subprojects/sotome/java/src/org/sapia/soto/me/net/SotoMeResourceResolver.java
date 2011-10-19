package org.sapia.soto.me.net;

import java.io.IOException;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public class SotoMeResourceResolver implements ResourceResolver {

  /**
   * Creates a new SotoMeResourceResolver instance.
   */
  public SotoMeResourceResolver() {
  }
  
  /* (non-Javadoc)
   * @see org.sapia.soto.me.resource.ResourceResolver#resolveResource(java.lang.String)
   */
  public Resource resolveResource(String aResourceUri) throws IOException {
    return UriResource.parseString(aResourceUri);
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.net.ResourceResolver#resolveResource(org.sapia.soto.me.net.Resource, java.lang.String)
   */
  public Resource resolveResource(Resource aRootResource, String aResourceUri) throws IOException {
    UriResource resource = UriResource.parseString(aResourceUri);
    if (!resource.isAbsolute()) {
      
    }
    
    return resource;
  }

}
