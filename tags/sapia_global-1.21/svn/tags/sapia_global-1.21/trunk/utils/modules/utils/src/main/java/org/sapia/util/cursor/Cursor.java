package org.sapia.util.cursor;

/**
 * This class specifies cursor behavior. A <code>Cursor</code> is a sequence
 * of <code>Batch</code> instances. A cursor instance creates batches through
 * a <code>CursorFeed</code>.
 * 
 * @see org.sapia.util.cursor.Batch
 * @see org.sapia.util.cursor.CursorFeed
 * @see org.sapia.util.cursor.CursorFactory
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
public interface Cursor {

  /**
   * Returns the number of items in this cursor. Note that the returned count
   * should not be necessarily relied upon: a cursor might be fed
   * asynchronously, and thus the number of items may not be constant over time,
   * up to a certain limit.
   * 
   * @return the number of items in this cursor (more precisely: the sum of the
   *         number of items that all batches in this instance hold).
   */
  public int getItemCount();

  /**
   * @return the number of <code>Batch</code> instances held with this
   *         instance.
   * @see #getItemCount()
   */
  public int getBatchCount();
  
  /**
   * @return <code>true</code> if this instance has a batch pending.
   */
  public boolean hasNextBatch();

  /**
   * @return the next <code>Batch</code>
   */
  public Batch nextBatch();

  /**
   * @return <code>true</code> if this instance has a "previous" batch
   *         pending.
   */
  public boolean hasPreviousBatch();

  /**
   * @return the previous <code>Batch</code>
   */
  public Batch previousBatch();
  

  /**
   * @return <code>true</code> if this cursor has a "current" batch.
   */
  public boolean hasCurrentBatch();
  
  /**
   * Returns the "current" batch (or, rather, the last batch that was fetched).
   * 
   * @return the <code>Batch</code> that is currently pointed to.
   */
  public Batch currentBatch();  

  /**
   * @return the batch size with which this instance was configured.
   */
  public int getBatchSize();
  
  /**
   * @param size the maximum size of the batches that this instance will return.
   */
  public void setBatchSize(int size);

  /**
   * Positions this instance at the beginning of the first batch.
   * 
   * @return this instance.
   */
  public Cursor toFirst();

  /**
   * Position this instance at the beginning of the last batch.
   * 
   * @return this instance.
   */
  public Cursor toLast();
  
  /**
   * @return the current position of the cursor, in terms of
   * the batch it currently points to.
   */
  public int getPos();
  
  /**
   * positions this cursor's internal index to the beginning (such
   * that thereafter calling <code>nextBatch()</code> will return
   * this instance's first batch.
   */
  public void reset();  
  
  /**
   * @return the current position of the cursor, in terms of
   * the individual items it contains.
   */
  public int getAbsolutePos();
  
  /**
   * @param pos the position to which this instance should be set,
   * in terms of the individual items it contains.
   */
  public void setAbsolutePos(int pos);
  
  /**
   * @return <code>true</code> if this instance's current position corresponds to its 
   * first item.
   */
  public boolean isStart();
  
  /**
   * @return <code>true</code> if this instance's current position corresponds to its 
   * last item.
   */  
  public boolean isEnd();
  
  /**
   * Returns the object at the given position.
   * 
   * @param pos a position.
   * @return the object at the given position.
   */
  public Object getItemAt(int pos);
  
  /**
   * Removes the item at the given position and returns it.
   * 
   * @param pos
   * @return the object at the given position.
   */
  public Object removeItemAt(int pos);

  /**
   * This non-blocking method can be polled to inquire about the number of
   * pending objects in this instance's underlying <code>CursorFeed</code>.
   * <p>
   * This method can be used in cases where an <code>hasNext()</code> call has
   * returned false, and where the underlying feed is asynchronously filled with
   * data. In such cases, the false <code>hasNext()</code> call may reflect a
   * temporary situation. This method conveniently allows insuring that indeed
   * no data is pending.
   * 
   * @return the number of pending object in the underlying feed.
   * 
   * @see CursorFeed#available()
   */
  public int available();
  
  /**
   * Client applications should call this method when they are finished with this
   * instance. This allows all resources to be released. In particular, this allows 
   * sparing processing resources in cases where <code>CursorFeed</code>s have an 
   * asynchronous behavior.
   * 
   * @see CursorFeed#close() 
   */
  public void close();

}
