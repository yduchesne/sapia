package org.sapia.regis.codegen.output;

public class ClassModelMember {

  private String name;

  private ClassModel model;

  public ClassModelMember(String name, ClassModel model) {
    this.name = name;
    this.model = model;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ClassModel getModel() {
    return model;
  }

  public void setModel(ClassModel model) {
    this.model = model;
  }
}
