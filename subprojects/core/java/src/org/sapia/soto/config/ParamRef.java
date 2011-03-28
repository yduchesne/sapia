package org.sapia.soto.config;

import org.sapia.resource.include.IncludeState;
import org.sapia.soto.SotoConsts;
import org.sapia.util.text.SystemContext;
import org.sapia.util.text.TemplateContextIF;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2005 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class ParamRef implements ObjectCreationCallback, SotoConsts{
  
  private TemplateContextIF _vars; 
  private String _name;
  
  public ParamRef(){
    SotoIncludeContext ctx = (SotoIncludeContext)IncludeState.currentContext(SOTO_INCLUDE_KEY);
    if(ctx == null){
      _vars = new SystemContext();
    }
    else{
      _vars = ctx.getTemplateContext();
    }
  }
  
  public void setName(String name){
    _name = name;
  }
  
  /**
   * @see org.sapia.util.xml.confix.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    if(_name == null){
      return null;
    }
    return _vars.getValue(_name);
  }

}
