package org.sapia.regis.prevayler;

import java.util.Date;

import org.prevayler.Query;
import org.sapia.regis.Node;

public abstract class AbstractQuery implements Query{
  
  public Object query(Object arg0, Date arg1) throws Exception {
    return doQuery((Node)arg0, arg1);
  }
  protected abstract Object doQuery(Node root, Date date) throws Exception;

}
