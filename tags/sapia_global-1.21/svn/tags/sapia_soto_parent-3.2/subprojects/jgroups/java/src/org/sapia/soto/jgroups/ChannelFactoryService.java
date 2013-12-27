package org.sapia.soto.jgroups;

import org.jgroups.Channel;
import org.jgroups.ChannelException;
import org.sapia.soto.Service;


/**
 * A Soto service that wraps a jgroups channel factory. 
 *
 * @author Jean-Cedric Desrochers
 */
public interface ChannelFactoryService extends Service {

  /**
   * Creates a new jgroups Channel for the type passed in. The type argument
   * refers to a configuration set of java groups protocol stack already define
   * in this service.
   * 
   * @param aType The type of channel to create.
   * @return The created channel instance. 
   * @throws ChannelException If an error occurs creating the channel.
   */
   public Channel createChannel(String aType) throws ChannelException;

}
