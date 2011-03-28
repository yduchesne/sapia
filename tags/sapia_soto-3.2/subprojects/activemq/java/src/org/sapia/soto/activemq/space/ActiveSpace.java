package org.sapia.soto.activemq.space;

import org.codehaus.activespace.Space;

/**
 * This interface extends the {@link org.codehaus.activespace.Space} interface
 * by adding to method used to support "pseudo" polymorphic takes. 
 * <p>
 * An instance of this class is used as follows (first, on the "taker" side):

 * <pre>
 * // factory is an instance of ActiveSpaceFactory
 * Space space = factory.createSpace("animals", 
 *   SpaceFactory.DISPATCH_ONE_CONSUMER, new Animal());
 *   
 * Animal animal = space.take();
 * </pre>
 * <p>
 * And on the "putter" side we would have:
 * <pre>
 * ActiveSpace space = (ActiveSpace)factory.createSpace("animals", 
 *   SpaceFactory.DISPATCH_ONE_CONSUMER, null);
 *   
 * space.putAs(new Cat(), Animal.class);
 * </pre>
 * <p>
 * 
 * @author yduchesne
 *
 */
public interface ActiveSpace extends Space{
  
  /**
   * @param entry an <code>Object</code>.
   * @param type the <code>Class</code> as which the object should 
   * be put into this instance. 
   */
  public void putAs(Object entry, Class type);

  /**
   * 
   * @param entry an <code>Object</code>.
   * @param lease a lease, in millis.
   * @param type the <code>Class</code> as which the object should 
   * be put into this instance.
   */
  public void putAs(Object entry, long lease, Class type);
  
  public Space request(Request req, long timeout, Class type);
  
  public void reply(Reply rep, long lease);

}
