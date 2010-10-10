package org.sapia.regis.loader;

import org.sapia.util.text.TemplateContextIF;
import org.sapia.util.xml.confix.ObjectFactoryIF;

public interface TagFactory {
  
  public Object create(TemplateContextIF context, ObjectFactoryIF fac) throws Exception;

}
