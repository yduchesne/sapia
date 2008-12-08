package org.sapia.regis.hibernate;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.sapia.regis.DuplicateNodeException;
import org.sapia.regis.Node;
import org.sapia.regis.Path;
import org.sapia.regis.RWNode;
import org.sapia.regis.RegisSession;
import org.sapia.regis.Registry;
import org.sapia.regis.impl.NodeImpl;
import org.sapia.regis.loader.RegistryConfigLoader;

public class HibernateRegistry implements Registry{
  
  private SessionFactory _fac;
  private Long _rootId;
    
  public HibernateRegistry(SessionFactory fac){
    _fac = fac;
    _rootId = acquireRoot().getId();
  }
  
  public RegisSession open() {
    Session s = _fac.openSession();
    Sessions.join(s);
    return new HibernateRegisSession(s);
  }
  
  public Node getRoot(){
    Session s = Sessions.get();
    Node n = (Node)s.load(NodeImpl.class, _rootId);
    return n;
  }

  public void close(){
    _fac.close();
  }
  
  public void load(File config) throws Exception{
    load(new FileInputStream(config));
  }
  
  public void load(InputStream is) throws Exception{
    RegistryConfigLoader loader = new RegistryConfigLoader((RWNode)getRoot());
    loader.load(is);
  }

  public SessionFactory getSessionFactory(){
    return _fac;
  }
  
  public Node createNode(Path path) throws DuplicateNodeException{
    RWNode node;
    RWNode root;
    
    if(path.isRoot()){
      return acquireRoot();
    }
    else{
      root = acquireRoot();
    }
    node = root;
    RWNode child;
    Iterator tokens = path.tokens();
    while(tokens.hasNext()){
      String token = (String)tokens.next();
      child = (RWNode)node.getChild(token);
      if(child == null){
        child = (RWNode)node.createChild(token);
      }
      node = child;
    }
    return node;
  }
  
  private NodeImpl acquireRoot(){
    Session session = null;
    if(Sessions.isRegistered()){
      session = Sessions.get();
    }
    else{
      session = _fac.openSession();
    }
    try{
      NodeImpl  root = doAcquireRoot(session);
      return root;
    }finally{
      if(!Sessions.isRegistered()){
        session.close();
      }
    }
  }
  
  
  private NodeImpl doAcquireRoot(Session session){
    NodeImpl root = (NodeImpl)session.createCriteria(NodeImpl.class)
    .add(Restrictions.eq("name", Node.ROOT_NAME)).uniqueResult();
    
    if(root == null){
      Transaction tx = session.beginTransaction();
      try{
        root = new NodeImpl(null, "");
        session.save(root);
      }finally{
        tx.commit();
      }
    }    
    return root;
  }
    
}
