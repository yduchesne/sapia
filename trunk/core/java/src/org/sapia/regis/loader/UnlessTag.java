package org.sapia.regis.loader;

import org.sapia.util.text.TemplateContextIF;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;
import org.sapia.util.xml.confix.ObjectFactoryIF;

public class UnlessTag extends Condition implements ObjectCreationCallback, TagFactory {
  public UnlessTag() {
    super("unless");
  }

  public Object onCreate() throws ConfigurationException {
    return super.create();
  }
  
  public boolean isEqual() {
    return !super.isEqual();
  }
  
  public Object create(TemplateContextIF context, ObjectFactoryIF fac) throws Exception {
    super.init(context, fac);
    return this;
  }
}
