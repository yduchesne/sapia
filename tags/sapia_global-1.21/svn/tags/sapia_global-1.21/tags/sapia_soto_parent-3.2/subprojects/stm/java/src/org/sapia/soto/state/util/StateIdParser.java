package org.sapia.soto.state.util;

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
public class StateIdParser {
  private static final char SEP = '/';
  private static final char DOT = '.';

  public static Created parseStateFrom(String pathInfo, String extension) {
    if(pathInfo.length() == 0) {
      return null;
    }

    if(pathInfo.charAt(0) == SEP) {
      if(pathInfo.length() == 1) {
        return null;
      }

      pathInfo = pathInfo.substring(1);

      if(pathInfo.length() == 0) {
        return null;
      }
    }

    int i;
    String path = null;

    if((i = pathInfo.lastIndexOf(SEP)) > 0) {
      path = pathInfo.substring(0, i);
      pathInfo = pathInfo.substring(i + 1);
    }

    if(extension != null) {
      i = pathInfo.lastIndexOf(DOT);

      if((i > 0) && extension.equals(pathInfo.substring(i + 1))) {
        pathInfo = pathInfo.substring(0, i);
      }
    }

    i = pathInfo.lastIndexOf(DOT);

    if(i > 0) {
      String module = pathInfo.substring(0, i);

      if(path != null) {
        pathInfo = new StringBuffer(path).append(SEP).append(
            pathInfo.substring(i + 1)).toString();
      } else {
        pathInfo = pathInfo.substring(i + 1);
      }

      return new Created(pathInfo, module);
    } else {
      if(path != null) {
        pathInfo = new StringBuffer(path).append(SEP).append(
            pathInfo.substring(i + 1)).toString();
      } else {
        pathInfo = pathInfo.substring(i + 1);
      }

      return new Created(pathInfo, null);
    }
  }

  public static class Created {
    public final String state;
    public final String module;

    Created(String state, String module) {
      this.state = state;
      this.module = module;
    }
  }
}
