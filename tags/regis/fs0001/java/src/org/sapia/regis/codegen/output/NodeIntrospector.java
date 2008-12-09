package org.sapia.regis.codegen.output;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.sapia.regis.Node;
import org.sapia.regis.Path;
import org.sapia.regis.Property;

public class NodeIntrospector {

  private NodeIntrospector parent;

  private CodeGenConfig config;

  private Node node;

  private String packageName, className;

  public NodeIntrospector(Node node, CodeGenConfig config) {
    this(null, node, config);
  }

  public NodeIntrospector(NodeIntrospector parent, Node node,
      CodeGenConfig config) {
    this.parent = parent;
    this.node = node;
    this.config = config;
  }

  public Node getNode() {
    return node;
  }

  public NodeIntrospector getParent() {
    return parent;
  }

  public boolean hasParent() {
    return parent != null;
  }

  public String getPackageName() {
    if (this.packageName == null) {
      StringBuilder sb = new StringBuilder();
      if (config.getPackagePrefix() != null) {
        sb.append(config.getPackagePrefix());
      }
      if (node.getParent() != null) {
        Path p = node.getAbsolutePath();
        if (sb.length() > 0 && !node.getParent().isRoot()) {
          sb.append(".");
        }
        int i = 0;
        java.util.Iterator<Object> tokens = p.tokens();
        while (tokens.hasNext() && i < p.tokenCount() - 1) {
          String token = (String) tokens.next();
          token = CodeGenUtils.removeInvalidChars(token);
          if (token.length() > 0) {
            sb.append(token.toLowerCase());
            if (i < p.tokenCount() - 2) {
              sb.append('.');
            }
          }
          i++;
        }
      }
      this.packageName = sb.toString();
    }
    return this.packageName;
  }

  public Collection<PropertyModel> getProperties() {
    Set<PropertyModel> propWrappers = new HashSet<PropertyModel>();
    for (Object k : getNode().getPropertyKeys()) {
      propWrappers.add(new PropertyModel(getNode().getProperty((String) k)));
    }
    return propWrappers;
  }

  public Set<String> getCommonPropertyKeysWith(Set somePropertyKeys,
      NodeIntrospector wrapper) {
    Set<String> commonPropertyKeys = new HashSet<String>();
    Collection<String> otherPropKeys = wrapper.getNode().getPropertyKeys();
    for (String k : otherPropKeys) {
      if (somePropertyKeys.contains(k)) {
        commonPropertyKeys.add(k);
      }
    }
    return commonPropertyKeys;
  }

  public String getClassName() {
    if (this.className == null) {
      if (node.isRoot()) {
        throw new IllegalStateException("Cannot get class name for root node");
      } else {
        this.className = CodeGenUtils.toCamelCase(node.getName());
      }
    }
    if (Character.isDigit(className.charAt(0))) {
      if (node.isRoot()) {
        className = "Class_" + className;
      } else {
        className = getParent().getClassName() + "_" + className;
      }
    }
    return className;
  }

  public Collection<NodeIntrospector> getChildren() {
    Collection<NodeIntrospector> childrenWrappers = new ArrayList<NodeIntrospector>();
    for (Object o : node.getChildren()) {
      Node n = (Node) o;
      childrenWrappers.add(new NodeIntrospector(this, n, config));
    }
    return childrenWrappers;
  }

  public void generate() throws IOException {
    doGenerate(null);
  }

  protected ClassModel doGenerate(Hints hints) throws IOException {

    if (getNode().isRoot()) {
      CodeGenContext ctx = new CodeGenContext(this.node);
      ctx.setClassName(this.config.getRootClassName() == null ? "Root" : this.config.getRootClassName());
      ctx.setPackagePath(Path.parse(getPackageName(), "."));
      ClassModel rootModel = new ClassModel(ctx);

      for (NodeIntrospector nw : getChildren()) {
        rootModel.addMember(new ClassModelMember(nw.getNode().getName(), nw
            .doGenerate(new Hints())));
      }
      rootModel.output(this.config.getDestinationDir());
      return null;
    } else {
      String packageName = getPackageName();

      // getting properties common to all children in order to generate
      // common interface

      Set<String> commonPropertyKeys = new HashSet<String>();
      Collection<NodeIntrospector> children = getChildren();

      if (children.size() > 0) {
        int i = 0;
        for (NodeIntrospector nw : getChildren()) {
          if (i == 0) {
            commonPropertyKeys.addAll(nw.getNode().getPropertyKeys());
          } else {
            commonPropertyKeys.retainAll(getCommonPropertyKeysWith(
                commonPropertyKeys, nw));
          }
          i++;
        }

        String interfaceName = getClassName() + "Node";
        CodeGenContext currentContext = new CodeGenContext(this.node);

        Set<PropertyModel> properties = new HashSet<PropertyModel>();
        NodeIntrospector firstChild = children.iterator().next();
        for (String key : commonPropertyKeys) {
          Property prop = firstChild.getNode().getProperty(key);
          properties.add(new PropertyModel(prop));
        }

        Path packagePath = Path.parse(packageName, ".");
        ClassModel model = new ClassModel(currentContext);
        currentContext.setClassName(getClassName());
        currentContext.setPackagePath(packagePath);
        currentContext.setHints(hints);
        model.addProperties(getProperties());

        if (properties.size() > 0) {

          // creating interface

          CodeGenContext interfaceContext = new CodeGenContext(this.node);
          interfaceContext.setClassName(interfaceName);
          interfaceContext.setPackagePath(packagePath);
          interfaceContext.setInterface(true);
          ClassModel interfaceModel = new ClassModel(interfaceContext);
          interfaceModel.addProperties(properties);
          interfaceModel.output(this.config.getDestinationDir());

          // creating default implementation
          CodeGenContext interfaceImplContext = new CodeGenContext(this.node);
          interfaceImplContext.setClassName(interfaceName + "Impl");
          interfaceImplContext.setPackagePath(packagePath);
          ClassModel interfaceModelImpl = new ClassModel(interfaceImplContext);
          interfaceImplContext.getHints().setParentInterface(interfaceModel);
          interfaceModelImpl.addProperties(properties);
          interfaceModelImpl.output(this.config.getDestinationDir());

          Hints childHints = new Hints();
          childHints.setParentInterface(interfaceModel);

          for (NodeIntrospector nw : getChildren()) {
            ClassModel cm = nw.doGenerate(childHints);
            model.addMember(new ClassModelMember(nw.getNode().getName(), cm));
          }
          model.output(this.config.getDestinationDir());
        } else {
          for (NodeIntrospector nw : getChildren()) {
            ClassModel cm = nw.doGenerate(new Hints());
            model.addMember(new ClassModelMember(nw.getNode().getName(), cm));
          }
          model.output(this.config.getDestinationDir());
        }
        return model;

      } else {
        Path packagePath = Path.parse(packageName, ".");
        CodeGenContext currentContext = new CodeGenContext(this.node);
        ClassModel model = new ClassModel(currentContext);
        currentContext.setClassName(getClassName());
        currentContext.setPackagePath(packagePath);
        model.addProperties(getProperties());
        currentContext.setHints(hints);
        for (NodeIntrospector nw : getChildren()) {
          nw.doGenerate(new Hints());
        }
        model.output(this.config.getDestinationDir());
        return model;
      }
    }

  }

}
