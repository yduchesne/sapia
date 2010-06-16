package org.sapia.corus.admin.facade.impl;

import org.sapia.corus.admin.facade.ClusterInvoker;
import org.sapia.corus.admin.facade.CorusConnectionContext;

public class FacadeHelper<M> {
  
  protected CorusConnectionContext context;
  protected ClusterInvoker<M> invoker;
  protected M proxy;
  
  public FacadeHelper(CorusConnectionContext context, Class<M> moduleInterface) {
    this.context = context;
    invoker = new ClusterInvoker<M>(context);
    invoker.proxy(moduleInterface);
  }

}
