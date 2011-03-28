package org.sapia.soto.regis;

import org.sapia.regis.Node;
import org.sapia.soto.config.SotoIncludeContext;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

public class NodeRefTag implements 
  ObjectCreationCallback, RegistryConsts{
  
  public Object onCreate() throws ConfigurationException {
    Node node = (Node)SotoIncludeContext.currentTemplateContext().getValue(NODE_REF);
    if(node == null){
      throw new ConfigurationException("Current node not found");
    }
    return node;
  }

}
