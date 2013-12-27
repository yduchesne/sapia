package org.sapia.util.cursor;

/**
 * Implementations of this interface are in charge of filling buffers of
 * objects. This interface is intended to hide how the actual objects are
 * obtained.
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
public interface CursorFeed {

  /**
   * This method fills the given array with objects. It returns the number of
   * objects that were fed into the array.
   * <p>
   * <b>NOTE: </b> this method should be implemented as a blocking one.
   * 
   * @param buffer
   *          an empty array, ready to receive <code>Object</code>s.
   * @return the number of objects that were fed into the array.
   * @throws Exception
   *           if a problem occurs.
   */
  public int read(Object[] buffer) throws Exception;

  /**
   * Returns the number of pending objects; this method does not block until
   * objects are available; it should instead return 0 in such a case.
   * 
   * @return the number of pending objects held within this instance.
   * @throws Exception
   *           if a problem occurs.
   */
  public int available() throws Exception;
  
  /**
   * Closes this instance. Thereafter, any call on the <code>read()</code> or
   * <code>available()</code> of this instance will return 0. 
   */
  public void close();

}
