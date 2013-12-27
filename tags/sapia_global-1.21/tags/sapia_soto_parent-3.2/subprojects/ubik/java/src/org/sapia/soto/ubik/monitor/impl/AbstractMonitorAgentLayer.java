package org.sapia.soto.ubik.monitor.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.sapia.soto.Layer;
import org.sapia.soto.ServiceMetaData;
import org.sapia.soto.ubik.monitor.FeedbackMonitorable;
import org.sapia.soto.ubik.monitor.MonitorAgent;
import org.sapia.soto.ubik.monitor.Monitorable;
import org.sapia.soto.ubik.monitor.Status;
import org.sapia.soto.util.Utils;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;
import org.sapia.util.xml.confix.ObjectHandlerIF;

/**
 * This class implements the {@link MonitorAgent} interface as a {@link Layer}. An instance of this
 * class can be configured to perform status checks by calling a specified method through Java reflection,
 * or by recognizing {@link Monitorable} and {@link FeedbackMonitorable} instances. 
 * <p>
 * If no method has been configured to perform status checks using Java reflection, an instance of this class
 * will attempt figuring out if the service to which it corresponds implements one of the above-mentioned 
 * interface. If not, the instance will throw an exception at initialization time, indicating that status 
 * checks cannot be performed given the service instance at hand, and the omitted dynamic method configuration.
 *  
 * @author yduchesne
 */
public abstract class AbstractMonitorAgentLayer implements Layer, MonitorAgent, ObjectHandlerIF{
  
  private static final String FIELD_ID = "id";  
  
  protected MethodDescriptor _method = null;
  private ServiceMetaData _meta;  
  private String _id;  
  
  /**
   * @return a <code>MethodDescriptor</code> whose attributes must be set.
   */
  public MethodDescriptor createInvoke(){
    _method = new MethodDescriptor();
    return _method;
  }
  
  /**
   * @param methodName the name of the method to invoke.
   */
  public void setInvoke(String methodName) {
    _method = new MethodDescriptor();
    _method.setName(methodName);
  }
  
  public void setId(String id){
    _id = id;
  }
  
  /////// Layer Interface

  public void init(ServiceMetaData meta) throws Exception {
    _meta = meta;    
    if (_method == null) {
      if(meta.getService() instanceof Monitorable || 
         meta.getService() instanceof FeedbackMonitorable){
        //noop
      }
      else{
        throw new IllegalStateException("Method not set");
      }
    }
    else{
      _method.init(meta);
    }
      
  }
  
  public void start(ServiceMetaData meta) throws Exception {
  }
  
  public void dispose() {
  }
  
  /////// MonitorAgent interface
  
  public Status checkStatus() throws RemoteException {
    Status stat = new Status(
        _meta.getService().getClass().getName(), 
        _id != null ? _id : _meta.getServiceID()
      );
        
    if(_method == null){
      if (_meta.getService() instanceof FeedbackMonitorable) {
        try {
          Properties result = ((FeedbackMonitorable)_meta.getService()).monitor();
          if(result == null){
            result = new Properties();
          }
          stat.addResultProperties(postProcess(result));
        } catch (Exception e) {
          Exception error = new Exception(
                  "Monitoring error: " +
                  e.getMessage(), e);
          stat.setError(error);
        }
        
      } else if (_meta.getService() instanceof Monitorable) {
        try {
          ((Monitorable)_meta.getService()).monitor();
        } catch (Exception e) {
          Exception error = new Exception(
                  "Monitoring error: " +
                  e.getMessage(), e);
          stat.setError(error);
        }        
      
      } else {
        throw new IllegalStateException("Method to invoke not set and service not" +
            " instance of Monitorable: " + _meta.getService());
      }
      
    } else {
      try {
        Object result = _method.invoke();
        
        // Add the received properties (if any...) to the status object
        if (result != null && result instanceof Properties) {
          stat.addResultProperties(postProcess((Properties) result));
        }
        
      } catch (Exception e) {
        String msg;
        if(e instanceof InvocationTargetException){
          InvocationTargetException ite = (InvocationTargetException)e;
          msg = ite.getMessage() + " (" + e.getClass().getName() + ")";
        }
        else{
          msg = e.getMessage();
        }
        Exception error = new Exception("Monitoring error: " +
                _method.getName() + " - " + msg, e);
        stat.setError(error);
      }
    }
    return stat;
  }
  
  public void handleObject(String name, Object obj) throws ConfigurationException {
    if(name.equals(FIELD_ID)){
      if(obj instanceof String){
        _id = (String)obj;
      }
      else{
        throw new ConfigurationException(
          Utils.createInvalidAssignErrorMsg(name, obj, String.class)
        );
      }
    }
  }
  
  /**
   * @param result status check result properties that are to be processed by inheriting
   * classes (if required).
   * 
   * @return the post-processed <code>Properties</code>.
   */
  protected abstract Properties postProcess(Properties result);

  /////// INNER CLASSES ///////
  
  public static class MethodDescriptor implements ObjectCreationCallback{
    
    private String name;
    private List args = new ArrayList();
    private Method method;
    private ServiceMetaData meta;
    private Object[] argVals;
    
    public Arg createArg(){
      Arg a = new Arg();
      args.add(a);
      return a;
    }
    public List getArgs(){
      return args;
    }
    public String getName() {
      return name;
    }
    public void setName(String name) {
      this.name = name;
    }
    public Object onCreate() throws ConfigurationException {
      if(name == null){
        throw new ConfigurationException("Method name not set");
      }
      return this;
    }
    public void init(ServiceMetaData meta) throws Exception{
      this.meta = meta;
      argVals  = new Object[args.size()];
      Class[] argTypes = new Class[args.size()];
      for(int i = 0; i < argVals.length; i++){
        Arg arg = (Arg)args.get(i);
        argVals[i] = arg.getValue(); 
        argTypes[i] = arg.getType();
      }
      
      try{
        method = meta.getService().getClass().getDeclaredMethod(name, argTypes);
      }catch(NoSuchMethodException e){
        if(args.size() == 0){
          throw new ConfigurationException("Could not find method: " + name
              + " on " + meta.getService());        
        }
        else{
          throw new ConfigurationException("Could not find method: " + name
            + " with specified parameter types on " + meta.getService() + ": " + args);
        }
      }
      
    }
    public Object invoke() throws Exception{
      return method.invoke(meta.getService(), argVals);
    }
  }
  
  public static class Arg implements ObjectCreationCallback{
    
    private Object value;
    private Class type;
    
    public Class getType() {
      return type;
    }
    public void setType(Class type) {
      this.type = type;
    }
    public void setType(String type) throws Exception{
      this.type = Class.forName(type);
    }    
    public Object getValue() {
      return value;
    }
    public void setValue(Object value) {
      this.value = value;
    }
    public Object onCreate() throws ConfigurationException {
      if(type == null){
        if(value != null){
          type = value.getClass();
        }
        else{
          throw new ConfigurationException("Method argument type not set");
        }
      }
      return this;
    }
    public String toString() {
      return "[value=" + value + ", type=" + type + "]";
    }
  }
}
