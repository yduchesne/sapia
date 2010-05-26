package org.sapia.util.cursor.impl;

import java.util.ArrayList;
import java.util.List;

import org.sapia.util.cursor.Batch;
import org.sapia.util.cursor.Cursor;

import junit.framework.TestCase;

/**
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class CursorImplTest extends TestCase{
  
  Cursor _cursor;
  
  public CursorImplTest(String name){
    super(name);
  }
  
  public void setUp(){
    List items = new ArrayList();
    for(int i = 0; i < 12; i++){
      items.add(""+i);
    }
    ListCursorFeed feed = new ListCursorFeed(items);    
    _cursor = new CursorImpl(feed, null, 3);
  }
  
  public void testGetAbsolutePos(){
    while(_cursor.hasNextBatch()){
      _cursor.nextBatch();
    }
    _cursor.setAbsolutePos(5);
    super.assertEquals(5, _cursor.getAbsolutePos());
  }
  
  public void testNextBatch(){
    Batch next;
    int count = 0;
    int totalcount = 0;
    while(_cursor.hasNextBatch()){
      next = _cursor.nextBatch();
      super.assertEquals(3, next.getCount());
      super.assertEquals(totalcount, next.getAbsolutePos());      
      count++;
      totalcount = totalcount + next.getCount();      
    }
    super.assertEquals(4, count);
  }
  
  public void testReset(){
    while(_cursor.hasNextBatch()){
      _cursor.nextBatch();
    }
    _cursor.reset();
    super.assertTrue(_cursor.hasNextBatch());
    super.assertEquals("0", _cursor.nextBatch().getItemAt(0));
  }
  
  public void testPreviousBatch(){
    Batch batch;
    int count = 0;
    while(_cursor.hasNextBatch()){
      batch = _cursor.nextBatch();
      super.assertEquals(3, batch.getCount());
      count++;
    }
    super.assertEquals(4, count);
    
    count = 0;
    while(_cursor.hasPreviousBatch()){
      batch = _cursor.previousBatch();
      super.assertEquals(3, batch.getCount());
      count++;
    }
    super.assertEquals(3, count);
    super.assertTrue(_cursor.hasNextBatch());
    super.assertEquals("3", _cursor.nextBatch().getItemAt(0));
  }  
  
  public void testSetGetFirstPos(){
    while(_cursor.hasNextBatch()){
      _cursor.nextBatch();
    }    
    _cursor.setAbsolutePos(0);
    Batch b = _cursor.currentBatch();
    super.assertTrue(b.hasNext());
    super.assertEquals("0", b.next());
    _cursor.setAbsolutePos(0);
    super.assertEquals("0", _cursor.getItemAt(0));
  }
  
  public void testSetGetLastPos(){
    while(_cursor.hasNextBatch()){
      _cursor.nextBatch();
    }    
    _cursor.setAbsolutePos(_cursor.getItemCount() - 1);
    Batch b = _cursor.currentBatch();
    super.assertTrue(b.hasNext());
    super.assertEquals("11", b.next());
    _cursor.setAbsolutePos(_cursor.getItemCount() - 1);
    super.assertEquals("11", _cursor.getItemAt(_cursor.getItemCount() - 1));
  }  
  
  public void testSetGetMidPos(){
    while(_cursor.hasNextBatch()){
      _cursor.nextBatch();
    }    
    _cursor.setAbsolutePos(5);
    Batch b = _cursor.currentBatch();
    super.assertTrue(b.hasNext());
    super.assertEquals("5", b.next());
    _cursor.setAbsolutePos(5);
    super.assertEquals("5", _cursor.getItemAt(5));
  }  
  
  public void testIsStart(){
    while(_cursor.hasNextBatch()){
      _cursor.nextBatch();
    }    
    _cursor.setAbsolutePos(0);
    super.assertTrue(_cursor.isStart());
  }
  
  public void testIsEnd(){
    while(_cursor.hasNextBatch()){
      _cursor.nextBatch();
    }    
    _cursor.setAbsolutePos(_cursor.getItemCount() - 1);
    super.assertTrue(_cursor.isEnd());
  } 
  
  public void testRemoveItemAt(){
    while(_cursor.hasNextBatch()){
      _cursor.nextBatch();
    }   
    for(int i = 0; i < 12; i++){
      super.assertEquals(""+i, _cursor.removeItemAt(0));
      if(_cursor.getItemCount() > 0)
        super.assertEquals(""+(i+1), _cursor.getItemAt(0));
    }
  }    
  
  public void testSetBatchSize(){
    while(_cursor.hasNextBatch()){
      _cursor.nextBatch();
    }   
    _cursor.setBatchSize(6);
    for(int i = 0; i < 12; i++){
      super.assertEquals(""+i, _cursor.getItemAt(i));
    }
    
    _cursor.setBatchSize(2);
    for(int i = 0; i < 12; i++){
      super.assertEquals(""+i, _cursor.getItemAt(i));
    }    
  }   
  
  public void testSingleResult(){
    List items = new ArrayList();
    items.add("0");
    ListCursorFeed feed = new ListCursorFeed(items);    
    Cursor cursor = new CursorImpl(feed, null, 3);  
    Batch b = cursor.nextBatch();
    super.assertEquals("0", b.next());
  }
  

}
