package org.sapia.soto.ubik.monitor.impl;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.sapia.soto.ubik.monitor.MonitorAgent;
import org.sapia.soto.ubik.monitor.Status;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 * This agent monitors a JDBC connection.
 * 
 * @author yduchesne
 *
 */
public class JdbcMonitorAgent implements MonitorAgent, ObjectCreationCallback{
  
  private String _driver, _id, _serviceClass;
  private String _url;
  private String _username;
  private String _password = "";

  public void setServiceClass(String clazz){
    _serviceClass = clazz;
  }
  
  public void setServiceId(String id){
    _id = id;
  }
  
  public void setDriverClass(String driver){
    _driver = driver;
  }
  
  public void setUrl(String url){
    _url = url;
  }
  
  public void setPassword(String pwd){
    _password = pwd;
  }
  
  public void setUsername(String username){
    _username = username;
  }
  
  public Object onCreate() throws ConfigurationException {
    if(_username == null){
      throw new IllegalStateException("username not set");
    }
    if(_url == null){
      throw new IllegalStateException("url not set");
    } 
    if(_driver == null){
      throw new IllegalStateException("driver class not set");
    }     
    if(_id == null){
      throw new IllegalStateException("service id not set");
    }
    if(_serviceClass == null){
      _serviceClass = getClass().getName();
    }
    return this;
  }
  
  public Status checkStatus() throws RemoteException {
    Connection conn = null;
    Status stat = new Status(_serviceClass, _id);
    try{
      Class.forName(_driver);
      Properties props = new Properties();
      props.setProperty("username", _username);
      if(_password != null){
        props.setProperty("password", _password);
      }
      conn = DriverManager.getConnection(_url, _username, _password);
    }catch(Exception e){
      stat.setError(e);
    }finally{
      if(conn != null){
        try{
          conn.close();
        }catch(Exception e2){}
      }
    }
    return stat;
  }
}
