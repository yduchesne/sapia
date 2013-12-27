package org.sapia.soto.regis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.sapia.regis.Node;
import org.sapia.regis.Path;
import org.sapia.regis.Query;
import org.sapia.regis.RegisSession;
import org.sapia.regis.Registry;
import org.sapia.regis.bean.BeanFactory;
import org.sapia.soto.Env;
import org.sapia.soto.NotFoundException;
import org.sapia.soto.util.Param;
import org.sapia.resource.ResourceHandler;
import org.sapia.util.xml.confix.ConfigurationException;

public class RegistryUtils {
  
  public static Path copy(Iterator tokens){
    List list = new ArrayList();
    while(tokens.hasNext()){
      list.add(tokens.next());
    }
    return new Path(list);
  }
  
  static synchronized void registerHandler(Env env){
    ResourceHandler handler = env.getResourceHandlers().select(RegisResource.REGIS_SCHEME);
    if(handler == null || !(handler instanceof RegisResourceHandler)){
      env.getResourceHandlers().prepend(new RegisResourceHandler(env));
    }
  }
  
  static Node getNode(String path, Env env) throws NotFoundException{
    Path p = Path.parse(path);
    Iterator itr = p.tokens();
    if(!itr.hasNext()){
      throw new IllegalStateException("Invalid path: " + path + "; expecting: <serviceId>/<path>");
    }
    Registry reg = (Registry)env.lookup((String)itr.next());
    RegisSession s = reg.open();
    try{
      return reg.getRoot().getChild(copy(itr));
    }finally{
      s.close();
    }
  }
  
  static Object getBean(String path, Env env, Class intf) 
    throws NotFoundException, ConfigurationException{
    Path p = Path.parse(path);
    Iterator itr = p.tokens();
    if(!itr.hasNext()){
      throw new IllegalStateException("Invalid path: " + path + "; expecting: <serviceId>/<path>");
    }
    Registry reg = (Registry)env.lookup((String)itr.next());
    RegisSession s = reg.open();
    try{
      Node node = reg.getRoot().getChild(copy(itr));
      if(node == null){
        throw new ConfigurationException("No configuration node found for: " + path);
      }
      return BeanFactory.newBeanInstanceFor(reg, node, intf);
    }finally{
      s.close();
    }
  }  
  
  static Collection getNodes(String path, List params, Env env) throws NotFoundException{
    Path p = Path.parse(path);
    Iterator itr = p.tokens();
    if(!itr.hasNext()){
      throw new IllegalStateException("Invalid path: " + path + "; expecting: <serviceId>/<path>");
    }
    Registry reg = (Registry)env.lookup((String)itr.next());
    RegisSession s = reg.open();
    try{
      Query q = Query.create(copy(itr));
      for(int i = 0; i < params.size(); i++){
        Param crit = (Param)params.get(i);
        if(crit.getName() != null){
          q.addCrit(crit.getName(), (String)crit.getValue());
        }
      }
      return reg.getRoot().getNodes(q);
    }finally{
      s.close();
    }
  }  

}
