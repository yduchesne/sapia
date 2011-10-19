package org.sapia.soto.me;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.midlet.MIDlet;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.sapia.soto.me.model.J2meServiceMetaData;
import org.sapia.soto.me.net.Resource;
import org.sapia.soto.me.net.SotoMeResourceResolver;
import org.sapia.soto.me.util.CompositeException;
import org.sapia.soto.me.util.Log;
import org.sapia.soto.me.util.NestedPropertyResolver;
import org.sapia.soto.me.util.PropertyResolver;
import org.sapia.soto.me.util.TimeSpan;
import org.sapia.soto.me.xml.J2meConfixProcessor;
import org.sapia.soto.me.xml.J2meProcessingException;
import org.sapia.soto.me.xml.SotoMeObjectFactory;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public class SotoMeContainer implements MIDletEnv, PropertyResolver {

  private static final int PROPERTY_SOURCE_SOTOME_DYNAMIC = 0;
  private static final int PROPERTY_SOURCE_SOTOME_STATIC = 1;
  private static final int PROPERTY_SOURCE_MIDLET = 2;
  private static final int PROPERTY_SOURCE_SYSTEM = 3;
  private static final String[] PROPERTY_SOURCES = new String[] {
    "SotoMe dynamically included properties", "SotoMe static container properties",
    "MIDlet application properties", "System properties"
  };
  
  private static final String MODULE_NAME = "Container";
  
  /** Defines the state of this SotoMe container. */
  private SotoMeLifeCycle _lifecycle;
  
  /** The parent MIDlet of this container (optionnal). */
  private MIDlet _parentMIDlet;
  
  /** The list of the services binded in this container. */
  private FastList _serviceRegistry;

  /** The map of service by their identifier. */
  private FastMap _servicesById;
  
  /** The registry ob objects of this container. */
  private FastMap _objectRegistry;
  
  /** The map of properties assigned to this container. */
  private FastMap _containerProperties;
  
  /** The list (used as a stack) of nexted include context. */
  private FastList _includeContextStack;
  
  /** The object factory of this container. */
  private SotoMeObjectFactory _objectFactory;
  
  /** The resource resolver of this container. */
  private SotoMeResourceResolver _resourceResolver;
  
  /**
   * Creates a new SotoMeContainer instance.
   */
  public SotoMeContainer() {
    _serviceRegistry = new FastList();
    _servicesById = new FastMap();
    _objectRegistry = new FastMap();
    _containerProperties = new FastMap();
    _includeContextStack = new FastList();
    _resourceResolver = new SotoMeResourceResolver();
    _objectFactory = new SotoMeObjectFactory(this);
    _lifecycle = new SotoMeLifeCycle();
    Log.initialize(this);
    Log.info(MODULE_NAME, "\n\n=====> SotoMe CREATED <=====");
  }
  
  /**
   * Creates a new SotoMeContainer instance with the argument passed in.
   * 
   * @param aParentMIDlet The parent MIDlet creating this container.
   */
  public SotoMeContainer(MIDlet aParentMIDlet) {
    this();
    _parentMIDlet = aParentMIDlet;
  }
  
  /**
   * Returns the current lifecycle object of this SotoMe container.
   *
   * @return The current lifecycle object of this SotoMe container.
   * @see SotoMeLifeCycle
   */
  public SotoMeLifeCycle getLifeCycle() {
    return _lifecycle;
  }
  
  /**
   * Loads the given resource name into this SotoMe container.
   * 
   * @param aResourceName The name of the resource to load.
   * @throws Exception If an error occurs loading the specified ressource.
   */
  public SotoMeContainer load(String aResourceName) throws Exception {
    return load(aResourceName, new FastMap());
  }
  
  /**
   * Loads the given resource name into this SotoMe container and adding
   * the passed in properties in the SotoMe container. 
   * 
   * @param aResourceName The name of the resource to load.
   * @param someProperties The properties to add to the SotoMe container.
   * @throws Exception If an error occurs loading the specified ressource.
   */
  public SotoMeContainer load(String aResourceName, FastMap someProperties) throws Exception {
    _lifecycle.assertState(SotoMeLifeCycle.STATE_CREATED, SotoMeLifeCycle.STATE_LOADED, "Could not load resource " + aResourceName);
    
    try {
      TimeSpan timeSpan = new TimeSpan().start();
      if (Log.isDebug()) {
        Log.debug(MODULE_NAME, "Loading container with resource " + aResourceName + "...");
      }

      // Including resource based on scheme presence or not
      include(_resourceResolver.resolveResource(aResourceName));

      timeSpan.stop();
      Log.info(MODULE_NAME, "Loaded container with resource " + aResourceName + " [completed in " + timeSpan.getDeltaMillis() + " ms]\n=====> SotoMe LOADED <=====");
      _lifecycle.loaded();
      return this;
      
    } catch (RuntimeException re) {
      String message = "System error loading resource in this SotoMe container";
      Log.fatal(MODULE_NAME, message, re);
      throw new CompositeException(message, re);
    }
  }
  
  /**
   * Starts this SotoMe container after initialization or after pause.
   *  
   * @throws Exception If an error occurs starting the container.
   */
  public SotoMeContainer start() throws Exception {
    _lifecycle.assertState(SotoMeLifeCycle.STATE_LOADED, SotoMeLifeCycle.STATE_PAUSED, "Could not start this SotoMe cotainer");
    
    try {
      TimeSpan timeSpan = new TimeSpan().start();
      _lifecycle.initialized();
      if (Log.isDebug()) {
        Log.debug(MODULE_NAME, "Starting container...");
      }

      // Starting each service that are initialized
      for (FastList.Node n = (FastList.Node) _serviceRegistry.head(), end = (FastList.Node) _serviceRegistry.tail(); (n = (FastList.Node) n.getNext()) != end; ) {
        J2meServiceMetaData metaData = (J2meServiceMetaData) n.getValue();
        if (metaData.getLifeCycle().isInitialized() || metaData.getLifeCycle().isPaused()) {
          if (Log.isDebug()) {
            Log.debug(MODULE_NAME, "Starting service named '" + metaData.getServiceName() + "'");
          }

          metaData.getService().start();
          metaData.getLifeCycle().started();
        }
      }

      _lifecycle.started();

      timeSpan.stop();
      Log.info(MODULE_NAME, "Started container [completed in " + timeSpan.getDeltaMillis() + " ms]\n=====> SotoMe STARTED <=====");
      return this;
      
    } catch (RuntimeException re) {
      String message = "System error starting in this SotoMe container";
      Log.fatal(MODULE_NAME, message, re);
      throw new CompositeException(message, re);
    }
  }

  /**
   * Pauses this SotoMe container.
   *  
   * @throws Exception If an error occurs starting the container.
   */
  public SotoMeContainer pause() throws Exception {
    _lifecycle.assertState(SotoMeLifeCycle.STATE_STARTED, "Could not pause this SotoMe cotainer");
    
    try {
      TimeSpan timeSpan = new TimeSpan().start();
      if (Log.isDebug()) {
        Log.debug(MODULE_NAME, "Pausing container...");
      }

      // Pausing each j2me service that are started
      for (FastList.Node n = (FastList.Node) _serviceRegistry.head(), end = (FastList.Node) _serviceRegistry.tail(); (n = (FastList.Node) n.getNext()) != end; ) {
        J2meServiceMetaData metaData = (J2meServiceMetaData) n.getValue();
        if (metaData.getLifeCycle().isStarted()) {
          if (Log.isDebug()) {
            Log.debug(MODULE_NAME, "Pausing j2me service named '" + metaData.getServiceName() + "'");
          }

          metaData.getService().pause();
          metaData.getLifeCycle().paused();
        }
      }
      
      timeSpan.stop();
      Log.info(MODULE_NAME, "Paused container [completed in " + timeSpan.getDeltaMillis() + " ms]\n=====> SotoMe PAUSED <=====");
      _lifecycle.paused();
      return this;
      
    } catch (RuntimeException re) {
      String message = "System error pausing in this SotoMe container";
      Log.fatal(MODULE_NAME, message, re);
      throw new CompositeException(message, re);
    }
  }

  /**
   * Dispose this SotoMe container.
   *  
   * @throws Exception If an error occurs starting the container.
   */
  public SotoMeContainer dispose() throws Exception {
    if (!_lifecycle.isDisposed()) {
      try {
        TimeSpan timeSpan = new TimeSpan().start();
        if (Log.isDebug()) {
          Log.debug(MODULE_NAME, "Disposing container...");
        }

        // Disposing each service that is at least initialized
        for (FastList.Node n = (FastList.Node) _serviceRegistry.head(), end = (FastList.Node) _serviceRegistry.tail(); (n = (FastList.Node) n.getNext()) != end; ) {
          J2meServiceMetaData metaData = (J2meServiceMetaData) n.getValue();
          if (!metaData.getLifeCycle().isCreated() && !metaData.getLifeCycle().isDisposed()) {
            if (Log.isDebug()) {
              Log.debug(MODULE_NAME, "Disposing service named '" + metaData.getServiceName() + "'");
            }

            metaData.getService().dispose();
            metaData.getLifeCycle().dispose();
          }
        }
        
        timeSpan.stop();
        Log.info(MODULE_NAME, "Disposed container [completed in " + timeSpan.getDeltaMillis() + " ms]\n=====> SotoMe DISPOSED <=====");
        _lifecycle.dispose();
        
      } catch (RuntimeException re) {
        String message = "System error disposing in this SotoMe container";
        Log.fatal(MODULE_NAME, message, re);
        throw new CompositeException(message, re);
      }
    }
    
    return this;
  }
  
  /* (non-Javadoc)
   * @see org.sapia.soto.me.MIDletEnv#include(org.sapia.soto.me.net.Resource)
   */
  public Object include(Resource aResource) throws IOException, J2meProcessingException {
    return include(aResource, new FastMap());
  }
  
  /* (non-Javadoc)
   * @see org.sapia.soto.me.MIDletEnv#include(org.sapia.soto.me.net.Resource, javolution.util.FastMap)
   */
  public Object include(Resource aResource, FastMap someProperties) throws IOException, J2meProcessingException {
    IncludeContext context;
    if (_includeContextStack.isEmpty()) {
      context = new IncludeContext(aResource, someProperties);
    } else {
      context = new IncludeContext(aResource, (IncludeContext) _includeContextStack.getLast(), someProperties);
    }
    _includeContextStack.addLast(context);
    
    try {
      InputStream input = aResource.getInputStream();
      if (input == null) {
        // no resource found... try relative to parent context (if possible)
        if (_includeContextStack.size() > 1) {
          IncludeContext parentContext = (IncludeContext) _includeContextStack.get(_includeContextStack.size()-2);
          Resource absoluteResource = aResource.toAbsoluteFrom(parentContext.getResource());
          
          input = absoluteResource.getInputStream();
          if (input != null) {
            context.setResource(absoluteResource);
          } else {
            throw new J2meProcessingException("Could not open input stream for resource " + aResource);
          }
        } else {
          throw new J2meProcessingException("Could not open input stream for resource " + aResource);
        }
        
      }
      
      J2meConfixProcessor processor = new J2meConfixProcessor(_objectFactory, this);
      return processor.process(input);
      
    } finally {
      _includeContextStack.removeLast();
    }
  }
  
  /* (non-Javadoc)
   * @see org.sapia.soto.me.MIDletEnv#getProperty(java.lang.String)
   * @see org.sapia.soto.me.util.PropertyResolver#getProperty(java.lang.String)
   */
  public String getProperty(String aName) {
    String value = null;
    
    // Get value from dynamically loaded properties
    int source = PROPERTY_SOURCE_SOTOME_DYNAMIC;
    if (!_includeContextStack.isEmpty()) {
      value = ((PropertyResolver) _includeContextStack.getLast()).getProperty(aName);
    }

    // Get value from static container properties
    if (value == null) {
      source = PROPERTY_SOURCE_SOTOME_STATIC;
      value = (String) _containerProperties.get(aName);
    }
    
    // If no value found, get value from MIDlet (if present)
    if (value == null && _parentMIDlet != null) {
      source = PROPERTY_SOURCE_MIDLET;
      value = _parentMIDlet.getAppProperty(aName);
    }
    
    // If no value, get value from system properties
    if (value == null) {
      source = PROPERTY_SOURCE_SYSTEM;
      value = System.getProperty(aName);
    }
    
    if (Log.isDebug()) {
      if (value != null) {
        Log.debug(MODULE_NAME, "Resolved property name '" + aName + "' to value '" + value + "' from source: " + PROPERTY_SOURCES[source]);
      } else {
        Log.debug(MODULE_NAME, "Could not resolved property name '" + aName + "' from any of the sources");
      }
    }
    
    return value;
  }
  
  /* (non-Javadoc)
   * @see org.sapia.soto.me.MIDletEnv#setProperty(java.lang.String, java.lang.String)
   */
  public void setProperty(String aName, String aValue) {
    _containerProperties.put(aName, aValue);
  }

  /**
   * Binds the passed in service meta data into the container.
   * 
   * @param aMetaData The meta data of the service to bind.
   * @exception ConfigurationException If an error occurs while initializing the service.
   */
  public void addServiceMetaData(J2meServiceMetaData aMetaData) throws ConfigurationException {
    // Validate arguments
    if (aMetaData.getId() != null && _servicesById.containsKey(aMetaData.getId())) {
      throw new ConfigurationException("A service already exists for ID: " + aMetaData.getId());
    }
    
    // Initialize the service
    if (aMetaData.getLifeCycle().isCreated()) {
      try {
        if (Log.isDebug()) {
          Log.debug(MODULE_NAME, "Initializing service of type " + aMetaData.getServiceName() + " with id '" + aMetaData.getId() + "'");
        }

        aMetaData.getService().init();
        aMetaData.getLifeCycle().initialized();
        
      } catch (Exception e) {
        throw new ConfigurationException("Unable to initialize the service " + aMetaData.getServiceName(), e);
      }
    }
    
    
    // Add into internal datastructures
    _serviceRegistry.add(aMetaData);
    if (aMetaData.getId() != null) {
      _servicesById.put(aMetaData.getId(), aMetaData);      
    }
  }
  
  /**
   * Returns the list of J2me service meta data.
   * 
   * @return The list of {@link J2meServiceMetaData} objects.
   */
  public FastList getJ2meMetaDatas() {
    return _serviceRegistry;
  }
  
  /* (non-Javadoc)
   * @see org.sapia.soto.me.MIDletEnv#lookupService(java.lang.String)
   */
  public J2meService lookupService(String anObjectId) throws NotFoundException {
    Object object = _servicesById.get(anObjectId);
    
    if (object == null || !(object instanceof J2meServiceMetaData)) {
      String message = "Could not perform lookup - no service found for id '" + anObjectId + "'";
      Log.error(MODULE_NAME, message);
      throw new NotFoundException(message);
    } else {
      J2meService service = ((J2meServiceMetaData) object).getService();
      if (Log.isDebug()) {
        Log.debug(MODULE_NAME, "Resolved id '" + anObjectId + "' to service: " + service.toString());
      }
      
      return service;
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.MIDletEnv#bindObject(java.lang.String, java.lang.Object)
   */
  public void bindObject(String anId, Object anObject) {
    if (Log.isDebug()) {
      Log.debug(MODULE_NAME, "Binding the id '" + anId + "' to the object: " + anObject.toString());
    }
    _objectRegistry.put(anId, anObject);
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.MIDletEnv#lookupObject(java.lang.String)
   */
  public Object lookupObject(String anObjectId) throws NotFoundException {
    Object object = _objectRegistry.get(anObjectId);
    
    if (object == null) {
      String message = "Could not perform lookup - no object found for id '" + anObjectId + "'";
      Log.error(MODULE_NAME, message);
      throw new NotFoundException(message);
    } else {
      if (Log.isDebug()) {
        Log.debug(MODULE_NAME, "Resolved id '" + anObjectId + "' to object: " + object.toString());
      }
      return object;
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.MIDletEnv#resolveResource(java.lang.String)
   */
  public Resource resolveResource(String aResourceUri) throws IOException {
    return _resourceResolver.resolveResource(aResourceUri);
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.MIDletEnv#getMetaDataFor(org.sapia.soto.Service)
   */
  public J2meServiceMetaData getMetaDataFor(J2meService aService) throws NotFoundException {
    boolean isFound = false;
    J2meServiceMetaData metaData = null;
    for (FastList.Node n = (FastList.Node) _serviceRegistry.head(), end = (FastList.Node) _serviceRegistry.tail(); !isFound && (n = (FastList.Node) n.getNext()) != end; ) {
      metaData = (J2meServiceMetaData) n.getValue();
      isFound = metaData.getService() == aService;
    }
    
    if (metaData == null) {
      throw new NotFoundException("No meta data found for service: " + aService);
    } else {
      return metaData;
    }
  }
  
  /**
   * 
   *
   */
  public static class IncludeContext implements PropertyResolver {
    private Resource _resource;
    private NestedPropertyResolver _propertyResolver;
    
    public IncludeContext(Resource aResource, FastMap someProperties) {
      _resource = aResource;
      _propertyResolver = new NestedPropertyResolver(someProperties);
    }
    
    public IncludeContext(Resource aResource, IncludeContext aParent, FastMap someProperties) {
      _resource = aResource;
      _propertyResolver = new NestedPropertyResolver(someProperties, aParent);
    }
    
    public Resource getResource() {
      return _resource;
    }
    
    public void setResource(Resource aResource) {
      _resource = aResource;
    }

    /* (non-Javadoc)
     * @see org.sapia.soto.me.util.PropertyResolver#getProperty(java.lang.String)
     */
    public String getProperty(String aName) {
      return _propertyResolver.getProperty(aName);
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.MIDletEnv#requestShutdown()
   */
  public void requestShutdown() {
    try {
      dispose();
    } catch (Exception e) {
      String message = "Error shutting down the SotoMe container";
      Log.error(message, e);
    } finally {
      if (_parentMIDlet != null) {
        _parentMIDlet.notifyDestroyed();
      }
    }
  }
}
