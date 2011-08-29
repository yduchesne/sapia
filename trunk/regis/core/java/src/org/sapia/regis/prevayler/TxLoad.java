package org.sapia.regis.prevayler;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.Properties;

import org.prevayler.Transaction;
import org.sapia.regis.Node;
import org.sapia.regis.Path;
import org.sapia.regis.RWNode;
import org.sapia.regis.loader.RegistryConfigLoader;

public class TxLoad implements Transaction{
  
  static final long serialVersionUID = 1L;
  private Path path;
  private String xmlConf;
  private Properties props;
  
  public TxLoad(Path path, String xmlConf, Properties props){
    if(path == null){
      this.path = Path.parse(Node.ROOT_NAME);
    }
    else{
      this.path = path;
    }
    this.xmlConf = xmlConf;
    this.props = props;
  }
  
  public void executeOn(Object arg0, Date arg1){
    Node root = (Node)arg0;
    RegistryConfigLoader loader = new RegistryConfigLoader((RWNode)root.getChild(path));
    try{
      if(props == null)
        loader.load(new ByteArrayInputStream(this.xmlConf.getBytes()));
      else 
        loader.load(new ByteArrayInputStream(this.xmlConf.getBytes()), props);      
    }catch(Exception e){
      throw new RuntimeException("Could not load configuration", e);
    }
  }

}
