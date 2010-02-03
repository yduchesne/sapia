package org.sapia.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

import org.sapia.resource.include.IncludeContext;
import org.sapia.resource.include.IncludeState;

public class TestIncludeContext extends IncludeContext{
  
  TestIncludedObject obj = new TestIncludedObject();
  
  protected Object doInclude(InputStream is, Object o) throws IOException, Exception {
    String txt = Utils.textStreamToString(is);
    obj.uri = getUri();
    StringTokenizer tk = new StringTokenizer(txt);
    while(tk.hasMoreTokens()){
      String token = tk.nextToken();
      if(token.startsWith("#") && token.endsWith("#")){
        String uri = token.substring(1, token.length() - 1);
        obj.children.add(IncludeState.createContext(uri, getConfig()).include());
      }
    }
    return obj;
    
  }

}
