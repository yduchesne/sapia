package org.sapia.soto.jgroups;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jgroups.Channel;
import org.jgroups.ChannelException;
import org.jgroups.conf.PlainConfigurator;
import org.jgroups.conf.ProtocolStackConfigurator;
import org.sapia.soto.ConfigurationException;
import org.sapia.soto.util.Namespace;
import org.sapia.util.xml.confix.ObjectHandlerIF;

/**
 * Implementation of the channel factory service.
 *
 * @author Jean-Cedric Desrochers
 */
public class ChannelFactoryServiceImpl implements ChannelFactoryService, ObjectHandlerIF {

  /** The jgroups channel configurators of this service. */
  private Map _jgroupConfigurators;
  
  /**
   * Creates a new ChannelFactoryServiceImpl instance.
   */
  public ChannelFactoryServiceImpl() {
    _jgroupConfigurators = new HashMap();
  }
  
  public void addProtocolStack(ProtocolStackDef aDef) {
    ProtocolStackConfigurator configurator = new PlainConfigurator(aDef.getProtocolStackString());
    _jgroupConfigurators.put(aDef.getType(), configurator);
  }
  
  public void addProtocolStacks(Collection someDefs) {
    for (Iterator it = someDefs.iterator(); it.hasNext(); ) {
      Object object = it.next();
      if (object instanceof ProtocolStackDef) {
        ProtocolStackDef def = (ProtocolStackDef) object;
        ProtocolStackConfigurator configurator = new PlainConfigurator(def.getProtocolStackString());
        _jgroupConfigurators.put(def.getType(), configurator);
      } else {
        throw new IllegalArgumentException("The collection passed in contains invalid object: expected a ProtocolStackDef bu was " + object);
      }
    }
  }
  
  /* (non-Javadoc)
   * @see org.sapia.util.xml.confix.ObjectHandlerIF#handleObject(java.lang.String, java.lang.Object)
   */
  public void handleObject(String elementName, Object obj) {
    if(obj instanceof ProtocolStackDef) {
      addProtocolStack((ProtocolStackDef) obj);
    }
  }
  
  /* (non-Javadoc)
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {
    if (_jgroupConfigurators.isEmpty()) {
      throw new ConfigurationException("This channel factory service contains no jgroups configurator");
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.Service#start()
   */
  public void start() throws Exception {
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.Service#dispose()
   */
  public void dispose() {
    _jgroupConfigurators.clear();
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.jgroups.ChannelFactoryService#createChannel(java.lang.String)
   */
  public Channel createChannel(String aType) throws ChannelException {
    ProtocolStackConfigurator configurator = (ProtocolStackConfigurator) _jgroupConfigurators.get(aType);
    
    if (configurator == null) {
      throw new ChannelException("Could not create a new channel, the type " + aType + " is not configured in this factory");
    } else {
      return new SotoChannel(configurator);
    }
  }

}
