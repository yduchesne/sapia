package org.sapia.regis.codegen.output;

class ClassModelMember {

  private String name;

  private ClassModel model;

  ClassModelMember(String name, ClassModel model) {
    this.name = name;
    this.model = model;
  }

  String getName() {
    return name;
  }

  void setName(String name) {
    this.name = name;
  }

  ClassModel getModel() {
    return model;
  }

  void setModel(ClassModel model) {
    this.model = model;
  }
}
