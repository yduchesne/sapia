package org.sapia.soto.state;

import org.apache.commons.beanutils.PropertyUtils;
import org.sapia.soto.state.helpers.ScopeParser;
import org.sapia.util.CompositeRuntimeException;

/**
 * This class holds key and scope data. It is a convenience class meant to facilitate
 * configuration of looked up values.
 * 
 * @see #parse(String)
 * @see #lookup(Result)
 */
public class StmKey{
  
  
  /**
   * When only a key is specifed, the lookup will search in every scope of the context
   */
  public static int MODE_LOOKUP_EVERYTIME=0;
  
  /**
   * When only a key is specified, the lookup will consider the key as the actual value
   */
  public static int MODE_LOOKUP_ONLY_WHEN_SCOPE_SPECIFIED=1;
  
  /**
   * The names of the scopes that were parsed. Will be <code>null</code>
   * if no scopes were given.
   */
  public String[] scopes;
  
  /**
   * The key that was parsed.
   */
  public String key;
  
  /**
   * The attribute (in beanutils format) of the value that is retrieved for the given key.
   */
  public String attribute;
  
  int mode=0;
  
  protected StmKey(){}
  
  /**
   * Looks up the object corresponding to this key from the given result's
   * context.
   *
   * @return the <code>Object</code> corresponding to the given key,
   * or <code>null</code> if no object could ne found.
   * @param res a <code>Result</code>.
   */
  public Object lookup(Result res){
    return lookup(res.getContext());
  }
  
  public Object lookup(Context ctx){
    Object obj = null;
    if(scopes == null){
      if(mode==MODE_LOOKUP_EVERYTIME){
        obj = ctx.get(key);
      }else{
        obj=key;
      }
    }
    else if(scopes.length == 1){
      obj = ctx.get(key, scopes[0]);
    }
    else{
      obj = ctx.get(key, scopes);
    }
    if(obj != null && attribute != null && !(scopes==null && mode==MODE_LOOKUP_ONLY_WHEN_SCOPE_SPECIFIED)){
      try{
        obj = PropertyUtils.getProperty(obj, attribute);
      }catch(Exception e){
        throw new CompositeRuntimeException("Could not get attribute: " + attribute + 
            " of object: " + obj, e);
      }
    }
    return obj;    
    
  }
  
  /**
   * This method returns an instance of this class corresponding to the passed in string.
   * The string must have the format <code>key:scope1,scope2,...,scopeN</code>.
   * <p>
   * The 'key' part indicates which value will be looked up by the returned <code>StmKey</code>
   * instance. The comma-delimited list of scopes is optional and must be demarcated by a ':';
   * it consists in a list of scope names, indicating which scopes will be searched for the 
   * desired value (scopes are search in the order in which their names are specified).
   * <p>
   * Additionnaly, the 'key' part can consist of key and attribute information. For example:
   * 
   * <pre>User.username:session</pre>
   * 
   * In the above case, the actual key will be "User", and and attribute part will be 'username'.
   * The attribute (if specified), is expected to respect the BeanUtils (from Jakarta) format. It
   * can consist of a complex (nested) attribute:
   * 
   * <pre>User.shoppingCart.items.size:session</pre>
   * 
   * Thus, in the above, the attribute is: <code>shoppingCart.items.size</code>.
   * 
   * In any case, internally, when using an attribute, the value corresponding to the specified
   * key is first looked up. Then, the attribute is "invoked" on it.
   * 
   * @param str the key information, in string format.
   * @return the <code>StmKey</code> corresponding to the passed in
   * string.
   */
  public static StmKey parse(String str){
      return parse(str,MODE_LOOKUP_EVERYTIME);
  }
  
  public static StmKey parse(String str,int mode){
    
   
    
    int idx = str.indexOf(':');
    String[] scopes = null;
    String key = null;
    if(idx > -1){
      key = str.substring(0, idx);
      if(idx < str.length()){
        scopes = ScopeParser.parse(str.substring(idx+1));
        if(scopes == null || scopes.length == 0){
          scopes = null;
        }
      }
    }
    else{
      key = str;
    }
    
    // checking if key as attribute information...
    idx = key.indexOf('.');
    String attribute = null;
    if(idx > -1){
      String tmp = key.substring(0, idx);
      attribute = key.substring(idx+1);
      key = tmp;
    }
    StmKey stmKey = new StmKey();
    stmKey.key = key;
    stmKey.scopes = scopes;
    stmKey.attribute = attribute;
    stmKey.mode=mode;
    return stmKey;
  }
  
  public String toString(){
    StringBuffer buf = new StringBuffer(key);
    for(int i = 0; scopes != null && i < scopes.length; i++){
      if(i == 0) buf.append(':');
      buf.append(scopes[i]);
      if(i < scopes.length - 1){
        buf.append(',');
      }
    }
    return buf.toString();
  }
}
