package org.sapia.soto.regis;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;

import org.sapia.regis.Node;
import org.sapia.regis.Path;
import org.sapia.regis.Registry;
import org.sapia.soto.Env;
import org.sapia.soto.NotFoundException;
import org.sapia.resource.Resource;
import org.sapia.resource.ResourceHandler;
import org.sapia.soto.config.SotoIncludeContext;
import org.sapia.soto.util.NestedIOException;
import org.sapia.resource.ResourceNotFoundException;
import org.sapia.soto.util.Utils;

public class RegisResourceHandler implements ResourceHandler{
  
  private Env _env;
  private Node _node;
  
  RegisResourceHandler(Env env){
    _env = env;
  }
  
  public boolean accepts(String uri) {
    return uri != null && uri.startsWith(RegisResource.REGIS_SCHEME);
  }
  
  public boolean accepts(URI uri) {
    return uri != null && uri.getScheme().equals(RegisResource.REGIS_SCHEME);
  }  
  
  
  public InputStream getResource(String uri) throws IOException, ResourceNotFoundException {
    return getResourceObject(uri).getInputStream();
  }
  
  public void setNode(Node node){
    _node = node;
  }
  
  public Resource getResourceObject(String uri) throws IOException {
    String pathStr = Utils.chopScheme(uri);
    Path p = null;
    int i = pathStr.indexOf('?'); 
    String realUri = null;
    if(i > 0){
      p = Path.parse(pathStr.substring(0, i));
      realUri = pathStr.substring(i+1, pathStr.length());
    }
    else{
      p = Path.parse(pathStr);
    }
    if(realUri != null && Utils.isRelativePath(realUri)){
      throw new IOException("Regis URI does note accept relative sub-URIs: " + realUri);
    }
    
    if(_node != null){
      return doGetResource(uri, realUri, _node);
      /*
      if(realUri == null){
        return new RegisResource(_env, _node, realUri, null, SotoIncludeContext.currentTemplateContext());
        
      }
      else{
        return new RegisResource(_env, _node, realUri, _env.resolveResource(realUri), SotoIncludeContext.currentTemplateContext());
      }*/
    }
    else{
      Iterator tokens = p.tokens();
      if(!tokens.hasNext()){
        throw new IOException("Registry service ID expected in URI: " + uri);
      }
      String serviceId = (String)tokens.next();
      try{
        Registry reg = (Registry)_env.lookup(serviceId);
        p = RegistryUtils.copy(tokens);
        Node node = reg.getRoot().getChild(p);
        if(node == null){
          throw new ResourceNotFoundException("No registry node found at: " + uri);
        }
        return doGetResource(uri, realUri, node);
        
      }catch(NotFoundException e){
        throw new ResourceNotFoundException("Registry service could not be found for ID: "
            + serviceId);
      }
    }
  }
  
  private Resource doGetResource(String uri, String realUri, Node node) throws IOException{
    SotoIncludeContext include = SotoIncludeContext.currentContext();
    if(include != null && include.getParent() != null){
      include = (SotoIncludeContext)include.getParent();
      try{
        if(realUri == null){
          return new RegisResource(node, realUri, null, include.getTemplateContext());              
        }
        else{
          return new RegisResource(node, realUri, include.getRelative(realUri), include.getTemplateContext());              
        }

      }catch(IOException e){
        throw e;
      }catch(Exception e){
        throw new NestedIOException("Could not include: " + uri, e);
      }
    }
    else{
      if(realUri == null){
        return new RegisResource(node, realUri, null, include.getTemplateContext());
      }
      else{
        return new RegisResource(node, uri, _env.getResourceHandlers().resolveResource(realUri), include.getTemplateContext());
      }
    }
    
  }
}
