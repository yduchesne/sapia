package org.sapia.util.cursor.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.sapia.util.cursor.Batch;
import org.sapia.util.cursor.Cursor;
import org.sapia.util.cursor.CursorException;
import org.sapia.util.cursor.CursorFeed;
import org.sapia.util.cursor.CursorListener;

/**
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class CursorImpl implements Cursor{
  
  private int _batchSize;
  private List _batches = new ArrayList(20);
  private int _pos = -1;;
  private int _totalCount;
  private boolean _finished;
  private CursorFeed _feed;
  private Batch _currentBatch;
  private CursorListener _listener;
  
  /**
   * @param feed a <code>CursorFeed</code>.
   * @param listener a <code>CursorListener</code>, or <code>null</code> if no listener is provided.
   * @param batchSize the size of the <code>Batch</code> instances that will
   * be created by this instance.
   */
  public CursorImpl(CursorFeed feed, CursorListener listener, int batchSize){
    _batchSize = batchSize;
    _feed = feed;
    _listener = listener;
  }
  
  /**
   * @see org.sapia.util.cursor.Cursor#getBatchCount()
   */
  public int getBatchCount() {
    return _batches.size();
  }
  
  /**
   * @see org.sapia.util.cursor.Cursor#getItemCount()
   */
  public int getItemCount() {
    return _totalCount;
  }
  
  /**
   * @see org.sapia.util.cursor.Cursor#getPos()
   */
  public int getPos() {
    return _pos;
  }
  
  /**
   * @see org.sapia.util.cursor.Cursor#reset()
   */
  public void reset() {
    _pos = -1;
    _currentBatch = null;
  }
  
  /**
   * @see org.sapia.util.cursor.Cursor#setPos(int)
   */
  /*public void setPos(int pos) {
    if(pos < 0 || pos >= _batches.size()){
      throw new IndexOutOfBoundsException(""+pos);
    }
    _pos = pos;
  }
  */
  
  /**
   * @return the current position of the cursor, in terms of
   * the individual items it contains.
   */
  public int getAbsolutePos(){
    if(_currentBatch == null){
      return 0;
    }
    return _currentBatch.getAbsolutePos();
  }
  
  /**
   * @param pos the position to which this instance should be set,
   * in terms of the individual items it contains.
   */
  public void setAbsolutePos(int pos){
    if(_currentBatch != null){
      if(pos < _batchSize){
        _currentBatch = (Batch)_batches.get(0);
        _currentBatch.setPos(pos);
        _pos = 0;
      }
      else{
        int batchPos = pos/_batchSize;
        int offset   = pos%_batchSize;
        if(batchPos >= _batches.size()){
          throw new IndexOutOfBoundsException(""+pos);
        }
        Batch b = (Batch)_batches.get(batchPos);
        if(offset >= b.getCount()){
          throw new IndexOutOfBoundsException(""+pos);
        }
        b.setPos(offset);
        _pos = batchPos;        
        _currentBatch = b;
      }
    }
    else if(pos == 0){
      _pos = pos;
    }else{
      throw new IndexOutOfBoundsException(""+pos);
    }
  }
  
  /**
   * @see org.sapia.util.cursor.Cursor#getItemAt(int)
   */
  public Object getItemAt(int pos) {
    if(_currentBatch != null){
      if(pos < _batchSize){
        _currentBatch = (Batch)_batches.get(0);
        return _currentBatch.getItemAt(pos);
      }
      else{
        int batchPos = pos/_batchSize;
        int offset   = pos%_batchSize;
        if(batchPos >= _batches.size()){
          throw new IndexOutOfBoundsException(""+pos);
        }
        Batch b = (Batch)_batches.get(batchPos);
        if(offset >= b.getCount()){
          throw new IndexOutOfBoundsException(""+pos);
        }
        return b.getItemAt(offset);
      }
    }
    throw new IndexOutOfBoundsException(""+pos);    
  }
  
  /**
   * @see org.sapia.util.cursor.Cursor#getItemAt(int)
   */  
  public Object removeItemAt(int pos) {
    if(_currentBatch != null){
      Object toReturn;
      if(pos < _batchSize){
        _currentBatch = (Batch)_batches.get(0);
        toReturn = _currentBatch.removeItemAt(pos);
      }
      else{
        int batchPos = pos/_batchSize;
        int offset   = pos%_batchSize;
        if(batchPos >= _batches.size()){
          throw new IndexOutOfBoundsException(""+pos);
        }
        Batch b = (Batch)_batches.get(batchPos);
        if(offset >= b.getCount()){
          throw new IndexOutOfBoundsException(""+pos);
        }
        toReturn = b.removeItemAt(offset);
      }
      _totalCount--;
      restructure(_batchSize);
      return toReturn;
    }
    throw new IndexOutOfBoundsException(""+pos);      
  }  
  
  /**
   * @see org.sapia.util.cursor.Cursor#isStart()
   */
  public boolean isStart(){
    if(_pos == 0){
      Batch b = (Batch)_batches.get(_pos);
      return b.getPos() == 0;
    }
    return false;
  }

  /**
   * @see org.sapia.util.cursor.Cursor#isEnd()
   */
  public boolean isEnd(){
    if(_pos == _batches.size() - 1){
      Batch b = (Batch)_batches.get(_pos);
      return b.getPos() == b.getCount() - 1;
    }
    return false;
  }
  
  /**
   * 
   * @see Cursor#getBatchSize()
   */
  public int getBatchSize(){
    return _batchSize;
  }
  
  /**
   * 
   * @see Cursor#setBatchSize(int)
   */  
  public void setBatchSize(int batchSize){
    if(batchSize <= 0)
      throw new IllegalArgumentException("Batch size must be greater than 0");
    if(_currentBatch == null){
      _batchSize = batchSize;
    }
    else{
      restructure(batchSize);
    }
  }
  
  /**
   * @see org.sapia.util.cursor.Cursor#hasCurrentBatch()
   */
  public boolean hasCurrentBatch() {
    if(_currentBatch == null){
      if(hasNextBatch()){
        _currentBatch = nextBatch();
        return true;
      }
      return false;
    }
    return true;
  }
  
  /**
   * @see org.sapia.util.cursor.Cursor#currentBatch()()
   */
  public Batch currentBatch(){
    if(_currentBatch == null){
      if(hasCurrentBatch()){
        return _currentBatch;
      }
      throw new NoSuchElementException();
    }
    return _currentBatch;
  }
  
  /**
   * @see Cursor#hasNextBatch()
   */
  public boolean hasNextBatch(){
    if(_pos < _batches.size() - 1){
      return true;
    }
    else if(_finished){
      return false;
    }
    else if(_pos >= _batches.size() - 1){
      Object[] items = newItems();
      int count = 0;
      try{
        count = _feed.read(items);
      }catch(Exception e){
        throw new CursorException("Could not feed", e);
      }
      if(count  == 0){
        _finished = true;
        return false;
      }
      Batch b = new BatchImpl(this, items, _listener, count, _batches.size());
      _batches.add(b);
      _totalCount = _totalCount+count;
      return true;
    }
    else{
      return true;
    }
  }
  
  /**
   * @see Cursor#hasPreviousBatch()
   */
  public boolean hasPreviousBatch(){
    if(_currentBatch != null){
      return _currentBatch.hasPreviousBatch();
    }
    return _pos > 0;
  }
  
  /**
   * @see Cursor#nextBatch() 
   */
  public Batch nextBatch(){
    if(_pos >= _batches.size() - 1){
      if(!hasNextBatch()){
        throw new NoSuchElementException();        
      }      
    }
    _currentBatch = (Batch)_batches.get(++_pos);
    return _currentBatch.toFirst();
    
  }
  
  /**
   * @return Cursor#previousBatch()
   */
  public Batch previousBatch(){
    if(_pos <= 0){
      throw new NoSuchElementException();
    }  
    _currentBatch = (Batch)_batches.get(--_pos);
    /*
    if(_currentBatch != null){
      int pos = _currentBatch.getIndex();
      pos--;
      if(pos < 0){
        throw new NoSuchElementException(); 
      }
      else{
        _pos = pos;
        _currentBatch = (Batch)_batches.get(_pos);
      }
    }
    else{
      if(_pos > _batches.size()){
        _pos--;
      }
      _currentBatch = (Batch)_batches.get(_pos--);      
    }*/
    return _currentBatch.toFirst();
  }
  
  /**
   * @return Cursor#toLast()
   */
  public Cursor toLast(){
    if(_batches.size() > 0)
      _pos = _batches.size() - 1;
    return this;
  }
  
  /**
   * @return Cursor#toFirst()
   */
  public Cursor toFirst(){
    _pos = 0;
    return this;
  }
  
  /**
   * @see org.sapia.util.cursor.Cursor#available()
   */
  public int available() {
    try{
      return _feed.available();
    }catch(Exception e){
      throw new CursorException("Could not poll underlying feed", e);
    }
  }
  
  /**
   * @see org.sapia.util.cursor.Cursor#close()
   */
  public void close() {
    _feed.close();
  }
  
  private void restructure(int batchSize){
    int absolutePos = getAbsolutePos();
    int totalCount = 0;
    List newBatches = new ArrayList(20);
    Batch newBatch = null;
    Object[] newItems = null;
    int count = 0;
    for(int i = 0; i < _batches.size(); i++){
      BatchImpl current = (BatchImpl)_batches.get(i);
      Object[] currentItems = current.items();
      if(newItems == null){
        newItems = new Object[batchSize];
      }
      for(int j = 0; j < current.getCount(); j++){
        if(count >= newItems.length){
          newBatches.add(new BatchImpl(this, newItems, _listener, count, newBatches.size()));
          newItems = new Object[batchSize];
          count = 0;
        }
        newItems[count] = currentItems[j];
        totalCount++;
        count++;
      }
    }
    if(newItems != null){
      newBatches.add(new BatchImpl(this, newItems, _listener, count, newBatches.size()));      
    }
    _totalCount = totalCount;
    if(absolutePos >= _totalCount){
      absolutePos = _totalCount - 1;
    }
    _batches = newBatches;
    _batchSize = batchSize;    
    if(_totalCount > 0){
      _currentBatch = null;
      _pos = -1;
      nextBatch();
      setAbsolutePos(absolutePos);
    }
    else{
      _pos = -1;
      _currentBatch = null;
    }
  }
  
  private Object[] newItems(){
    return new Object[_batchSize];
  }
}
