/*
 * CursorCursorFeed.java
 *
 * Created on March 24, 2005, 9:56 AM
 */

package org.sapia.util.cursor.impl;

import org.sapia.util.cursor.Batch;
import org.sapia.util.cursor.Cursor;
import org.sapia.util.cursor.CursorFeed;

/**
 *
 * @author yduchesne
 */
public class CursorCursorFeed implements CursorFeed{
  
  private Cursor _cursor;
  private boolean _enableClose;
  private Batch _current;
  
  public CursorCursorFeed(Cursor cursor){
    _cursor = cursor;
  }
  
  /**
   * Indicates if this instance should close its underlying cursor upon its own
   * <code>close()</code> method being called. Defaults to <code>false</code>.
   *
   * @param enabled if <code>true</code>, this instance will call its 
   * encapsulated cursor's own <code>close()</code> method.
   */
  public void setEnableClose(boolean enabled){
    _enableClose = enabled;
  }
  
  public int read(Object[] buffer) throws Exception{
    if(_current == null || !_current.hasNext()){
      if(_cursor.hasNextBatch()){
        _current = _cursor.nextBatch();
        if(!_current.hasNext()){
          return 0;
        }
      }
      else{
        return 0;
      }
    }
    int count = 0;
    while(count < buffer.length){
      buffer[count] = _current.next();
      count++;
    }
    return count;
  }

  public int available() throws Exception{
    if(_current == null || !_current.hasNext()){
      if(_cursor.hasNextBatch()){
        _current = _cursor.nextBatch();
        if(!_current.hasNext()){
          return 0;
        }
        return _current.getCount();
      }
      else{
        return 0;
      }
    }
    return _current.getCount() - _current.getPos();
  }
  
  public void close(){
    if(_enableClose)
      _cursor.close();
  }
  
}
