package org.sapia.soto.properties;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;
import java.util.StringTokenizer;

import org.sapia.soto.Env;
import org.sapia.soto.NotFoundException;
import org.sapia.soto.config.SotoIncludeContext;
import org.sapia.soto.util.PropertiesContext;
import org.sapia.resource.Resource;
import org.sapia.resource.ResourceHandler;
import org.sapia.resource.ResourceNotFoundException;
import org.sapia.soto.util.NestedIOException;
import org.sapia.soto.util.Utils;
import org.sapia.util.text.TemplateContextIF;

public class PropertyResourceHandler implements ResourceHandler{

  public static final String SCHEME = "props:/";
  static final char DELIM = '?';
  static final String PATH_SEP = "/";
  static final int PATH_INDEX = 0;
  static final int URI_INDEX = 1;
  
  private Env _env;
  private TemplateContextIF _ctx;
  
  PropertyResourceHandler(Env env) {
    _env = env;
    _ctx = SotoIncludeContext.currentTemplateContext();
  }
  
  public boolean accepts(String uri) {
    return uri.startsWith(SCHEME);
  }
  
  public boolean accepts(URI uri) {
    return uri.getScheme().equals(SCHEME);
  }

  public InputStream getResource(String uri) throws IOException, ResourceNotFoundException {
    return getResourceObject(uri).getInputStream();
  }

  public Resource getResourceObject(String uri) throws IOException {
    String[] tokens = tokens(uri);
    if(tokens.length == 1){
      PropInfo info = parsePropInfo(tokens[PATH_INDEX], uri);
      try{
        SotoIncludeContext include = SotoIncludeContext.currentContext();
        if(include != null && include.getParent() != null){
          try{
            return new PropertyStreamResource(uri, include.getParent().getResource(), info.names, findPropertyService(info.serviceId));
          }catch(Exception e){
            throw new NestedIOException("Could not acquire resource: " + uri, e);
          }
        }
        else{
          return new PropertyStreamResource(uri, null, info.names, findPropertyService(info.serviceId));
        }
      }catch(NotFoundException e){
        throw new ResourceNotFoundException("'" + info.serviceId  + "' of URI " + uri + " does not match to an existing PropertyService instance");        
      }
    }
    else{
      Properties props = findProperties(tokens[PATH_INDEX], uri);
      PropertiesContext ctx = new PropertiesContext(_ctx, false);
      ctx.addProperties(props);
      SotoIncludeContext include = SotoIncludeContext.currentContext();
      if(include != null && (include = (SotoIncludeContext)include.getParent()) != null){
        try{
          return new PropertyResource(tokens[URI_INDEX], include.getRelative(tokens[URI_INDEX]), ctx);
        }catch(IOException e){
          throw e;
        }catch(Exception e){
          throw new NestedIOException("Could not include: " + uri, e);
        }
      }
      else{
        return new PropertyResource(uri, _env.getResourceHandlers().resolveResource(tokens[URI_INDEX]), ctx);  
      }
    }
  }
  
  private Properties findProperties(String propPath, String uri) throws IOException, ResourceNotFoundException{
    PropInfo info = parsePropInfo(propPath, uri);
    try{
      PropertyService service = findPropertyService(info.serviceId);
      Properties props = service.getProperties(info.names);
      return props;
    }catch(NotFoundException e){
      throw new ResourceNotFoundException("'" + info.serviceId  + "' of URI " + uri + " does not match to an existing PropertyService instance");
    }
  }
  
  private PropInfo parsePropInfo(String propPath, String uri) throws IOException{
    StringTokenizer tokenizer = new StringTokenizer(propPath, PATH_SEP);
    String name = null;
    if(tokenizer.hasMoreTokens()){
      String serviceId = tokenizer.nextToken();
      if(tokenizer.hasMoreTokens()){
        name = tokenizer.nextToken();
        PropInfo info =  new PropInfo();
        info.serviceId = serviceId;
        info.names = Utils.split(name, ',', true);
        return info;
      }
      else{
        throw new IOException("Invalid URI: " + uri + "; expected: props:/serviceId/propsName?someUri");
      }
    }
    else{
      throw new IOException("Invalid URI: " + uri + "; expected: props:/serviceId/propsName?someUri");
    }    
  }
  
  private PropertyService findPropertyService(String serviceId) throws NotFoundException{
    return (PropertyService)_env.lookup(serviceId);    
  }
  
  private String[] tokens(String uri) throws IOException{
    String path = Utils.chopScheme(uri);
    String[] tokens = Utils.split(path, DELIM, true);
    if(tokens.length != 1 && tokens.length != 2){
      throw new IOException("Invalid URI: " + uri + "; expected: props:/serviceId/propsName?someUri");
    }
    if(tokens[0].startsWith("/")){
      tokens[0] = new StringBuffer(tokens[0]).deleteCharAt(0).toString();
    }
    return tokens;
  }
  
  static class PropInfo{
    String serviceId;
    String[] names;
  }
}
