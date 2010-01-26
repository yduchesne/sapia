package org.sapia.util.cursor;

import org.sapia.util.cursor.impl.CursorImpl;

/**
 * This class is a factory of <code>Cursor</code> instances.
 * <p>
 * Usage:
 * <pre>
 * // creating a cursor... 
 * CursorFactory cursors = new CursorFactory();
 * cursors.setBatchSize(25);
 * Cursor cursor = cursors.newInstance(someFeed);
 * 
 * // playing with it...
 * 
 * batch current = null;
 * 
 * // moving forward
 * while(cursor.hasNextBatch()){
 *   current = cursor.nextBatch();
 *   while(current.hasNext()){
 *     doSomethingWith(current.next());
 *   }
 * }
 * 
 * // moving backwards...
 * 
 * if(current != null){
 *   while(current.hasPrevious()){
 *     doSomethingWith(current.previous());
 *   }
 *   while(cursor.hasPreviousBatch()){
 *     current = cursor.previousBatch();
 *     while(current.hasPrevious()){
 *       doSomethingWith(current.previous());
 *     }     
 *   }  
 * }
 * 
 * </pre>
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
public class CursorFactory {

  /**
   * The default batch size of the cursors that this instance creates.
   */
  public static final int DEFAULT_BATCH_SIZE = 10;

  private int             _batchSize         = DEFAULT_BATCH_SIZE;

  /**
   * Creates a new cursor and returns it.
   * 
   * @param feed
   *          a <code>CursorFeed</code> that will be used by the cursor to
   *          internally create <code>Batch</code> instances.
   */
  public Cursor newInstance(CursorFeed feed) {
    return new CursorImpl(feed, null, _batchSize);
  }

  /**
   * Creates a new cursor and returns it.
   * 
   * @param feed
   *          a <code>CursorFeed</code>.
   * @param listener
   *          a <code>CursorListener</code>.
   * @return a <code>Cursor</code>.
   */
  public Cursor newInstance(CursorFeed feed, CursorListener listener) {
    return new CursorImpl(feed, listener, _batchSize);
  }

  /**
   * @return this instance's configured batch size.
   */
  public int getBatchSize() {
    return _batchSize;
  }

  /**
   * Sets this instance's batch size.
   * 
   * @param size
   *          a batch size.
   */
  public void setBatchSize(int size) {
    _batchSize = size;
  }

}
