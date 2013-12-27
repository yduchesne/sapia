package org.sapia.soto.state.helpers;

import org.apache.commons.lang.StringUtils;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.StmKey;

/**
 * A utility class that parses scope names from a comma-delimited list.
 * 
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class ScopeParser {

  public static final String COMMA = ",";
  
  /**
   * This class holds key and scope data.
   * @deprecated use <code>StmKey</code> instead
   * @see StmKey
   */
  public static class Key extends StmKey{
  }

  /**
   * Parses a comma-delimited list of scope names and returns it as an array.
   * 
   * @param scopeList
   *          a comma-delimited list of scope names.
   * @return the array of the parsed scope names (with each item in the array
   *         corresponding to a scope name).
   */
  public static String[] parse(String scopeList) {
    String[] scopes = StringUtils.split(scopeList, COMMA);
    for(int i = 0; i < scopes.length; i++) {
      scopes[i] = scopes[i].trim();
    }
    return scopes;
  }
  
  /**
   * This method expects a string in the following format:
   * <p>
   * <pre>key:scopes</pre>
   * <p>
   * where <code>key</code> is a character string, and <code>scopes</code> is
   * a comma-delimited list of scopes.
   * <p>
   * The method returns a <code>Key</code> instance.
   *
   * @param a string.
   *
   * @return the <code>Key</code> instance corresponding to the parsed data
   * of the given string.
   */
  public static StmKey parseKey(String str){
    return StmKey.parse(str);
  }
  
  /**
   * Looks up an object (under the given key) from the given result's
   * context.
   *
   * @return the <code>Object</code> corresponding to the given key,
   * or <code>null</code> if no object could ne found.
   * @param res a <code>Result</code>.
   * 
   * @deprecated use StmKey.lookup() instead.
   * @see StmKey#lookup(Result)
   */
  public static Object lookup(ScopeParser.Key key, Result res){
    return key.lookup(res);
  }  

}
