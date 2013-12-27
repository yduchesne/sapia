package org.sapia.regis.loader;

import org.sapia.util.text.TemplateContextIF;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;
import org.sapia.util.xml.confix.ObjectFactoryIF;

public class IfTag extends Condition implements ObjectCreationCallback, TagFactory {
  
  public IfTag(){
    super("if");
  }
  
  public Object onCreate() throws ConfigurationException {
    return super.create();
  }

  public Object create(TemplateContextIF context, ObjectFactoryIF fac) throws Exception {
    super.init(context, fac);
    return this;
  }  
}
