package org.sapia.regis.bean;

import java.lang.reflect.Proxy;

import org.sapia.regis.Node;
import org.sapia.regis.Registry;
import org.sapia.regis.RegistryProvider;

/**
 * An instance of this class allows wrapping a <code>Node</code> in a dynamic proxy
 * that implements a given interface. The interface must have getter methods that correspond
 * to the name of the properties on the given node.
 * 
 * @author yduchesne
 *
 */
public class BeanFactory {
  
  /**
   * @param reg the <code>Registry</code> from which the given node originates.
   * @param node a <code>Node</code>.
   * @param interf a user-defined interface.
   * @return
   */
  public static Object newBeanInstanceFor(Registry reg, Node node, Class interf){
    NodeInvocationHandler handler = new NodeInvocationHandler(reg, node, interf);
    return newBeanInstanceFor(node, interf, handler);
  }
  
  static Object newBeanInstanceFor(Node node, Class interf, NodeInvocationHandler handler){
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    if(loader == null){
      loader = BeanFactory.class.getClassLoader();
    }
    return Proxy.newProxyInstance(loader, new Class[]{interf, RegistryProvider.class}, handler);
  }  

}
