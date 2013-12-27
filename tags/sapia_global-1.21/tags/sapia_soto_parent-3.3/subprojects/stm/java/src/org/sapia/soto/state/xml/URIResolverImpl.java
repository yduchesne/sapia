/*
 * URIResolverImpl.java
 *
 * Created on April 6, 2005, 2:36 PM
 */

package org.sapia.soto.state.xml;

import java.io.IOException;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.sapia.soto.Debug;
import org.sapia.soto.Env;

/**
 *
 * @author yduchesne
 */
public class URIResolverImpl implements URIResolver{
  
  private Env _env;
  
  /** Creates a new instance of URIResolverImpl */
  public URIResolverImpl(Env env) {
    _env = env;
  }
  
  public Source resolve(String href, String base) throws TransformerException{
    try{
      if(Debug.DEBUG){
        Debug.debug(getClass(), "Including: " + base + "/" + href);
      }
      return new StreamSource(_env.resolveStream(href));
    }catch(IOException e){
      throw new TransformerException("Could not resolve resource: " + href);
    }
  }
  
}
