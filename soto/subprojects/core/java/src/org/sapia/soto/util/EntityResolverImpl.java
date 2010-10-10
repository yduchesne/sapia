package org.sapia.soto.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.sapia.soto.Env;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class EntityResolverImpl implements EntityResolver {
  private Env            _env;
  private EntityResolver _parent;

  public EntityResolverImpl(Env env) {
    _env = env;
  }

  public EntityResolverImpl(Env env, EntityResolver parent) {
    _env = env;
    _parent = parent;
  }

  /**
   * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String,
   *      java.lang.String)
   */
  public InputSource resolveEntity(String href, String base)
      throws SAXException, IOException {
    try {
      if(Utils.hasScheme(href)) {
        return new InputSource(_env.getResourceHandlerFor(href).getResource(
            href));
      } else {
        String uri;

        if(href == null) {
          uri = base;
        } else if(base == null) {
          uri = href;
        } else {
          uri = base + File.separator + href;
        }

        return new InputSource(_env.getResourceHandlerFor(uri).getResource(uri));
      }
    } catch(FileNotFoundException e) {
      if(_parent == null) {
        throw new SAXException("Could not resolve URI: " + href, e);
      }

      return _parent.resolveEntity(href, base);
    } catch(IOException e) {
      throw new SAXException("Could not resolve URI: " + href, e);
    }
  }
}
