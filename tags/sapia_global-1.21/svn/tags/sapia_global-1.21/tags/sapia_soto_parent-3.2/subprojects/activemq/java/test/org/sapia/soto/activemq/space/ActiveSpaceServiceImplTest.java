package org.sapia.soto.activemq.space;

import java.io.File;
import java.io.Serializable;

import junit.framework.TestCase;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.codehaus.activespace.Space;
import org.codehaus.activespace.SpaceFactory;
import org.sapia.soto.SotoContainer;
import org.sapia.soto.activemq.VmBrokerService;
import org.sapia.soto.activemq.example.SampleEntry;
import org.sapia.soto.activemq.example.SpaceTaker;

public class ActiveSpaceServiceImplTest extends TestCase {
  
  VmBrokerService broker;
  ActiveMQConnectionFactory fac;
  ActiveSpaceServiceImpl spaceService;
  
  public void setUp() throws Exception{
    broker  = new VmBrokerService();
    fac = new ActiveMQConnectionFactory("vm://localhost");
    broker.start();
    spaceService = new ActiveSpaceServiceImpl();
    spaceService.setConnectionFactory(fac);
    spaceService.init();
    
  }
  
  protected void tearDown() throws Exception {
    broker.stop();
  }

  public void testPolymorphicEntry() throws Exception {
    Cat cat = new Cat();
    Space taker = spaceService.createSpace("test", SpaceFactory.DISPATCH_ONE_CONSUMER, new Animal());    
    ActiveSpace putter = (ActiveSpace)spaceService.createSpace("test", SpaceFactory.DISPATCH_ONE_CONSUMER, null);
    putter.putAs(new Cat(), Animal.class);
    cat = (Cat)taker.take(2000);
    if(cat == null){
      throw new Exception("No entry found");
    }
  }  

  public void testSpecificEntry() throws Exception {
    TestEntry entry = new TestEntry();
    entry.entryId = "foo";
    Space space = spaceService.createSpace("test", SpaceFactory.DISPATCH_ONE_CONSUMER, entry);
    space.put(entry);
    entry = (TestEntry)space.take(2000);
    if(entry == null){
      throw new Exception("No entry found");
    }
    assertEquals("foo", entry.entryId);
  }
   
  public void testNullEntry() throws Exception {
    TestEntry template = new TestEntry();
    Space space = spaceService.createSpace("test", SpaceFactory.DISPATCH_ONE_CONSUMER, template);
    
    TestEntry entry = new TestEntry();
    entry.entryId = "foo";
    space.put(entry);
    
    entry = (TestEntry)space.take(2000);
    if(entry == null){
      throw new Exception("No entry found");
    }
    assertEquals("foo", entry.entryId);
  }
  
  public void testNoMatch() throws Exception {
    TestEntry template = new TestEntry();
    template.entryId = "foo";
    Space space = spaceService.createSpace("test", SpaceFactory.DISPATCH_ONE_CONSUMER, template);
    
    TestEntry entry = new TestEntry();
    entry.entryId = "bar";
    space.put(entry);
    
    entry = (TestEntry)space.take(2000);
    if(entry != null){
      fail("Entry should not have been found");
    }
  }   
  
  public void testConfiguration() throws Exception{
    SotoContainer container = new SotoContainer();
    container.load(new File("etc/activemq/activeSpace.xml"));
    container.start();
    SpaceTaker taker = (SpaceTaker)container.lookup("taker");
    Space space = taker.getSpace();
    space.put(new SampleEntry());
    SampleEntry entry = (SampleEntry)space.take(2000);
    if(entry == null){
      throw new Exception("No entry found");
    }    
  }
  
  /*
  public void testRequestReply() throws Exception{
    ActiveSpace space = (ActiveSpace)spaceService.createSpace("test", SpaceFactory.DISPATCH_ONE_CONSUMER, new Animal());
    Replier replier = new Replier();
    replier.space = space;
    Thread t = new Thread(replier);
    t.start();
    Request req = Request.create(new Cat());
    Space resp = space.request(req, 10000, Animal.class);
    
    for(int i = 0; i < 3; i++){
      Reply reply = (Reply)resp.take(5000);
      if(reply == null){
        fail("Reply not received");
      }
    }
    
  }*/
  
  static class Animal implements Serializable{}
  static class Cat extends Animal{}  
  static class Dog extends Animal{}  
  
  
  public static class Replier implements Runnable {
    
    ActiveSpace space;
    Request req;
    Exception e;
    public void run() {
      try{
        req = (Request)space.take(2000);
        if(req == null){
          e = new Exception("Request is null");
          throw e;
        }
        else{
          for(int i = 0; i < 3; i++){
            space.reply(Reply.create(req, "RESPONSE"), 2000);
          }
        }
      }catch(Exception e){
        e.printStackTrace();
        this.e = e;
      }
    }
    
    public Request getRequest() throws Exception{
      if(req == null)
        throw new IllegalStateException("Request not receive");
      if(e != null){
        throw e;
      }
      return req;
    } 
    
    
  }

}
