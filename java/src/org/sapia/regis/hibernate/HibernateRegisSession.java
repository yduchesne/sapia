package org.sapia.regis.hibernate;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.sapia.regis.Node;
import org.sapia.regis.RWSession;
import org.sapia.regis.impl.NodeImpl;

public class HibernateRegisSession implements RWSession{
  
  private Session session;
  private Transaction tx;
  
  HibernateRegisSession(Session deleg){
    session = deleg;
  }
  
  public Node attach(Node node) {
    return (Node)session.load(NodeImpl.class, (((NodeImpl)node).getId()));
  }
  
  public void begin() {
    if(tx != null){
      tx.commit();
    }
    tx = session.beginTransaction();
  }
  
  public void commit() {
    if(tx == null){
      throw new IllegalStateException("Session not in transaction");
    }
    tx.commit();
    tx = null;
  }
  
  public void rollback() {
    if(tx == null){
      throw new IllegalStateException("Session not in transaction");
    }
    try{
      tx.rollback();
    }finally{
      tx = null;
    }
  }
  
  public void close() {
    Sessions.close();
    if(session.isOpen()){
      try{
        session.flush();
      }catch(RuntimeException e){
        session.close();
        throw e;
      }
      session.close();
    }
  }

}
