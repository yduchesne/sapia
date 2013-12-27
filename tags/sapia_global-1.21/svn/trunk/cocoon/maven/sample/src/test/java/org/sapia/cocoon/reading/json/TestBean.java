package org.sapia.cocoon.reading.json;

import java.util.Date;

public class TestBean {
  
  private NestedTestBean[] array;
  private String name;
  private int id;
  private Date creationDate;
  
  public NestedTestBean[] getArray() {
    return array;
  }

  public void setArray(NestedTestBean[] array) {
    this.array = array;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public static TestBean create(){
    TestBean bean = new TestBean();
    bean.id = 1;
    bean.name = "test-bean";
    bean.creationDate = new Date();
    bean.array = new NestedTestBean[5];
    for(int i = 0; i < bean.array.length; i++){
      bean.array[i] = new NestedTestBean();
      bean.array[i].setName("nested-bean-" + i);
    }
    return bean;
  }


}
