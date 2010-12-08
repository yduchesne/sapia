package org.sapia.util.cursor;

/**
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
public interface CursorListener {

  /**
   * This method is called by the <code>Cursor</code> to which this instance
   * is associated on the call to the <code>next()</code> method (on the
   * cursor). This allows this method to actually initialize the state of the
   * given object before it is handed over to the client application, or
   * to provide a new instance at the place of the given object.
   * <p>
   * For example, the given object might be some lazily initializable place
   * holder that is kept in place of the original object, in order to spare
   * resources.
   * 
   * @param next
   *          the "next" <code>Object</code>.
   * @return the <code>Object</code> that should be returned to the client
   * by this instance's <code>Cursor</code>.
   */
  public Object onNext(Object next);

}
