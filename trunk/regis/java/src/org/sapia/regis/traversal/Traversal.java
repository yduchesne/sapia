package org.sapia.regis.traversal;

import java.util.Iterator;

import org.sapia.regis.Node;

public class Traversal {

  private Node root;
  
  public Traversal(Node root){
    this.root = root;
  }
  
  public void traverse(Visitor visitor){
    doTraverse(root, visitor);
  }
  
  private void doTraverse(Node current, Visitor visitor){
    visitor.visit(current);
    Iterator children = current.getChildren().iterator();
    while(children.hasNext()){
      Node child = (Node)children.next();
      doTraverse(child, visitor);
    }
  }
}
