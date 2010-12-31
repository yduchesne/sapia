package org.sapia.domain.dublincore.parser;


// Import of Sapia's utility classes
// ---------------------------------
import org.sapia.util.xml.parser.XMLDictionnaryIF;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */
public interface DublinCoreDictionaryIF extends XMLDictionnaryIF {
  /** Defines the namespace URI of the Simple Dublin Core element set. */
  public static final String DUBLIN_CORE_NAMESPACE_URI = "http://purl.org/dc/elements/1.1/";

  /** Defines the Dublin Core contributor element name. */
  public static final String ELEMENT_CONTRIBUTOR = "contributor";

  /** Defines the Dublin Core coverage element name. */
  public static final String ELEMENT_COVERAGE = "coverage";

  /** Defines the Dublin Core creator element name. */
  public static final String ELEMENT_CREATOR = "creator";

  /** Defines the Dublin Core date element name. */
  public static final String ELEMENT_DATE = "date";

  /** Defines the Dublin Core description element name. */
  public static final String ELEMENT_DESCRIPTION = "description";

  /** Defines the Dublin Core format element name. */
  public static final String ELEMENT_FORMAT = "format";

  /** Defines the Dublin Core identifier element name. */
  public static final String ELEMENT_IDENTIFIER = "identifier";

  /** Defines the Dublin Core language element name. */
  public static final String ELEMENT_LANGUAGE = "language";

  /** Defines the Dublin Core publisher element name. */
  public static final String ELEMENT_PUBLISHER = "publisher";

  /** Defines the Dublin Core relation element name. */
  public static final String ELEMENT_RELATION = "relation";

  /** Defines the Dublin Core rights element name. */
  public static final String ELEMENT_RIGHTS = "rights";

  /** Defines the Dublin Core source element name. */
  public static final String ELEMENT_SOURCE = "source";

  /** Defines the Dublin Core subject element name. */
  public static final String ELEMENT_SUBJECT = "subject";

  /** Defines the Dublin Core title element name. */
  public static final String ELEMENT_TITLE = "title";

  /** Defines the Dublin Core type element name. */
  public static final String ELEMENT_TYPE = "type";
}
