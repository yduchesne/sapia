package org.sapia.soto.regis;

import java.util.Collection;

import org.sapia.regis.Node;

public class TestRegisClient {
  
  TestConfig _cfg;
  Node _node;
  Node _compNode;
  Collection _nodes;
  Collection _dbServices;
  
  public void setConfig(TestConfig cfg){
    _cfg = cfg;
  }
  
  public void setNode(Node node){
    _node = node;
  }
  
  public void setCompositeNode(Node node){
    _compNode = node;
  } 
  
  public void setNodes(Collection nodes){
    _nodes = nodes;
  }  
  
  public void setDbServices(Collection services){
    _dbServices = services;
  }  

}
