/*
 * SessionStep.java
 *
 * Created on June 8, 2005, 8:44 AM
 */

package org.sapia.soto.hibernate.stm;
import org.hibernate.Transaction;
import org.sapia.soto.hibernate.SessionState;
import org.sapia.soto.state.ExecContainer;
import org.sapia.soto.state.Executable;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.State;
import org.sapia.soto.state.Step;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectHandlerIF;

/**
 *
 * @author yduchesne
 */
public class TxStep extends ExecContainer 
  implements State, Step, ObjectHandlerIF{
  
  private String _id;
  
  /** Creates a new instance of SessionStep */
  public TxStep() {
  }
  
  public void setId(String id){
    _id = id;
  }
  
  public String getId(){
    return _id;
  }
  
  
  public String getName(){
    return "TxStep";
  }
  
  public void execute(Result rs){
    Transaction tx = SessionState.currentSession().beginTransaction();
    try{
      super.execute(rs);
      if(rs.isError()){
        tx.rollback();
      }
      else{
        tx.commit();
      }
    }catch(RuntimeException e){
      tx.rollback();
      throw e;
    }
  }
  
  public void handleObject(String name, Object o) throws ConfigurationException{
    if(o instanceof Executable){
      super.addExecutable((Executable)o);
    }
    else{
      throw new ConfigurationException("Expecting instances of " + 
        Executable.class.getName() + "; got: " + o.getClass().getName() + " for element: " + name);
    }
  }  
}
