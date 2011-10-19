package org.sapia.soto.state.cocoon.simple;

import java.io.OutputStream;

import org.sapia.soto.util.matcher.UriPattern;
import org.sapia.util.text.MapContext;
import org.sapia.util.text.SystemContext;
import org.sapia.util.text.TemplateContextIF;
import org.sapia.util.text.TemplateElementIF;
import org.sapia.util.text.TemplateFactory;

import simple.http.serve.Context;
import gnu.trove.THashMap;

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
public class ResourceMatcher {

  private static final String START_DELIM = "{";
  private static final String END_DELIM   = "}";

  private UriPattern          _pattern;
  private String              _resource;

  private THashMap            _cache      = new THashMap();

  public void setPattern(String pattern) {
    _pattern = UriPattern.parse(pattern);
  }

  public void setTarget(String resource) {
    _resource = resource;
  }

  public boolean matches(String path, OutputStream out, Context ctx)
      throws Exception {
    if(_pattern == null) {
      throw new IllegalStateException("Pattern not specified");
    }

    String target;
    if((target = (String) _cache.get(path)) != null) {
      ctx.getContent(target).write(out);
      return true;
    } else if(_resource == null) {
      UriPattern.MatchResult res = _pattern.matchResult(path);
      if(res.matched) {
        _cache.put(path, path);
        ctx.getContent(path).write(out);
        return true;
      }
      return false;
    } else {
      UriPattern.MatchResult res = _pattern.matchResult(path);
      if(res.matched) {
        TemplateFactory fac = new TemplateFactory(START_DELIM, END_DELIM);
        TemplateElementIF template = fac.parse(_resource);
        TemplateContextIF templateCtx = new MapContext(res.result,
            new SystemContext(), false);
        target = template.render(templateCtx);
        if(target.charAt(0) != '/') {
          target = '/' + target;
        }
        _cache.put(path, target);
        ctx.getContent(target).write(out);
        return true;
      } else {
        return false;
      }
    }
  }

}
