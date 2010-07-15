package org.sapia.corus.db.persistence;

public class TestPersistentObject {
  
  private long id = System.currentTimeMillis();
  private String name;
  private boolean active = true;
  
  public boolean isActive(){
    return active;
  }
  
  public void setActive(boolean active) {
    this.active = active;
  }
  
  public long getId() {
    return id;
  }
  
  void setId(long id) {
    this.id = id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
}
