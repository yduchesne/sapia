package org.sapia.regis.codegen.output;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.sapia.regis.Node;
import org.sapia.regis.codegen.NodeCapable;
import org.sapia.regis.codegen.NodeCapableImpl;

class ClassModel {

  private CodeGenContext ctx;

  private Collection<ClassModelMember> members = new ArrayList<ClassModelMember>();

  private Set<PropertyModel> properties = new TreeSet<PropertyModel>();

  ClassModel(CodeGenContext ctx) {
    this.ctx = ctx;
  }

  CodeGenContext getContext() {
    return ctx;
  }

  void addMember(ClassModelMember member) {
    members.add(member);
  }

  Collection<PropertyModel> getProperties() {
    return properties;
  }

  void addProperties(Collection<PropertyModel> properties) {
    this.properties.addAll(properties);
  }

  void output(File destDir) throws IOException {
    PrintWriter writer = ctx.createWriter(destDir);
    try {
      doOutput(writer);
    } finally {
      writer.flush();
      writer.close();
    }
  }

  void doOutput(PrintWriter writer) throws IOException {
    String packageName = ctx.getPackagePath().toString('.');
    if (packageName != null && packageName.length() > 0) {
      writer.println("package " + packageName + ";");
      writer.println();
    }

    if (ctx.isInterface()) {
      writer.println("/**");
      writer.println(" * Generated: " + new Date());
      writer.println(" * @author: " + System.getProperty("user.name"));
      writer.println(" * @version: " + ctx.getConfig().getVersion());
      writer.println(" */");            
      writer.println("public interface " + ctx.getClassName() + " extends " 
          + NodeCapable.class.getName() +  " {");
      writer.println();
      for (PropertyModel p : getProperties()) {
        p.toSource(writer, true, ctx.getConfig().isGenerateGetters());
      }
      
      for (ClassModelMember member : members) {
        if (member.getModel().ctx.getHints().getParentInterface() == null) {
          if(ctx.getConfig().isGenerateGetters()){
            writer.println("  public "
                + member.getModel().ctx.getFullyQualifiedClassName() + " get"
                + CodeGenUtils.toCamelCase(member.getName(), true) + "();");
          }
          else{
            writer.println("  public "
                + member.getModel().ctx.getFullyQualifiedClassName() 
                + CodeGenUtils.toCamelCase(member.getName(), false) + "();");
          }
        }
      }      
      
    } else {
      writer.println("/**");
      writer.println(" *");
      writer.println(" * Wraps the node at <code>" + ctx.getNode().getAbsolutePath().toString().replace("*/", "/") + "</code>");
      writer.println(" *");      
      writer.println(" * Generated: " + new Date());
      writer.println(" * @author: " + System.getProperty("user.name"));
      writer.println(" * @version: " + ctx.getConfig().getVersion());
      writer.println(" */");      
      if (ctx.getHints().getParentInterface() != null) {
        writer.println("public class "
            + ctx.getClassName()
            + " extends "
            + NodeCapableImpl.class.getName()
            + " implements "
            + ctx.getHints().getParentInterface().ctx
                .getFullyQualifiedClassName() + " {");
      } else {
        writer.println("public class " + ctx.getClassName() + " extends "
            + NodeCapableImpl.class.getName() + " {");
      }
      writer.println();
      writer.println("  public static final String NODE_PATH =\""
          + ctx.getNode().getAbsolutePath().toString() + "\";");
      writer.println();
      writer.println("  public " + ctx.getClassName() + "("
          + Node.class.getName() + " node){");
      writer.println("    super(node);");
      writer.println("  }");
      writer.println();
      for (PropertyModel p : getProperties()) {
        p.toSource(writer, false, ctx.getConfig().isGenerateGetters());
      }
      for (ClassModelMember member : members) {
        if (member.getModel().ctx.getHints().getParentInterface() == null) {
          if(ctx.getConfig().isGenerateGetters()){
            writer.println("  public "
                + member.getModel().ctx.getFullyQualifiedClassName() + " get"
                + CodeGenUtils.toCamelCase(member.getName(), true) + "(){");
          }
          else if(ctx.getConfig().isGenerateGetters()){
            writer.println("  public "
                + member.getModel().ctx.getFullyQualifiedClassName() 
                + CodeGenUtils.toCamelCase(member.getName(), false) + "(){");
          }          
          writer.println("    return getInstanceFor("
              + member.getModel().ctx.getFullyQualifiedClassName()
              + ".class, \"" + member.getName() + "\");");
          writer.println("  }");

        }
      }

      Map<ClassModel, List<ClassModelMember>> membersByInterface = new HashMap<ClassModel, List<ClassModelMember>>();
      for (ClassModelMember member : members) {
        if (member.getModel().ctx.getHints().getParentInterface() != null) {
          ClassModel intf = member.getModel().ctx.getHints()
              .getParentInterface();
          List<ClassModelMember> membersForIntf = membersByInterface.get(intf);
          if (membersForIntf == null) {
            membersForIntf = new ArrayList<ClassModelMember>();
            membersByInterface.put(intf, membersForIntf);
          }
          membersForIntf.add(member);
        }
      }

      for (ClassModel intf : membersByInterface.keySet()) {
        List<ClassModelMember> members = membersByInterface.get(intf);
        writer.println("  public java.util.Collection<"
            + intf.ctx.getFullyQualifiedClassName() + "> get"
            + intf.ctx.getClassName() + "s(){");
        writer.println("    java.util.List<"
            + intf.ctx.getFullyQualifiedClassName()
            + "> members = new java.util.ArrayList<"
            + intf.ctx.getFullyQualifiedClassName() + ">();");
        writer.println("    "+Iterator.class.getName()+" children = this.node.getChildren().iterator();");
        writer.println("    while(children.hasNext()){");
        writer.println("        "+Node.class.getName()+" child = ("+Node.class.getName()+")children.next();");
        writer.println("        members.add(getInstanceFor("+intf.ctx.getFullyQualifiedClassName() + "Impl.class, child.getName()))");
        writer.println("    }");
        writer.println("    return members;");
        writer.println("  }");
      }
    }
    writer.println("}");
  }

}
