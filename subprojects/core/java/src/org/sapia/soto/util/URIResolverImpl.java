package org.sapia.soto.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.sapia.soto.Env;

/**
 * Implements JAXP's <code>URIResolver</code> over an <code>Env</code>
 * instance.
 * 
 * @see org.sapia.soto.Env
 * @see org.sapia.soto.Env#getResourceHandlerFor(String)
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
public class URIResolverImpl implements URIResolver {
  private Env         _env;
  private URIResolver _parent;

  public URIResolverImpl(Env env) {
    _env = env;
  }

  public URIResolverImpl(Env env, URIResolver parent) {
    _env = env;
    _parent = parent;
  }

  /**
   * @see javax.xml.transform.URIResolver#resolve(java.lang.String,
   *      java.lang.String)
   */
  public Source resolve(String href, String base) throws TransformerException {
    try {
      if(Utils.hasScheme(href)) {
        return new StreamSource(_env.getResourceHandlerFor(
            Utils.getScheme(href)).getResource(href));
      } else {
        String uri;

        if(href == null) {
          uri = base;
        } else if(base == null) {
          uri = href;
        } else {
          uri = base + File.separator + href;
        }

        return new StreamSource(_env.getResourceHandlerFor(uri)
            .getResource(uri));
      }
    } catch(FileNotFoundException e) {
      if(_parent == null) {
        throw new TransformerException("Could not resolve URI: " + href, e);
      }

      return _parent.resolve(href, base);
    } catch(IOException e) {
      throw new TransformerException("Could not resolve URI: " + href, e);
    }
  }
}
