package org.sapia.soto.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.sapia.util.text.MapContext;
import org.sapia.util.text.TemplateContextIF;
import org.sapia.util.text.TemplateElementIF;
import org.sapia.util.text.TemplateException;
import org.sapia.util.text.TemplateFactory;

/**
 * This class holds various utility methods.
 * 
 * @author Yanick Duchesne
 *         <dl>
 *         <dt><b>Copyright: </b>
 *         <dd>Copyright &#169; 2002-2003 <a
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
   * This method uses Java's introspection features to copy the fields of a
   * source object to a target object.
   * 
   * @param src
   *          the source object.
   * @param trg
   *          the target object.
   * 
   * @throws Exception
   *           if a problem occurs performing this operation, especially if
   *           security credentials are violated.
   */
  public static void copyFields(Object src, Object trg) throws Exception {
    copyFields(src.getClass(), src, trg);
  }

  /**
   * Returns whether the given path is relative or not.
   * 
   * @param path
   *          a path.
   * @return returns <code>true</code> if the given path is relative.
   */
  public static boolean isRelativePath(String path) {
    path = path.trim();

    if(path.length() > 0) {
      if(Character.isLetter(path.charAt(0)) && path.length() > 2 && path.charAt(1) == ':'){
        return false;
      }
      
      return !hasScheme(path)
          && ((path.charAt(0) != '/') && (path.charAt(0) != '\\') && (path
              .charAt(0) != '.'));
    }

    return false;
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

    return path.indexOf(":/") >= 0;
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
      String toReturn = path.substring(idx + 1);
      if(toReturn.startsWith("//")){
        toReturn = toReturn.substring(1);
      }
            
      return toReturn;  

      /*if(toReturn.charAt(0) != '/'){
        return new StringBuffer('/').append(toReturn).toString();
      if(File.separator.equals("/") && (toReturn.charAt(0) != '/')) {
        return File.separator + toReturn;
      } else if(File.separator.equals("\\") && (toReturn.charAt(0) != '/')) {        
        return '/' + toReturn;
      } else {
        return toReturn;
      }*/
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
    int idx = path.indexOf(":/");

    if(idx >= 0) {
      return path.substring(0, idx);
    } else {
      return null;
    }
  }

  /**
   * Performs variable interpolation on a passed in stream, and returns the
   * resulting stream.
   * 
   * @param ctx
   *          a <code>TemplateContextIF</code>.
   * @param is
   *          an <code>InputStream</code>.
   * @param resourceName
   *          the "name" of the passed in stream - used if an error is
   *          generated.
   * 
   * @return the interpolated <code>InputStream</code>.
   * @throws IOException
   *           if a problem occus.
   */
  public static InputStream replaceVars(TemplateContextIF ctx, InputStream is,
      String resourceName) throws IOException {
    String txt = textStreamToString(is);

    try {
      TemplateFactory fac = new TemplateFactory();
      TemplateElementIF elem = fac.parse(txt);

      return new ByteArrayInputStream(elem.render(ctx).getBytes());
    } catch(TemplateException e) {
      throw new NestedIOException("Could not replace variables in content: "
          + resourceName, e);
    } finally {
      is.close();
    }
  }
  
  /**
   * Replaces the property values in the given <code>Properties</code>
   * instance with the variable values provided by the given context.
   * <p>
   * Property values may hold variables, as illustrated by the following example:
   * <pre>
   * some-property=${some-variable} and some-value
   * </pre>
   * 
   * @param ctx a <code>TemplateContextIF</code> holding variables.
   * @param props a <code>Properties</code> whose values should be interpolated.
   * @return the resulting <code>Properties</code> (with interpolated values).
   * @throws TemplateException an error occurs while performing the replacement.
   */
  public static Properties replaceVars(TemplateContextIF ctx, 
      Properties props) throws TemplateException{
    return doReplaceVars(ctx, props, true);
  }
  
  public static Properties doReplaceVars(TemplateContextIF ctx, 
      Properties props, boolean secondPass) throws TemplateException{
    Enumeration names = props.propertyNames();
    Properties result = new Properties();
    TemplateFactory fac = new TemplateFactory();
    PropertiesContext child = new PropertiesContext(ctx, false);
    child.addProperties(result);
    while(names.hasMoreElements()){
      String name = (String)names.nextElement();
      String value = props.getProperty(name);
      if(value != null){
        TemplateElementIF elem = fac.parse(value);
        value = elem.render(child);
        result.setProperty(name, value);
      }
    }
    if(secondPass){
      return doReplaceVars(ctx, props, false);
    }
    return result;
  } 

  /**
   * @see #replaceVars(TemplateContextIF, Properties)
   */
  public static Map replaceVars(TemplateContextIF ctx, Map props)
    throws TemplateException{
    Iterator keys = props.keySet().iterator();
    Map result = new HashMap();
    TemplateFactory fac = new TemplateFactory();
    MapContext child = new MapContext(result, ctx, false);
    while(keys.hasNext()){
      Object next = keys.next();
      if(next instanceof String){
        String name = (String)next;
        Object value = props.get(name);
        if(value != null && value instanceof String){
          String valueString = (String)value;
          TemplateElementIF elem = fac.parse(valueString);
          value = elem.render(child);
          result.put(name, value);
        }
      }
    }
    return result;
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
    String compared = base.replace('\\', '/');

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

  /**
   * Returns the classes that the given object is an instance of.
   * 
   * @param src
   *          the "source" object.
   * @return the array of <code>Class</code> objects of which the given object
   *         is an instance.
   */
  public static Class[] getClasses(Object src) {
    Set classes = new HashSet();
    getClasses(classes, src.getClass());

    return (Class[]) classes.toArray(new Class[classes.size()]);
  }
  
  /**
   * Transfers the data of a given input to a given output. Both streams
   * are closed before this method returns.
   * 
   * @param from the <code>InputStream</code> holding the data to copy.
   * @param to the target <code>OutputStream</code>.
   */
  public static void copy(InputStream from, OutputStream to) throws IOException{
    try{
      byte[] buf = new byte[1048];
      int read = 0;
      while((read = from.read(buf)) > 0){
        to.write(buf, 0, read);
      }
    }finally{
      try{
        from.close(); 
      }catch(Exception e){
        //noop
      }
      try{
        to.close();
      }catch(Exception e){
        //noop
      }      
    }
  }
  
  public static String createInvalidAssignErrorMsg(
    String attributeName,  
    Object toAssign, 
    Class expectedInstanceOf){
    
    String msg = "Invalid instance for: '" + attributeName + "'; " +
      " got instance of: " + toAssign.getClass().getName() + 
      " and expected instance of :" + expectedInstanceOf.getName();
    
    return msg;
    
  }

  private static void getClasses(Set classes, Class current) {
    Class[] classArr = current.getInterfaces();

    for(int i = 0; i < classArr.length; i++) {
      classes.add(classArr[i]);
      getClasses(classes, classArr[i]);
    }

    current = current.getSuperclass();

    if(current != null) {
      getClasses(classes, current);
    }
  }

  private static void copyFields(Class current, Object src, Object trg)
      throws Exception {
    Field[] fields = current.getDeclaredFields();
    Object value;

    for(int i = 0; i < fields.length; i++) {
      if(!fields[i].isAccessible()) {
        fields[i].setAccessible(true);
      }

      value = fields[i].get(src);
      fields[i].set(trg, value);
    }

    current = current.getSuperclass();

    if(current != null) {
      copyFields(current, src, trg);
    }
  }
  
  /**
   * Utility method that sleeps for a given time without interruption.
   * 
   * @param millis The number of milliseconds to sleep for.
   */
  public static void sleepUninterruptedly(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException ie) {
      // noop
    }
  }
}
