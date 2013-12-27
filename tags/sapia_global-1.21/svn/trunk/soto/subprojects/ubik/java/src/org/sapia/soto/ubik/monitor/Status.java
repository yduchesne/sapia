package org.sapia.soto.ubik.monitor;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
import java.util.Properties;

/**
 * An instance of this class holds status information following a dynaming
 * method invocation performed by a <code>MonitorAgent</code>.
 * 
 * @author Yanick Duchesne
 *
 */
public class Status implements Externalizable{
  
  static final long serialVersionUID = 1L;
  
  private Exception _error;
  private String _serviceClassName, _serviceId;
  private Properties _resultProps;
  
  /**
   * DO NOT CALL. MEANT FOR EXTERNALIZATION.
   */
  public Status(){}
  
  public Status(String serviceClassName, String serviceId){
    _serviceClassName  = serviceClassName;
    _serviceId = serviceId;
    _resultProps = new Properties();
  }
  
  /**
   * @return the identifier of the service to which this instance corresponds 
   * (if the identifier od the service was not set - in the case of anonymous Soto
   * services, this method returns <code>null</code>).
   */
  public String getServiceId(){
    return _serviceId;
  }
  
  /**
   * @return the class name of the service to which this instance corresponds.
   */
  public String getServiceClassName(){
    return _serviceClassName;
  }

  /**
   * @return an <code>Exception</code> if an error occured while performing
   * the status invocation, or <code>null</code> if no error occurred.
   */
  public Exception getError(){
    return _error;
  }
  
  /**
   * @return <code>true</code> if an error occurred while performing
   * the status invocation.
   * 
   * @see #getError()
   */
  public boolean isError(){
    return _error != null;
  }

  public void setError(Exception e){
    _error = e;
  }
  
  public Properties getResultProperties() {
    return _resultProps;
  }
  
  public String getResultProperty(String aName) {
    return _resultProps.getProperty(aName);
  }
  
  public void setResultProperty(String aName, String aValue) {
    _resultProps.setProperty(aName, aValue);
  }
  
  public void addResultProperties(Properties someProps) {
    if (someProps != null) {
      for (Iterator it = someProps.keySet().iterator(); it.hasNext(); ) {
        String aName = (String) it.next();
        String aValue = someProps.getProperty(aName);
        _resultProps.setProperty(aName, aValue);
      }
    }
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    _error = (Exception) in.readObject();
    _serviceClassName = (String) in.readObject();
    _serviceId = (String) in.readObject();
    _resultProps = (Properties) in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(_error);
    out.writeObject(_serviceClassName);
    out.writeObject(_serviceId);
    out.writeObject(_resultProps);
  }
}
