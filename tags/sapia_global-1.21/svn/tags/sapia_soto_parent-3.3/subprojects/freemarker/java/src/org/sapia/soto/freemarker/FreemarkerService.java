package org.sapia.soto.freemarker;

import java.io.IOException;
import java.util.Locale;

import freemarker.template.Template;

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
public interface FreemarkerService {

  /**
   * Resolves the template corresponding to the given path.
   * 
   * @param path
   *          the path to a template.
   * @return a <code>Template</code>.
   * @throws IOException
   *           if the template could not be resolved.
   */
  public Template resolveTemplate(String path) throws IOException;

  /**
   * Resolves the template corresponding to the given path and locale.
   * 
   * @see #resolveTemplate(String)
   */
  public Template resolveTemplate(String path, Locale locale)
      throws IOException;

}
