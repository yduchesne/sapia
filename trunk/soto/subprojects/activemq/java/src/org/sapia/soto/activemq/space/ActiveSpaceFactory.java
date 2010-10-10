package org.sapia.soto.activemq.space;

import org.codehaus.activespace.Space;
import org.codehaus.activespace.SpaceException;
import org.codehaus.activespace.SpaceFactory;

/**
 * This interface extends the {@link org.codehaus.activespace.SpaceFactory} interface
 * by adding the {@link #createSpace(String, int, Object)} method, which internally creates
 * a SQL92 selector string based on a "template" object (in a manner similar to JavaSpace).
 * <p>
 * In addition, {@link org.codehaus.activespace.Space} instances created by this factory are in
 * fact  {@link org.sapia.soto.activemq.space.ActiveSpace} instances.
 * 
 * @author yduchesne
 *
 */
public interface ActiveSpaceFactory extends SpaceFactory{
  
  /**
   * This method creates a {@link Space} instance given a "template" object instead of a SQL92
   * selector. This method will internally generate a SQL92 selector string from the template object.
   * The selector generator is done based on the public fields of the template object: the object is
   * introspected, and a SQL predicate is created for each primitive, not null and public field, where
   * the predicate name will be the field name, and the value will be the field value. In addition,
   * a predicate will be created based on the class of the template object. All predicates are then 
   * concatenated to create a SQL selector.
   * <p>
   * This allows using this method in a way that is similar to JavaSpace's entry processing mechanism.
   * <p>
   * See the {@link ActiveSpace} interface for details on polymorphic matching.
   *  
   * @param destination a name of the destination to which the returned space corresponds.
   * @param deliveryMode a delivery mode.
   * @param template a template object.
   * @return a <code>Space</code>.
   * @throws SpaceException
   * 
   * @see SpaceFactory#DISPATCH_ALL_CONSUMERS
   * @see SpaceFactory#DISPATCH_ONE_CONSUMER
   * @see SpaceFactory#DISPATCH_ONE_CONSUMER_EXCLUSIVE 
   */
  public Space createSpace(String destination, int deliveryMode, Object template) throws SpaceException;  

}
