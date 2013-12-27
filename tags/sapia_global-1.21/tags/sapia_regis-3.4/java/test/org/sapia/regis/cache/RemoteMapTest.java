package org.sapia.regis.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class RemoteMapTest extends TestCase {
  
  private Map remote;
  private Map delegate;
  
  protected void setUp() throws Exception {
    delegate = new HashMap();
    delegate.put("prop1", "value1");
    delegate.put("prop2", "value2");
    remote = new RemoteMap(delegate);
  }

  /*
   * Test method for 'org.sapia.regis.cache.RemoteMap.entrySet()'
   */
  public void testSerializeEntrySet() throws Exception{
    serialize(remote.entrySet());
    try{
      serialize(delegate.entrySet());
      fail("Should have thrown NotSerializableException");
    }catch(Exception e){}
    
    super.assertEquals(2, remote.entrySet().size());
    super.assertTrue(remote.entrySet().contains(new SerializableEntry("prop1", "value1")));
  }

  /*
   * Test method for 'org.sapia.regis.cache.RemoteMap.keySet()'
   */
  public void testSerializeKeySet() throws Exception{
    serialize(remote.keySet());
    try{
      serialize(delegate.keySet());
      fail("Should have thrown NotSerializableException");
    }catch(Exception e){}
  }

  /*
   * Test method for 'org.sapia.regis.cache.RemoteMap.values()'
   */
  public void testSerializeValues() throws Exception{
    serialize(remote.values());
    try{
      serialize(delegate.values());
      fail("Should have thrown NotSerializableException");
    }catch(Exception e){}
  }
  
  private void serialize(Object o) throws Exception{
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream os = new ObjectOutputStream(bos);
    os.writeObject(o);
    os.flush();
    os.close();
    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
    ois.readObject();
    
  }

}
