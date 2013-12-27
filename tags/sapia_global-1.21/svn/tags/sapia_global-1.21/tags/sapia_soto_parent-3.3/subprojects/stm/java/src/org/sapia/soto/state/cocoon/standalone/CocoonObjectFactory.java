package org.sapia.soto.state.cocoon.standalone;

import org.apache.cocoon.environment.http.HttpContext;
import org.apache.cocoon.environment.http.HttpEnvironment;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
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
public class CocoonObjectFactory {
  public static final String DEFAULT_ENCODING = "ISO-8859-1";

  public static HttpEnvironment getEnvironment(HttpServletRequest req,
      HttpServletResponse res, ServletContext ctx, String containerEncoding,
      String defaultFormEncoding) throws IOException {
    HttpEnvironment env = new HttpEnvironment(getUriFromRequest(req),
        getRootFromContext(ctx), req, res, ctx, new HttpContext(ctx),
        (containerEncoding == null) ? DEFAULT_ENCODING : containerEncoding,
        (defaultFormEncoding == null) ? DEFAULT_ENCODING : defaultFormEncoding);

    return env;
  }

  private static String getUriFromRequest(HttpServletRequest request) {
    String uri = request.getServletPath();

    if(uri == null) {
      uri = "";
    }

    String pathInfo = request.getPathInfo();

    if(pathInfo != null) {
      // VG: WebLogic fix: Both uri and pathInfo starts with '/'
      // This problem exists only in WL6.1sp2, not in WL6.0sp2 or WL7.0b.
      if((uri.length() > 0) && (uri.charAt(0) == '/')) {
        uri = uri.substring(1);
      }

      uri += pathInfo;
    }

    //    if (uri.length() == 0) {
    //      /* empty relative URI
    //           -> HTTP-redirect from /cocoon to /cocoon/ to avoid
    //              StringIndexOutOfBoundsException when calling
    //              "".charAt(0)
    //         else process URI normally
    //      */
    //      String prefix = request.getRequestURI();
    //      if (prefix == null) {
    //          prefix = "";
    //      }
    //
    //      res.sendRedirect(res.encodeRedirectURL(prefix + "/"));
    //      return;
    //    }
    if(uri.charAt(0) == '/') {
      uri = uri.substring(1);
    }

    return uri;
  }

  private static String getRootFromContext(ServletContext ctx)
      throws IOException {
    String servletContextPath = ctx.getRealPath("/");

    //    // first init the work-directory for the logger.
    //    // this is required if we are running inside a war file!
    //    final String workDirParam = getInitParameter("work-directory");
    //    if (workDirParam != null) {
    //        if (this.servletContextPath == null) {
    //            // No context path : consider work-directory as absolute
    //            this.workDir = new File(workDirParam);
    //        } else {
    //            // Context path exists : is work-directory absolute ?
    //            File workDirParamFile = new File(workDirParam);
    //            if (workDirParamFile.isAbsolute()) {
    //                // Yes : keep it as is
    //                this.workDir = workDirParamFile;
    //            } else {
    //                // No : consider it relative to context path
    //                this.workDir = new File(servletContextPath , workDirParam);
    //            }
    //        }
    //    } else {
    //        this.workDir = (File)
    // this.servletContext.getAttribute("javax.servlet.context.tempdir");
    //        this.workDir = new File(workDir, "cocoon-files");
    //    }
    //    this.workDir.mkdirs();
    //
    //    initLogger();
    String path = servletContextPath;

    if(path == null) {
      try {
        URL url = ctx.getResource("/WEB-INF");

        if(url != null) {
          path = url.toExternalForm();
        } else {
          path = new File(System.getProperty("user.dir")).toURL()
              .toExternalForm();
        }
      } catch(MalformedURLException me) {
        throw new MalformedURLException("Unable to get resource 'WEB-INF' - "
            + me.getMessage());
      }

      path = path.substring(0, path.length() - "WEB-INF".length());
    }

    /*String servletContextURL;

    try {
      if(path.indexOf(':') > 1) {
        servletContextURL = path;
      } else {
        servletContextURL = new File(path).toURL().toExternalForm();
      }
    } catch(MalformedURLException me) {
      try {
        servletContextURL = new File(path).toURL().toExternalForm();
      } catch(MalformedURLException ignored) {
        throw new MalformedURLException(
            "Unable to determine servlet context URL. - " + me.getMessage());
      }
    }*/

    return servletContextPath;
  }
}
