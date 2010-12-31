package org.sapia.util.xml.parser;


/**
 * This interface defines a bunch of constant values used in manipulation
 * of XML documents. This dictionnary interface represents all the different
 * values defined by the XML speficication like the namespace uri, the default
 * xml namespace prefix, the reserved attributes like <CODE>xml:lang</CODE>,
 * <CODE>xml:space</CODE> and <CODE>xml:base</CODE> and their values.
 *
 * @author Jean-Cedric Desrochers
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface XMLDictionnaryIF {
  /** Defines the namspace URI of XML 1.0 */
  public static final String XML_NAMESPACE_URI = "http://www.w3.org/XML/1998/namespace";

  /** Defines the reserved namespace prefix for the XML specification. */
  public static final String XML_NAMESPACE_PREFIX = "xml:";

  /** Defines the lang element name. */
  public static final String LOCAL_ELEMENT_XML_LANG = "lang";

  /** Defines the xml:lang element name. */
  public static final String QUALIFIED_ELEMENT_XML_LANG = "xml:lang";

  /** Defines the space element name. */
  public static final String LOCAL_ELEMENT_XML_SPACE = "space";

  /** Defines the xml:space element name. */
  public static final String QUALIFIED_ELEMENT_XML_SPACE = "xml:space";

  /** Defines the base element name. */
  public static final String LOCAL_ELEMENT_XML_BASE = "xml:base";

  /** Defines the xml:base element name. */
  public static final String QUALIFIED_ELEMENT_XML_BASE = "xml:base";

  /** Defines the restricted value default for the xml:space element. */
  public static final String VALUE_DEFAULT = "default";

  /** Defines the restricted value preserved for the xml:space element. */
  public static final String VALUE_PRESERVED = "preserved";
}
