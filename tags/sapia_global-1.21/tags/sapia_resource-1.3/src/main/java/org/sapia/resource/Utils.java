package org.sapia.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class holds various utility methods.
 * 
 * @author Yanick Duchesne
 *         <dl>
 *         <dt><b>Copyright: </b>
 *         <dd>Copyright &#169; 2002-2007 <a
 *         href="http://www.sapia-oss.org">Sapia Open Source Software </a>. All
 *         Rights Reserved.</dd>
 *         </dt>
 *         <dt><b>License: </b>
 *         <dd>Read the license.txt file of the jar or visit the <a
 *         href="http://www.sapia-oss.org/license.html">license page </a> at the
 *         Sapia OSS web site</dd>
 *         </dt>
 *         </dl>
 */
public class Utils {
  
  private static final char WIN_FILE_SEP = '\\';
  private static final char LINUX_FILE_SEP = '/';  
  
  /**
   * Splits the given string into parts delimited by the given "split"
   * character.
   * 
   * @param toSplit
   *          the <code>String</code> to split.
   * @param splitChar
   *          the delimiting character.
   * @param trim
   *          if <code>true</code>, the tokens resulting from the split will
   *          be trimed.
   * @return an array of <code>String</code> s corresponding to the tokens
   *         that resulted from the split.
   */
  public static String[] split(String toSplit, char splitChar, boolean trim) {
    List tokens = new ArrayList();

    StringBuffer token = new StringBuffer();

    for(int i = 0; i < toSplit.length(); i++) {
      if(toSplit.charAt(i) == splitChar) {
        if(trim) {
          tokens.add(token.toString().trim());
        } else {
          tokens.add(token.toString());
        }

        token.delete(0, token.length());
      } else {
        token.append(toSplit.charAt(i));
      }
    }

    if(token.length() > 0) {
      if(trim) {
        tokens.add(token.toString().trim());
      } else {
        tokens.add(token.toString());
      }
    }

    return (String[]) tokens.toArray(new String[tokens.size()]);
  }

  /**
   * Tests if the given path has a protocol/scheme.
   * 
   * @param path
   *          the path on which to perform the test.
   * @return <code>true</code> if path has a scheme.
   */
  public static boolean hasScheme(String path) {
    if(path == null) {
      return false;
    }

    if(path.indexOf(":") >= 0){
      if(path.length() >= 2){
        if(path.charAt(1) == ':'){
          if(Character.isLetter(path.charAt(0))){
            return false;
          }
          else{
            return false;
          }
        }
        else{
          return true;
        }
      }
      else{
        return false;
      }
    }
    else{
      return false;
    }
  }

  /**
   * Chops the scheme/protocol from the given URL path and returns the path
   * without the scheme.
   * 
   * @param path
   *          a URL path.
   * @return the path without the scheme, or the given path, if it has no
   *         scheme.
   */
  public static String chopScheme(String path) {
    int idx = path.indexOf(":");

    if(idx >= 0) {
      if(path.length() >= 2){
        if(path.charAt(1) == ':'){
          if(Character.isLetter(path.charAt(0))){
            return path;
          }
        }        
      }      
      String toReturn = path.substring(idx + 1);
      if(toReturn.startsWith("//")){
        toReturn = toReturn.substring(1);
      }
      return toReturn;  
    }

    return path;
  }

  /**
   * Returns the scheme of the given URL path.
   * 
   * @param path
   *          a URL path.
   * @return the scheme of the given URL path, or <code>null</code> if URL is
   *         not valid (i.e.: does not have a scheme/protocol).
   */
  public static String getScheme(String path) {
    int idx = path.indexOf(":");

    if(idx >= 0) {
      if(path.length() >= 2){
        if(path.charAt(1) == ':'){
          if(Character.isLetter(path.charAt(0))){
            return null;
          }
        }        
      }      
      return path.substring(0, idx);
    } else {
      return null;
    }
  }

  /**
   * Returns the path corresponding to the given base path, and the given
   * relative path - results in "concatenation" of both, sort of.
   * 
   * <pre>
   * 
   * // will print: /opt/some/path/relative/path
   * System.out.println(&quot;/opt/some/path&quot;, &quot;relative/path&quot;, false);
   * 
   * // will print: /opt/some/relative/path
   * System.out.println(&quot;/opt/some/file.txt&quot;, &quot;relative/path&quot;, true);
   * </pre>
   * 
   * @param base
   *          a base path.
   * @param relative
   *          the path to evaluate relatively to the given path.
   * @param isBaseFile
   *          if <code>true</code>, indicates that the base path corresponds
   *          to a file.
   * @return the evaluated path.
   */
  public static String getRelativePath(String base, String relative,
      boolean isBaseFile) {
    String toReturn;
    String compared = base.replace(WIN_FILE_SEP, LINUX_FILE_SEP);
    relative = Utils.chopScheme(relative);
    if(isWindowsDrive(relative)){
      relative = Utils.chopScheme(relative);
    }
    if(isBaseFile) {
      int idx;

      if((idx = compared.lastIndexOf('/')) >= 0) {
        toReturn = base.substring(0, idx) + File.separator + relative;
      } else {
        toReturn = base + File.separator + relative;
      }
    } else {
      if(compared.endsWith("//")) {
        toReturn = base + relative;
      } else {
        toReturn = base + File.separator + relative;
      }
    }

    return toReturn;
  }  
  
  public static URI toURIObject(String uri) throws IOException{
    try{
      return new URI(uri);
    }catch(URISyntaxException e){
      throw new IOException("Could not parse uri: " + uri + " - " + e.getMessage());
    }
  }
  
  public static boolean isAbsolute(String uri){
  	if(isWindowsDrive(uri)){
      return true;
  	}
  	else if(hasScheme(uri)){
  	  uri = chopScheme(uri);
  	}
    if(startsWith(uri, LINUX_FILE_SEP) || 
  	   startsWith(uri, WIN_FILE_SEP) || 
	  isWindowsDrive(uri)){
  	  return true;
    }
    return false;	
  }
  
  private static boolean startsWith(String comparee, char comparant){
  	if(comparee.length() == 0) return false;
  	return comparee.charAt(0) == comparant;
  }
  
  /**
   * Returns the data of the given text stream as a string.
   * <p>
   * IMPORTANT: the input stream is not closed by this method.
   * 
   * @param is
   *          an <code>InputStream</code> of character data.
   * @return the data of the given test stream.
   * @throws IOException
   *           if a problem occurs.
   */
  public static String textStreamToString(InputStream is) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is), 1024);
    String line;
    StringWriter writer = new StringWriter();

    while((line = reader.readLine()) != null) {
      writer.write(line);
      writer.write(System.getProperty("line.separator"));
    }

    return writer.getBuffer().toString();
  }
  
  public static boolean isWindowsFileSep(){
    return File.separatorChar == WIN_FILE_SEP;
  }
  
  public static String replaceWinFileSep(String uri){
    if(isWindowsFileSep()){
      uri = uri.replace(WIN_FILE_SEP, LINUX_FILE_SEP);
    }
    return uri;
  }
  
  public static boolean isWindowsDrive(String uri){
    return uri.length() >= 2 && uri.charAt(1) == ':' && Character.isLetter(uri.charAt(0));
  }
  
}
