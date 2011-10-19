/**
 * 
 */
package org.sapia.soto.me.xml;

import javolution.text.TextBuilder;
import javolution.util.FastList;
import javolution.util.FastMap;

import org.sapia.soto.me.MIDletEnvAware;
import org.sapia.soto.me.SotoMeContainer;
import org.sapia.soto.me.model.Bind;
import org.sapia.soto.me.model.ChooseCondition;
import org.sapia.soto.me.model.ClassDefinition;
import org.sapia.soto.me.model.DebugMessage;
import org.sapia.soto.me.model.Definitions;
import org.sapia.soto.me.model.IfCondition;
import org.sapia.soto.me.model.IncludeResource;
import org.sapia.soto.me.model.J2meApplication;
import org.sapia.soto.me.model.J2meServiceMetaData;
import org.sapia.soto.me.model.J2meServiceRef;
import org.sapia.soto.me.model.J2meServiceSelector;
import org.sapia.soto.me.model.Lookup;
import org.sapia.soto.me.model.Namespace;
import org.sapia.soto.me.model.Parameter;
import org.sapia.soto.me.model.SwitchCondition;
import org.sapia.soto.me.model.UnlessCondition;
import org.sapia.soto.me.net.UriResource;
import org.sapia.soto.me.util.Log;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public class SotoMeObjectFactory implements ObjectFactory {

  public static final String PREFIX_SOTO = "soto";
  
  public static final String TAG_APPLICATION = "app";
  public static final String TAG_NAMESPACE   = "namespace";
  public static final String TAG_DEFS        = "defs";
  public static final String TAG_SERVICE     = "service";
  public static final String TAG_SERVICE_REF = "serviceRef";
  public static final String TAG_DEBUG       = "debug";
  public static final String TAG_PARAM       = "param";
  public static final String TAG_SELECT      = "serviceSelect";
  public static final String TAG_BIND        = "bind";
  public static final String TAG_LOOKUP      = "lookup";
  public static final String TAG_REF         = "ref";
  public static final String TAG_IF          = "if";
  public static final String TAG_UNLESS      = "unless";
  public static final String TAG_CHOOSE      = "choose";
  public static final String TAG_SWITCH      = "switch";
  public static final String TAG_INCLUDE     = "include";
  
  private static final String MODULE_NAME = "ObjectFactory";

  /** The list of namespaces registered with this object factory. */
  private FastMap _namespaces;
  
  /** The list of prefix for which a resource was loaded. */
  private FastList _loadedPrefixResources;
  
  /** The SotoMe container of this object factory. */
  private SotoMeContainer _container;
  
  /**
   * Creates a new SotoMeObjectFactory instance.
   */
  public SotoMeObjectFactory(SotoMeContainer aContainer) {
    _namespaces = new FastMap();
    _loadedPrefixResources = new FastList();
    _container = aContainer;
  }
  
  /**
   * Adds the namespace passed in to this object factory;
   * 
   * @param aNamespace The namespace to add.
   */
  public void addNamespace(Namespace aNamespace) {
    if (aNamespace == null) {
      throw new IllegalArgumentException("The passed in namespace is null");
    }
    
    Namespace currentNamespace = (Namespace) _namespaces.get(aNamespace.getPrefix());
    if (currentNamespace == null) {
      _namespaces.put(aNamespace.getPrefix(), aNamespace);
      if (Log.isDebug()) {
        Log.debug(MODULE_NAME, "Added a new class definitions from namespace " + aNamespace.getPrefix() + "\n\t" + aNamespace.getClassDefinitions());
      }
    } else {
      FastList newClassDefs = aNamespace.getClassDefinitions();
      for (FastList.Node n = (FastList.Node) newClassDefs.head(); (n = (FastList.Node) n.getNext()) != newClassDefs.tail(); ) {
        ClassDefinition newClassDef = (ClassDefinition) n.getValue();
        ClassDefinition currentClassDef = currentNamespace.getClassDefinition(newClassDef.getName());
        if (currentClassDef == null) {
          currentNamespace.addClassDefinition(newClassDef);
          if (Log.isDebug()) {
            Log.debug(MODULE_NAME, "Added a new class definition to namespace '" + currentNamespace.getPrefix() + "' " + newClassDef);
          }
        } else if (!newClassDef.getClassName().equals(currentClassDef.getClassName())) {
          throw new IllegalArgumentException("Could not add namespace for prefix '" + aNamespace.getPrefix() +
                  "' - the class definition name '" + newClassDef.getName() + "' already exists but for another class (current:" +
                  currentClassDef.getClassName() + " new:" + newClassDef.getClassName() + ")");
        }
      }
    }
    
  }
  
  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.ObjectFactory#newObjectFor(java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
   */
  public Object newObjectFor(String aPrefix, String aNamespaceURI, String anElementName, Object aParent) throws ObjectCreationException {
    Object newObject = null;
    if (PREFIX_SOTO.equals(aPrefix)) {
      newObject = createSotoObjectFor(aPrefix, aNamespaceURI, anElementName, aParent);
      
    } else if (aPrefix != null) {
      loadResourceDefinitionsForNamespacePrefix(aPrefix, aNamespaceURI);
      newObject = createObjectFromNamespace(aPrefix, aNamespaceURI, anElementName, aParent);
    }
    
    if (newObject != null) {
      if (newObject instanceof MIDletEnvAware) {
        ((MIDletEnvAware) newObject).setMIDletEnv(_container);
      }
      return newObject;
    } else {
      throw new ObjectCreationException("Could not create a object for the element '" + aPrefix + ":" + anElementName + "'");
    }
  }
  
  /**
   * Internal utility method that create objects specific to the SotoMe container.
   * 
   * @param aPrefix The namespace prefix of the element.
   * @param aNamespaceURI The namespace URI of the element.
   * @param anElementName The element name for wich to create an object.
   * @param aParent The parent object of the object to create.
   * @return The created object.
   * @exception ObjectCreationException If an error occurs creating the object.
   */
  protected Object createSotoObjectFor(String aPrefix, String aNamespaceURI, String anElementName, Object aParent) throws ObjectCreationException {
    if (TAG_APPLICATION.equals(anElementName)) {
      J2meApplication application = new J2meApplication(_container);
      return application;
      
    } else if (TAG_DEFS.equals(anElementName)) {
      Definitions defs = new Definitions();
      return defs;
      
    } else if (TAG_NAMESPACE.equals(anElementName)) {
      Namespace namespace = new Namespace();
      namespace.setSotoMeObjectFactory(this);
      return namespace;

    } else if (TAG_SERVICE.equals(anElementName)) {
      J2meServiceMetaData metaData = new J2meServiceMetaData();
      return metaData;

    } else if (TAG_SERVICE_REF.equals(anElementName)) {
      J2meServiceRef serviceRef = new J2meServiceRef(_container);
      return serviceRef;

    } else if (TAG_SELECT.equals(anElementName)) {
      J2meServiceSelector selector = new J2meServiceSelector(_container);
      return selector;

    } else if (TAG_DEBUG.equals(anElementName)) {
      DebugMessage debug = new DebugMessage();
      return debug;

    } else if (TAG_PARAM.equals(anElementName)) {
      Parameter param = new Parameter(_container);
      return param;

    } else if (TAG_BIND.equals(anElementName)) {
      Bind bind = new Bind();
      return bind;

    } else if (TAG_LOOKUP.equals(anElementName) || TAG_REF.equals(anElementName)) {
      Lookup lookup = new Lookup();
      return lookup;

    } else if (TAG_IF.equals(anElementName)) {
      IfCondition ifCondition = new IfCondition();
      return ifCondition;

    } else if (TAG_UNLESS.equals(anElementName)) {
      UnlessCondition unlessCondition = new UnlessCondition();
      return unlessCondition;

    } else if (TAG_CHOOSE.equals(anElementName)) {
      ChooseCondition chooseCondition = new ChooseCondition();
      return chooseCondition;

    } else if (TAG_SWITCH.equals(anElementName)) {
      SwitchCondition switchCondition = new SwitchCondition();
      return switchCondition;

    } else if (TAG_INCLUDE.equals(anElementName)) {
      IncludeResource include = new IncludeResource();
      return include;
      
    } else {
      throw new ObjectCreationException("Not recognized name for soto namespace - could not create a object for the element '" + aPrefix + ":" + anElementName + "'");
    }
  }

  /**
   * Internal utility method that create objects according to namespace definitions loaded.
   * 
   * @param aPrefix The namespace prefix of the element.
   * @param aNamespaceURI The namespace URI of the element.
   * @param anElementName The element name for wich to create an object.
   * @param aParent The parent object of the object to create.
   * @return The created object.
   * @exception ObjectCreationException If an error occurs creating the object.
   */
  protected Object createObjectFromNamespace(String aPrefix, String aNamespaceURI, String anElementName, Object aParent) throws ObjectCreationException {
    Namespace namespace = (Namespace) _namespaces.get(aPrefix);
    if (namespace == null) {
      throw new ObjectCreationException("Could not create a object for the element '" + aPrefix + ":" + anElementName + "'");
    }
    
    ClassDefinition classDef = namespace.getClassDefinition(anElementName);
    if (classDef == null) {
      throw new ObjectCreationException("Could not create a object for the element '" + aPrefix + ":" + anElementName + "'");
    }
    
    try {
      Class newClass = Class.forName(classDef.getClassName());
      Object newObject =  newClass.newInstance();
      
      return newObject;
      
    } catch (Exception e) {
      throw new ObjectCreationException("Could not create a object for the element '" + aPrefix + ":" + anElementName + "'", e);
    }
  }
  
  /**
   * Internal method that will attempt to parse the namespace URI of the prefix as a loadable resource.
   * If it succeed then the content of the resource will be loaded as a list of namespace definitions.
   *  
   * @param aPrefix The prefix of the resource to load.
   * @param aNamespaceURI The namespace URI associated with the prefix.
   */
  protected void loadResourceDefinitionsForNamespacePrefix(String aPrefix, String aNamespaceURI) {
    String qName = new TextBuilder().
            append(aPrefix).append(":").append(aNamespaceURI).toString();
    
    if (!_loadedPrefixResources.contains(qName)) {
      if (UriResource.isResourceUri(aNamespaceURI)) {
        try {
          Log.info(MODULE_NAME, "Loading definitions of the prefix '" + aPrefix + "' from the resource URI " + aNamespaceURI);
          UriResource defsResource = UriResource.parseString(aNamespaceURI);
          _container.include(defsResource);
        } catch (Exception e) {
          Log.warn(MODULE_NAME, "Unable to load definitions of the prefix '" + aPrefix + "' from the resource URI " + aNamespaceURI, e);
        } finally {
          _loadedPrefixResources.add(qName);
        }
      }
    }
  }
}
