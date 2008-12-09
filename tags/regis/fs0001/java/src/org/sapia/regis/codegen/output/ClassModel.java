package org.sapia.regis.codegen.output;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.sapia.regis.Node;
import org.sapia.regis.codegen.NodeCapableImpl;

public class ClassModel {

  private CodeGenContext ctx;

  private Collection<ClassModelMember> members = new ArrayList<ClassModelMember>();

  private Set<PropertyModel> properties = new TreeSet<PropertyModel>();

  public ClassModel(CodeGenContext ctx) {
    this.ctx = ctx;
  }

  public CodeGenContext getContext() {
    return ctx;
  }

  public void addMember(ClassModelMember member) {
    members.add(member);
  }

  public Collection<PropertyModel> getProperties() {
    return properties;
  }

  public void addProperties(Collection<PropertyModel> properties) {
    this.properties.addAll(properties);
  }

  public void output(File destDir) throws IOException {
    PrintWriter writer = ctx.createWriter(destDir);
    try {
      doOutput(writer);
    } finally {
      writer.flush();
      writer.close();
    }
  }

  private void doOutput(PrintWriter writer) throws IOException {
    String packageName = ctx.getPackagePath().toString('.');
    if (packageName != null && packageName.length() > 0) {
      writer.println("package " + packageName + ";");
      writer.println();
    }

    if (ctx.isInterface()) {
      System.out.println("Generating : " + ctx.getClassName());
      writer.println("public interface " + ctx.getClassName() + " {");
      writer.println();
      for (PropertyModel p : getProperties()) {
        p.toSource(writer, true);
      }
    } else {
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
        p.toSource(writer, false);
      }
      for (ClassModelMember member : members) {
        if (member.getModel().ctx.getHints().getParentInterface() == null) {
          writer.println("  public "
              + member.getModel().ctx.getFullyQualifiedClassName() + " get"
              + CodeGenUtils.toCamelCase(member.getName()) + "(){");
          writer.println("    return getConcreteInstanceFor("
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
        for (ClassModelMember member : members) {
          writer.println("    members.add(getConcreteInstanceFor("
              + intf.ctx.getFullyQualifiedClassName() + "Impl.class, \""
              + member.getName() + "\"));");
        }
        writer.println("    return members;");
        writer.println("  }");

      }

    }
    writer.println("}");
  }

}
