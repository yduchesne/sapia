package org.sapia.util.cursor.impl;

import java.util.Iterator;

import org.sapia.util.cursor.CursorFeed;

/**
 * Implements the <code>CursorFeed</code> interface over the
 * <code>java.util.Iterator</code> interface.
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
public class IteratorCursorFeed implements CursorFeed {

  private Iterator _feed;
  private boolean  _closed;

  public IteratorCursorFeed(Iterator feed) {
    _feed = feed;
  }

  /**
   * @see org.sapia.util.cursor.CursorFeed#read(java.lang.Object[])
   */
  public int read(Object[] buffer) throws Exception {
    int count = 0;
    if(_closed || !_feed.hasNext()) {
      return 0;
    }
    while(count < buffer.length && _feed.hasNext()) {
      buffer[count] = _feed.next();
      count++;
    }
    return count;
  }

  /**
   * @see org.sapia.util.cursor.CursorFeed#available()
   */
  public int available() throws Exception {
    if(_closed) {
      return 0;
    }
    if(_feed.hasNext())
      return 1;
    else
      return 0;
  }

  /**
   * @see org.sapia.util.cursor.CursorFeed#close()
   */
  public void close() {
    _closed = true;
  }
}
