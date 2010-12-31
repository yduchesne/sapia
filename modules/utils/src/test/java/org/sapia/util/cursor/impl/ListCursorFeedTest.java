package org.sapia.util.cursor.impl;

import java.util.ArrayList;
import java.util.List;

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
public class ListCursorFeedTest extends TestCase{
  
  public ListCursorFeedTest(String name){
    super(name);
  }
  
  public void testRead() throws Exception{
    Object[] buffer = new Object[4];
    List items = new ArrayList();
    for(int i = 1; i <= 12; i++){
      items.add(""+i);
    }
    ListCursorFeed feed = new ListCursorFeed(items);
    int total = 1;
    for(int i = 1; i <= 3; i++){
      int count = feed.read(buffer);
      super.assertEquals(4, count);
      for(int j = 0; j < count; j++, total++){
        super.assertEquals(""+total, buffer[j]);
      }
    }
    super.assertEquals(0, feed.read(buffer));
  }
}
