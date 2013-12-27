package org.sapia.soto.util;

import java.util.HashMap;

import org.sapia.soto.util.matcher.UriPattern;
import org.sapia.util.text.MapContext;
import org.sapia.util.text.SystemContext;
import org.sapia.util.text.TemplateContextIF;
import org.sapia.util.text.TemplateElementIF;
import org.sapia.util.text.TemplateException;
import org.sapia.util.text.TemplateFactory;

public class ResourceAlias {
  
  static final TemplateContextIF EMPTY_CONTEXT = new MapContext(new HashMap(), new SystemContext(), false);
  private UriPattern _pattern;
  private String _uri;
  private TemplateElementIF _target;
    
  public void setUri(String uri){
    if(uri.indexOf('*') < 0){
      _uri = uri;
    }
    _pattern = UriPattern.parse(uri);
  }
  
  public void setRedirect(String uri){
    TemplateFactory fac = new TemplateFactory("{", "}");
    _target = fac.parse(uri);
  }
  
  public String match(String uri) throws TemplateException{
    if(_target != null && _pattern != null){
      if(_uri != null){
        if(!_uri.equals(uri)){
          return null;
        }
        return _target.render(EMPTY_CONTEXT);
      }
 
      UriPattern.MatchResult result = _pattern.matchResult(uri);
      if(result.matched){
        return _target.render(new MapContext(result.result, new SystemContext(), false));
      }
      else{
        return null;
      }
    }
    else{
      return null;
    }
  }
}
