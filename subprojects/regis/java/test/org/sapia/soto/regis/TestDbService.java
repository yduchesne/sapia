package org.sapia.soto.regis;

import org.sapia.regis.Node;

public class TestDbService {
  
  String username, password, url, role;
  Node config;
  boolean readOnly;

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
  
  public void setConfig(Node config){
    this.config = config;
  }

  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public boolean isReadOnly() {
    return readOnly;
  }

  public String getRole() {
    return role;
  }

}
