package org.sapia.soto.regis;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.sapia.regis.Node;
import org.sapia.resource.Resource;
import org.sapia.resource.ResourceNotFoundException;
import org.sapia.soto.util.TemplateContextMap;
import org.sapia.soto.util.Utils;
import org.sapia.util.text.TemplateContextIF;

public class RegisResource implements Resource{
  
  public static final String REGIS_SCHEME = "regis";
  
  private Node _node;
  private TemplateContextIF _ctx;
  private String _uri;
  private Resource _res;
  
  RegisResource(Node node, String uri, Resource res, TemplateContextIF ctx){
    _ctx = ctx;
    _node = node;
    _res = res;
    _uri = uri;
  }
  
  public InputStream getInputStream() throws IOException {
    if(_node == null){
      throw new ResourceNotFoundException("No registry node at: " + _uri);
    }
   
    if(_res != null){
      return Utils.replaceVars(new NodeTemplateContext(_node, _ctx), _res.getInputStream(), _uri);  
    }
    else{
      Map map = _node.getProperties(new TemplateContextMap(_ctx));
      Properties props = new Properties();
      Iterator keys = map.keySet().iterator();
      while(keys.hasNext()){
        String key = (String)keys.next();
        props.setProperty(key, (String)map.get(key));
      }
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      props.store(bos, "");
      return new ByteArrayInputStream(bos.toByteArray());
    }
  }
  
  public String getURI() {
    return _uri;
  }
  
  public long lastModified() {
    return _node.lastModifChecksum();
  }
  
  public Resource getRelative(String uri) throws IOException {
    throw new UnsupportedOperationException();
  }

}
