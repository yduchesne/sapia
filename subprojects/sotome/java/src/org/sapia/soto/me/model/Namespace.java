package org.sapia.soto.me.model;

import javolution.text.TextBuilder;
import javolution.util.FastList;
import javolution.util.FastMap;

import org.sapia.soto.me.ConfigurationException;
import org.sapia.soto.me.xml.NullObject;
import org.sapia.soto.me.xml.ObjectCreationCallback;
import org.sapia.soto.me.xml.ObjectCreationException;
import org.sapia.soto.me.xml.ObjectFactory;
import org.sapia.soto.me.xml.SotoMeObjectFactory;

/**
 * This class implements the <code>soto:namespace</code> tag for the SotoMe container.
 *
 * @author Jean-Cédric Desrochers
 */
public class Namespace extends BaseModel implements ObjectFactory, ObjectCreationCallback {

  public static final String TAG_PREFIX = "prefix";
  public static final String TAG_DEF    = "def";

  /** The SotoMe object factory of this namespace. */
  private SotoMeObjectFactory _objectFactory;
  
  /** The prefix of this namespace. */
  private String _prefix;
  
  /** The map of class definitions associated to this namespace. */
  private FastMap _classDefinitions;
  
  /**
   * Creates a new Namespace instance.
   */
  public Namespace() {
    _classDefinitions = new FastMap();
  }

  /**
   * Changes the SotoMe object factory of this namespace.
   * 
   * @param aFactory The new object factory.
   */
  public void setSotoMeObjectFactory(SotoMeObjectFactory aFactory) {
    _objectFactory = aFactory;
  }
  
  /**
   * Returns the prefix of this namespace.
   * 
   * @return The prefix of this namespace.
   */
  public String getPrefix() {
    return _prefix;
  }
  
  /**
   * Changes the prefix of this namespace.
   * 
   * @param aPrefix The new prefix.
   */
  public void setPrefix(String aPrefix) {
    _prefix = aPrefix;
  }
  
  /**
   * Returns the list of class definitions.
   * 
   * @return The list of {@link ClassDefinition} objects.
   */
  public FastList getClassDefinitions() {
    return new FastList(_classDefinitions.values());
  }
  
  /**
   * Returns the class definition associated to the name passed in.
   * 
   * @param aName The name of the class definition to retrieve.
   * @return The {@link ClassDefinition} instance found or <code>null</code> if none is found.
   */
  public ClassDefinition getClassDefinition(String aName) {
    return (ClassDefinition) _classDefinitions.get(aName);
  }
  
  /**
   * Adds the class definition passed in to this namespace.
   * 
   * @param aDefinition The class definition to add.
   */
  public void addClassDefinition(ClassDefinition aDefinition) {
    if (aDefinition == null) {
      throw new IllegalArgumentException("The class definition passed in is null");
    }
    
    _classDefinitions.put(aDefinition.getName(), aDefinition);
  }
  
  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.ObjectHandler#handleObject(java.lang.String, java.lang.Object)
   */
  public void handleObject(String aName, Object anObject) throws ConfigurationException {
    if (TAG_PREFIX.equals(aName)) {
      assertObjectType(String.class, aName, anObject);
      setPrefix((String) anObject);

    } else if (TAG_DEF.equals(aName)) {
      assertObjectType(ClassDefinition.class, aName, anObject);
      addClassDefinition((ClassDefinition) anObject);

    } else {
      throwUnrecognizedObject(aName, anObject);
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.ObjectFactory#newObjectFor(java.lang.String, java.lang.String, java.lang.String, java.lang.Object)
   */
  public Object newObjectFor(String aPrefix, String aNamespaceURI, String anElementName, Object aParent) throws ObjectCreationException {
    if (TAG_DEF.equals(anElementName)) {
      return new ClassDefinition();
    } else {
      throw new ObjectCreationException("Could not create an object for the name '" + anElementName +
              "' on the current object " + getClass().getName());
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.me.xml.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    // if an object factory is assigned, registered with it
    if (_objectFactory != null) {
      _objectFactory.addNamespace(this);
      return NullObject.getInstance();
      
    // otherwise return this and let parent class handle registration with factory
    } else {
      return this;
    }
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString() {
    TextBuilder builder = new TextBuilder();
    builder.append(super.toString()).
            append("[prefix=").append(_prefix).
            append(" defs=").append(_classDefinitions).
            append("]");
    
    return builder.toString();
  }
}
