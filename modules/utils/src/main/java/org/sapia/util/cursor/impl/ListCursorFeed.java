package org.sapia.util.cursor.impl;

import java.util.List;

import org.sapia.util.cursor.CursorFeed;

/**
 * Implements the <code>CursorFeed</code> interface over the
 * <code>java.util.List</code> interface.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class ListCursorFeed implements CursorFeed{
  
  private List _feed;
  private int  _currentIndex;
  private boolean _closed;
  
  public ListCursorFeed(List feed){
    _feed = feed;
  }
  
  /**
   * @see org.sapia.util.cursor.CursorFeed#read(java.lang.Object[])
   */
  public int read(Object[] buffer) throws Exception {
    int count = 0;
    if(_closed || _currentIndex >= _feed.size()){
      return 0;
    }
    for(; count < buffer.length && _currentIndex < _feed.size(); _currentIndex++, count++){
      buffer[count] =_feed.get(_currentIndex);
    }
    return count;
  }
  
  /**
   * @see org.sapia.util.cursor.CursorFeed#available()
   */
  public int available() throws Exception {
    if(_closed){
      return 0;
    }
    return _feed.size() - _currentIndex;
  }
  
  /**
   * @see org.sapia.util.cursor.CursorFeed#close()
   */
  public void close() {
    _closed = true;
  }
}
