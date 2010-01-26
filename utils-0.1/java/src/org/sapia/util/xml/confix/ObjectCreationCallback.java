package org.sapia.util.xml.confix;


/**
 * This interface is meant to be implemented by objects that are created by
 * the Confix runtime, but are meant only for the sake of creating another object. This interface
 * can also be implemented if a validation needs to be performed before actually returning the
 * created object.
 * <p>
 * This allows objects that do not obey the Confix restrictions (adder and setter methods,
 * no-args constructor, etc) to still be created with Confix. For example, the following
 * code shows how a URL instance could be created:
 *
 * <pre>
 *
 * public class URLFactory implements ObjectCreationCallback{
 *
 *   private _link;
 *
 *   public void setLink(String link){
 *     _link = link;
 *   }
 *    public object onCreate() throws ConfigurationException{
 *     if(_link == null){
 *       throw new ConfigurationException("URL 'link' attribute not specified");
 *     }
 *     try{
 *        return new URL(_link);
 *     }catch(MalformedURLException e){
 *       throw new ConfigurationException("Invalid value for 'link' attribute in URL", e);
 *     }
 *   }
 * }
 * </pre>
 *
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface ObjectCreationCallback {
  /**
   * Called by the Confix runtime when this instance has been created; allows
   * it to return another object at its place. The method can also be used
   * if validation needs to be performed before return this instance (or the
   * object that this instance creates).
   *
   * @return an <code>Object</code>
   * @throws ConfigurationException if a problem occurs.
   */
  public Object onCreate() throws ConfigurationException;
}
