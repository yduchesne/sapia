package org.sapia.util.xml.confix;


// Import of dom4j classes
// ----------------------
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;


import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

// Import of Sapia's utility classes
// ---------------------------------
import org.sapia.util.xml.ProcessingException;
import org.xml.sax.InputSource;

// Import of Sun's JDK classes
// ---------------------------
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.util.Iterator;
import java.util.List;

/**
 * This class creates an object graph for a given XML input stream. It internally
 * uses Dom4J to transform the stream into a document object model, and then to create
 * objects from the elements found in the document.
 * <p>
 * Usage:
 * <p>
 * <pre>
 * ObjectFactoryIF fac = new ReflectionFactory("com.newtrade.company");
 * Dom4jProcessor proc = new Dom4jProcessor(fac);
 * Company comp = (Company)proc.process(new FileInputStream("d:/dev/company.xml"));
 * </pre>
 *
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class Dom4jProcessor extends AbstractXMLProcessor {
  /////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////  CONSTRUCTORS  /////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  
  /**
   * Creates a new JDOMProcessor instance with the argument passed in.
   *
   * @param anObjectFactory The object factory of this processor.
   */
  public Dom4jProcessor(ObjectFactoryIF anObjectFactory) {
    super(anObjectFactory);
  }
  
  /////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////  OVERRIDEN METHODS  //////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  
  /**
   * This method takes an XML stream as input and returns an object
   * representation of the passed-in XML.
   *
   * @param is an XML stream
   * @return an object representation of the XML stream.
   * @exception ProcessingException
   */
  public Object process(InputStream is) throws ProcessingException {
    try {
      // Build the document from the input stream
      SAXReader  builder = new SAXReader();
      builder.setStripWhitespaceText(false);
      builder.setMergeAdjacentText(false);
      Document   doc = builder.read(is);
      
      // Process the document
      Object aResult = process(null, doc.getRootElement());
      
      return aResult;
    } catch (DocumentException de) {
      String aMessage = "Error parsing the XML of the input stream.";
      throw new ProcessingException(aMessage, de);
    } finally {
      try {
        if (is != null) {
          is.close();
        }
      } catch (IOException ioe) {
        String aMessage = "Error closing the input stream to process.";
        
        throw new ProcessingException(aMessage, ioe);
      }
    }
  }
  
  /**
   * This method takes an object and assigns to it the object representation
   * of the passed XML stream.
   *
   * @param root an <code>Object</code> that is the root of the object graph to create
   * from the given XML.
   * @param is an XML <code>InputStream</code>.
   * @throws ProcessingException if an error occurs while processing the given XML stream.
   */
  public void process(Object root, InputStream is) throws ProcessingException {
    try {
      // Build the document from the input stream
      SAXReader reader = new SAXReader();
      Document   doc = reader.read(is);
      
      // Process the document
      Object aResult = process(root, doc.getRootElement());
    } catch (DocumentException de) {
      String aMessage = "Error parsing the XML of the input stream.";
      throw new ProcessingException(aMessage, de);
    } finally {
      try {
        if (is != null) {
          is.close();
        }
      } catch (IOException ioe) {
        String aMessage = "Error closing the input stream to process.";
        
        throw new ProcessingException(aMessage, ioe);
      }
    }
  }
  
  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////////  HELPER METHODS  ////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  
  /**
   * This method will process the dom element passed in to create an object.
   *
   * @param aParent The parent object of the one to create.
   * @param anElement The dom element to process.
   * @exception ProcessingException If an error occurs while processing the dom tree.
   */
  public Object process(Object aParent, Element anElement)
  throws ProcessingException {
    return process(aParent, anElement, null);
  }
  
  private Object process(Object aParent, Element anElement, String setterName)
  throws ProcessingException {
    String aName = anElement.getName();
    
    if (setterName == null) {
      setterName = aName;
    }
    
    CreationStatus status = null;
    
    try {
      status = getObjectFactory().newObjectFor(anElement.getNamespace()
      .getPrefix(),
        anElement.getNamespace().getURI(), aName, aParent);
    } catch (ObjectCreationException oce) {
      if (aParent == null) {
        String aMessage = "Unable to create an object for the element " +
          anElement;
        
        throw new ProcessingException(aMessage, oce);
      }
      List children;
      if ((aParent != null) &&
        (containsMethod("set", aParent, aName) ||
        containsMethod("add", aParent, aName)) &&
        ((children = anElement.elements()).size() == 1)) {
        Element child = (Element) children.get(0);
        process(aParent, child, setterName);
        
        return aParent;
      }
      
      try {
        String aValue = anElement.getTextTrim();
        
        invokeSetter(aParent.getClass().getName(), aParent, aName, aValue);
        
        return aParent;
      } catch (ConfigurationException ce) {
        String aMessage =
          "Unable to create an object nor to call a setter for the element " +
          anElement;
        oce.printStackTrace();
        throw new ProcessingException(aMessage, ce);
      }
    }
    
    String text = anElement.getTextTrim();
    
    if (text.length() > 0) {
      try {
        invokeSetter(aName, status.getCreated(), "Text", text);
      } catch (ConfigurationException ce) {
        String aMessage = "The object '" + aName +
          "' does not accept free text";
        
        throw new ProcessingException(aMessage, ce);
      }
    }
    
    try {
      // Process the attributes of the DOM element
      for (Iterator it = anElement.attributeIterator(); it.hasNext();) {
        Attribute attr = (Attribute) it.next();
        
        invokeSetter(aName, status.getCreated(), attr.getName(), attr.getValue());
      }
      
      // Process the child elements
      for (Iterator it = anElement.elementIterator(); it.hasNext();) {
        Element child = (Element) it.next();
        
        if (status.getCreated() instanceof Dom4jHandlerIF) {
          ((Dom4jHandlerIF) status.getCreated()).handleElement(child);
        } else if(status.getCreated() instanceof XMLConsumer) {
          XMLConsumer cons = (XMLConsumer)status.getCreated();
          ByteArrayOutputStream bos = new ByteArrayOutputStream();
          XMLWriter writer;
          try{
            writer = new XMLWriter(bos, OutputFormat.createPrettyPrint());
          }catch(UnsupportedEncodingException e){
            throw new ProcessingException("Could not instantiate XMLWriter", e);
          }
          
          try{
            Element copy = child.createCopy();
            copy.setDocument(null);
            writer.write(DocumentHelper.createDocument(copy));
            ByteArrayInputStream in = new ByteArrayInputStream(bos.toByteArray());
            InputSource is = new InputSource(in);
            cons.consume(is);
          }catch(Exception e){
            throw new ProcessingException("Could not pipe content of element: " + child.getQualifiedName() + " to XMLConsumer", e);
          }
        } else {
          process(status.getCreated(), child);
        }
      }
      
      // before assigning to parent, check if object
      // implements ObjectCreationCallback.
      if (status.getCreated() instanceof ObjectCreationCallback) {
        status._created = ((ObjectCreationCallback) status.getCreated()).onCreate();
      }
      
      // assign obj to parent through setXXX or addXXX
      if ((aParent != null) && !status.wasAssigned() &&
        !(status.getCreated() instanceof NullObject)) {
        assignToParent(aParent, status.getCreated(), setterName);
      }
      
      if (status.getCreated() instanceof NullObject) {
        return null;
      }
      
      return status.getCreated();
    } catch (ConfigurationException ce) {
      String aMessage = "Unable to process the content of the element " +
        aName;
      
      throw new ProcessingException(aMessage, ce);
    }
  }
}
