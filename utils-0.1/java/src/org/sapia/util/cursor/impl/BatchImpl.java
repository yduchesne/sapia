package org.sapia.util.cursor.impl;

import org.sapia.util.cursor.Batch;
import org.sapia.util.cursor.Cursor;
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
public class BatchImpl implements Batch{
  
  private Cursor _owner;
  private Object[] _items;
  private int _count, _index;
  private CursorListener _listener;
  private int _pos;
  
  /**
   * @param items the array of objects that this instance will wrap.
   * @param listener a <code>CursorListener</code>, or <code>null</code> if none is provided.
   * @param count the number of actual items in the array.
   * @param absoluteIndex the index of this instance, relative to its cursor.
   */
  public BatchImpl(Cursor owner, Object[] items, CursorListener listener, int count, int absoluteIndex){
    _items = items;
    _listener = listener;
    _count = count;
    _index = absoluteIndex;
    _owner = owner;
  }
  
  /**
   * @see Batch#getCount()
   */
  public int getCount(){
    return _count;
  }
  
  /**
   * @see org.sapia.util.cursor.Batch#getIndex()
   */
  public int getIndex() {
    return _index;
  }
  
  /**
   * @see Batch#getAbsolutePos()
   */
  public int getAbsolutePos(){
    return _index * _owner.getBatchSize() + _pos;
  }
  
  /**
   * @see Batch#getPos()
   */ 
  public int getPos(){
    return _pos;
  }
  
  /**
   * @see Batch#setPos(int)
   */
  public void setPos(int pos){
    if(_count == 0 || pos < 0 || pos >= _count){
      throw new IndexOutOfBoundsException(""+pos);
    }
    _pos = pos;
  }
  
  /**
   * @see Batch#toFirst()
   */
  public Batch toFirst(){
    _pos = 0;
    return this;
  }
  
  /**
   * @see Batch#toLast()
   */
  public Batch toLast(){
    _pos = _count - 1;
    return this;
  }
  
  /**
   * @see Batch#getItemAt(int)
   */
  public Object getItemAt(int index){
    if(index < 0 || index >= _count){
      throw new java.lang.IndexOutOfBoundsException(""+index);
    }
    if(_listener != null)
      return _listener.onNext(_items[index]);
    return _items[index];
  }
  
  /**
   * @see Batch#removeItemAt(int)
   */  
  public Object removeItemAt(int index){
    if(index < 0 || index >= _count){
      throw new java.lang.IndexOutOfBoundsException(""+index);
    }
    Object toReturn = null;
    if(_listener != null){
      toReturn = _listener.onNext(_items[index]);
    }
    else{
      toReturn = _items[index];    
    }
    System.arraycopy(_items, index+1, _items, index, _count-(index+1));
    _count--;
    return toReturn;
  }  
  
  /**
   * @see Batch#hasNext()
   */
  public boolean hasNext(){
    return _pos < _count;
  }
  
  /**
   * @see org.sapia.util.cursor.Batch#hasNextBatch()
   */
  public boolean hasNextBatch() {
    if(_index < _owner.getBatchCount() - 1){
      return true;
    }
    return _owner.hasNextBatch();
  }
  
  /**
   * @see Batch#next()
   */
  public Object next(){
    if(_pos >= _count){
      throw new java.util.NoSuchElementException();
    }
    if(_listener != null){
       return _listener.onNext(_items[_pos++]);
    }
    return _items[_pos++];
  }
  
  /**
   * @see Batch#hasPrevious()
   */
  public boolean hasPrevious(){
    return _pos > 0;
  }
  
  /**
   * @see org.sapia.util.cursor.Batch#hasPreviousBatch()
   */
  public boolean hasPreviousBatch() {
    return _index > 0;
  }
  
  /**
   * @see Batch#previous()
   */
  public Object previous(){
    if(_pos < 0){
      throw new java.util.NoSuchElementException();
    }
    if(_listener != null){
      return _listener.onNext(_items[_pos--]);
    }
    return _items[_pos--];
  }
    
  
  /**
   * @see BatchImpl#getIterator()
   */
  public java.util.Iterator getIterator(){
    return new BatchIterator(this, false);
  }
  
  /**
   * @see BatchImpl#getReverseIterator()
   */
  public java.util.Iterator getReverseIterator(){
    return new BatchIterator(this, true);
  }  
  
  Object[] items(){
    return _items;
  }
  
  ////////////////// INNER CLASSES /////////////////
  
  public static class BatchIterator implements java.util.Iterator{
    
    private Batch _batch;
    private boolean _reverse;
    
    BatchIterator(Batch batch, boolean reverse){
      _batch = batch;
      _reverse = reverse;
    }
    
    public boolean hasNext(){
      if(_reverse){
        return _batch.hasPrevious();
      }
      return _batch.hasNext();
    }
    
    public Object next(){
      if(_reverse){
        return _batch.previous();
      }
      return _batch.next();
    }
    
    public void remove(){
      throw new UnsupportedOperationException();
    }
  }
}
