package org.sapia.soto.config;

import java.util.ArrayList;
import java.util.List;

import org.sapia.soto.Attribute;
import org.sapia.soto.AttributeServiceSelector;
import org.sapia.soto.Env;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

public class SelectTag implements ObjectCreationCallback{
  
  private List _attributes = new ArrayList();
  private Env _env;

  public SelectTag(Env env){
    _env = env;
  }
  public Attribute createAttribute(){
    Attribute attr = new Attribute();
    _attributes.add(attr);
    return attr;
  }
  
  public Object onCreate() throws ConfigurationException {
    AttributeServiceSelector selector = new AttributeServiceSelector();
    for(int i = 0; i < _attributes.size(); i++){
      selector.addCriteria((Attribute)_attributes.get(i));
    }
    return _env.lookup(selector, false);
  }

}
