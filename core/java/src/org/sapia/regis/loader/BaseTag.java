package org.sapia.regis.loader;

public class BaseTag implements TagNameAware{
  
  private String name;

  public String getTagName() {
    return name;
  }

  public void setTagName(String name) {
    this.name = name;
  }

}
