package org.sapia.util.xml.idefix;


// Import of Sun's JDK classes
// ---------------------------
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 *
 *
 * @author Jean-Cedric Desrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class CompleteEncoder implements XmlEncoderIF {
  /////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////  CONSTRUCTORS  /////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Creates a new CompleteEncoder instance.
   */
  public CompleteEncoder() {
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////  INTERACE IMPLEMENTATION  ///////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Encodes the object passed in as a leaf.
   *
   * @param aName The name of the leaf to encode.
   * @param aValue The value of the leaf to encode.
   * @param anXmlBuffer The xml buffer to use to encode the leaf.
   */
  public void encodeLeaf(String aName, Object aValue, XmlBuffer anXmlBuffer) {
  }

  /**
   * Encodes the object passed in as a node.
   *
   * @param anObject The object to encode.
   * @param anXmlBuffer The xml buffer to use to encode the node.
   * @param someObjects The list of visited object (to detect circular references).
   */
  public void encodeNode(Object anObject, XmlBuffer anXmlBuffer,
    List someObjects) {
  }

  /**
   * Encodes the object passed in as a java array.
   *
   * @param anArray The array of objects to encode.
   * @param anXmlBuffer The xml buffer to use to encode the leaf.
   * @param someObjects The list of visited object (to detect circular references).
   */
  public void encodeArray(Object anArray, XmlBuffer anXmlBuffer,
    List someObjects) {
  }

  /**
   * Encodes the object passed in as a collection.
   *
   * @param aCollection The collection of objects to encode.
   * @param anXmlBuffer The xml buffer to use to encode the object.
   * @param someObjects The list of visited object (to detect circular references).
   */
  public void encodeCollection(Collection aCollection, XmlBuffer anXmlBuffer,
    List someObjects) {
  }

  /**
   * Encodes the object passed in as an iterator.
   *
   * @param anIterator The iterator of objects to encode.
   * @param anXmlBuffer The xml buffer to use to encode the object.
   * @param someObjects The list of visited object (to detect circular references).
   */
  public void encodeIterator(Iterator anIterator, XmlBuffer anXmlBuffer,
    List someObjects) {
  }

  /**
   * Encodes the object passed in as a map.
   *
   * @param aMap The map of objects to encode.
   * @param anXmlBuffer The xml buffer to use to encode the object.
   * @param someObjects The list of visited object (to detect circular references).
   */
  public void encodeMap(Map aMap, XmlBuffer anXmlBuffer, List someObjects) {
  }
}
