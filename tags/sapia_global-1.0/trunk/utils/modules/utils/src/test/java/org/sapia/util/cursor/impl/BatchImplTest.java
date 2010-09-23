/*
 * BatchImplTest.java
 * JUnit based test
 *
 * Created on November 7, 2004, 5:25 PM
 */

package org.sapia.util.cursor.impl;

import junit.framework.TestCase;
import org.sapia.util.cursor.impl.BatchImpl;
import java.util.Iterator;

/**
 *
 * @author yduchesne
 */
public class BatchImplTest extends TestCase {
  
  private BatchImpl _batch;
  private CursorImpl _cursor;
  
  public BatchImplTest(String testName) {
    super(testName);
  }
  
  public void setUp(){
    Object[] items = new Object[10];
    for(int i = 0; i < 7; i++){
      items[i] = ""+i;
    }
    _cursor = new CursorImpl(null, null, 7);
    _batch = new BatchImpl(_cursor, items, null, 7, 1);
  }

  public void testGetCount() {
    super.assertEquals(7, _batch.getCount());
  }

  public void testGetAbsolutePos() {  
    super.assertEquals(7, _batch.getAbsolutePos());    
    _batch.next();
    super.assertEquals(8, _batch.getAbsolutePos());    
  }


  public void testGetPos() {
    super.assertEquals(0, _batch.getPos());    
    _batch.next();
    super.assertEquals(1, _batch.getPos());    
  }

  public void testSetPos() {
    _batch.setPos(1);
    super.assertEquals(1, _batch.getPos());    
  }
  
  public void testToFirst() {
    _batch.setPos(1);
    super.assertEquals(1, _batch.getPos());    
    _batch.toFirst();
    super.assertEquals(0, _batch.getPos());        
  }

  public void testToLast() {
    _batch.toLast();
    super.assertEquals(6, _batch.getPos());        
  }

  public void testGetItemAt() {
    for(int i = 0; i < _batch.getCount(); i++){
      super.assertEquals(""+i, _batch.getItemAt(i));
    }
  }
  
  public void testRemoveItemAt() {
    int count = 0;
    while(_batch.getCount() > 0){
      super.assertEquals(""+count, _batch.removeItemAt(0));
      count++;
    }
    super.assertEquals(7, count);    
    super.assertEquals(0, _batch.getCount());    
  }  

  public void testNext() {
    int count = 0;
    while(_batch.hasNext()){
      super.assertEquals(""+count, _batch.next());      
      count++;
    }
  }

  public void testPrevious() {
    _batch.toLast();
    int count = _batch.getPos();
    while(_batch.hasPrevious()){
      super.assertEquals(""+count, _batch.previous());      
      count--;
    }
  }

  public void testGetIterator() {
    int count = 0;
    Iterator itr = _batch.getIterator();
    while(itr.hasNext()){
      super.assertEquals(""+count, itr.next());      
      count++;
    }
  }

  public void testGetReverseIterator() {
    _batch.toLast();
    int count = _batch.getPos();
    Iterator itr = _batch.getReverseIterator();    
    while(itr.hasNext()){
      super.assertEquals(""+count, itr.next());      
      count--;
    }
  }  
}
