package org.sapia.util.cursor;

import java.util.Iterator;

/**
 * This insterface specifies the behavior of batches of objects created by
 * <code>Cursor</code> implementations.
 * 
 * @see org.sapia.util.cursor.Cursor
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
public interface Batch {

  /**
   * @return the actual number of items that this instance holds.
   */
  public int getCount();

  /**
   * @param index
   *          an index that is relative to this batch (and not relative to the
   *          cursor to which this instance belongs).
   * 
   * @return the <code>Object</code> at the specified index.
   */
  public Object getItemAt(int index);

  /**
   * Removes the object at the given index and returns it.
   *
   * @param index
   *          an index that is relative to this batch (and not relative to the
   *          cursor to which this instance belongs).
   * 
   * @return the <code>Object</code> at the specified index.
   */  
  public Object removeItemAt(int index);

  /**
   * @return the current position (relative to this instance's first item).
   */
  public int getPos();

  /**
   * @param pos
   *          this instance's current position.
   */
  public void setPos(int pos);

  /**
   * Sets the current position to the first item.
   * 
   * @return this instance.
   */
  public Batch toFirst();

  /**
   * Sets the current position to the last item.
   * 
   * @return this instance.
   */
  public Batch toLast();

  /**
   * @return the current absolute position (relative to this instance's cursor)
   *         of the "current" item in this instance.
   */
  public int getAbsolutePos();
  
  /**
   * @return the position of this instance within its cursor.
   */
  public int getIndex();

  /**
   * @return an <code>Iterator</code> over this instance.
   */
  public Iterator getIterator();

  /**
   * Returns an iterator that will traverse this instance's elements in a
   * "descending" fashion.
   * 
   * @return an <code>Iterator</code> over this instance.
   */
  public Iterator getReverseIterator();

  /**
   * @return <code>true</code> if this instance has a "next" item pending.
   */
  public boolean hasNext();
  
  /**
   * @return <code>true</code> if this instance has a "next" batch pending
   * in the cursor.
   */
  public boolean hasNextBatch();
  
  /**
   * @return <code>true</code> if this instance has a "previous" batch pending
   * in the cursor.
   */
  public boolean hasPreviousBatch();  

  /**
   * @return the next item.
   */
  public Object next();

  /**
   * @return <code>true</code> if this instance has a "previous" item pending.
   */
  public boolean hasPrevious();

  /**
   * @return the previous item.
   */
  public Object previous();

}
