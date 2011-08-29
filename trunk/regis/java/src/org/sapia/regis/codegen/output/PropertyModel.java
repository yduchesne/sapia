package org.sapia.regis.codegen.output;

import java.io.PrintWriter;

import org.sapia.regis.Property;

class PropertyModel implements Comparable<PropertyModel> {

  private Property property;

  PropertyModel(Property prop) {
    this.property = prop;
  }

  void toSource(PrintWriter writer, boolean isInterface, boolean generateGetters) {
    if (isInterface) {
      if(generateGetters){
        writer.println("  public " + Property.class.getName() + " get"
            + CodeGenUtils.toCamelCase(property.getKey(), true) + "();");
      }
      else{
        writer.println("  public " + Property.class.getName() 
            + CodeGenUtils.toCamelCase(property.getKey(), false) + "();");        
      }
    } else {
      if(generateGetters){
        writer.println("  public " + Property.class.getName() + " get"
            + CodeGenUtils.toCamelCase(property.getKey(), true) + "() {");
      }
      else{
        writer.println("  public " + Property.class.getName()
            + CodeGenUtils.toCamelCase(property.getKey(), false) + "() {");        
      }
      writer.println("    return this.node.getProperty(\"" + property.getKey()
          + "\");");
      writer.println("  }");
    }
  }

  public int compareTo(PropertyModel o) {
    return property.getKey().compareTo(o.property.getKey());
  }

}
