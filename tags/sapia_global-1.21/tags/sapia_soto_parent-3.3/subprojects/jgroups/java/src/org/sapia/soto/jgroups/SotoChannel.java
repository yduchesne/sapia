package org.sapia.soto.jgroups;

import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.conf.ProtocolStackConfigurator;

/**
 * 
 *
 * @author Jean-Cedric Desrochers
 */
public class SotoChannel extends JChannel {

  /**
   * Creates a new SotoChannel instance.
   * 
   * @param aConfigurator The protocol stack configurator.
   * @throws ChannelException If an error occurs.
   */
  protected SotoChannel(ProtocolStackConfigurator aConfigurator) throws ChannelException {
    super(aConfigurator);
  }
}
