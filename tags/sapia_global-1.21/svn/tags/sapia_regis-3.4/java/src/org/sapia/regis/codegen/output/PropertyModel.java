package org.sapia.regis.codegen.output;

import java.io.PrintWriter;

import org.sapia.regis.Property;

class PropertyModel implements Comparable<PropertyModel> {

  Property property;

  PropertyModel(Property prop) {
    this.property = prop;
  }

  void toSource(PrintWriter writer, boolean isInterface) {
    if (isInterface) {
      writer.println("  public " + Property.class.getName() + " get"
          + CodeGenUtils.toCamelCase(property.getKey()) + "();");
    } else {
      writer.println("  public " + Property.class.getName() + " get"
          + CodeGenUtils.toCamelCase(property.getKey()) + "() {");
      writer.println("    return this.node.getProperty(\"" + property.getKey()
          + "\");");
      writer.println("  }");
    }
  }

  public int compareTo(PropertyModel o) {
    return property.getKey().compareTo(o.property.getKey());
  }

}
