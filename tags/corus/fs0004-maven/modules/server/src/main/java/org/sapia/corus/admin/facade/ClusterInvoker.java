package org.sapia.corus.admin.facade;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.sapia.corus.admin.Results;
import org.sapia.corus.core.ClusterInfo;

public class ClusterInvoker<M>{
  
  private CorusConnectionContext context;
  private Class<M> moduleInterface;
  private Method toInvoke;
  private Object[] args;
  
  public ClusterInvoker(CorusConnectionContext context){
    this.context = context;
  }

  public synchronized <T> void invokeLenient(Results<T> results, ClusterInfo info){
    try{
      context.invoke(results, moduleInterface, toInvoke.getName(), args, toInvoke.getParameterTypes(), info);
    }catch(RuntimeException e){
      throw e;
    }catch(Throwable e){
      throw new RuntimeException(e);
    }
  }

  public synchronized <T> void invoke(Results<T> results, ClusterInfo info) throws Throwable{
    context.invoke(results, moduleInterface, toInvoke.getName(), args, toInvoke.getParameterTypes(), info);
  }

  public synchronized <T> T invokeLenient(Class<T> returnType, ClusterInfo info){
    try{
      return context.invoke(returnType, moduleInterface, toInvoke, args, info);
    }catch(RuntimeException e){
      throw e;
    }catch(Throwable e){
      throw new RuntimeException(e);
    }
  }

  public synchronized <T> T invoke(Class<T> returnType, ClusterInfo info) throws Throwable{
    return context.invoke(returnType, moduleInterface, toInvoke, args, info);
  }
  
  public <T> T proxy(Class<T> moduleInterface){
    return moduleInterface.cast(Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{moduleInterface}, new ClusterInvokerHandler()));
  }
  
  class ClusterInvokerHandler implements InvocationHandler{
    
    @Override
    public synchronized Object invoke(Object proxy, Method someMethod, Object[] someArgs)
        throws Throwable {
      toInvoke = someMethod;
      args = someArgs;
      return null;
    }
  }
}
