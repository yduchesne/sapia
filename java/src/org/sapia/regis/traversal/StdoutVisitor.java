package org.sapia.regis.traversal;

import java.util.Iterator;

import org.sapia.regis.Node;

/**
 * A visitor that pretty-prints node structure to stdout.
 * 
 * @author yduchesne
 *
 */
public class StdoutVisitor implements Visitor{
  
  @Override
  public void visit(Node node) {
    
    int indents = node.getAbsolutePath().tokenCount();
    
    StringBuffer indentation = new StringBuffer();
    for(int i = 0; i < indents; i++){
      indentation.append("    ");
    }
    System.out.print(indentation + " |--+ ");
    System.out.println(node.getName());
    
    Iterator keys = node.getPropertyKeys().iterator();
    while(keys.hasNext()){
      String key = (String)keys.next();
      System.out.print(indentation);
      System.out.print("    - ");
      System.out.println(key + " = " + node.getProperty(key).asString()); 
    }
  }

}
