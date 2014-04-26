package org.sapia.archie.impl;

import java.util.StringTokenizer;

import org.sapia.archie.Name;
import org.sapia.archie.NameParser;
import org.sapia.archie.NamePart;
import org.sapia.archie.ProcessingException;

/**
 * @author Yanick Duchesne
 */
public class AttributeNameParser implements NameParser{
  
  static final char   QMARK = '?';
  static final String AMP   = "&";
  static final char   EQ    = '=';
  static final String SLASH = "/";
  static final char   SEPARATOR = '/';
  
  /**
   * @see org.sapia.archie.NameParser#asString(org.sapia.archie.Name)
   */
  public String asString(Name name) {
    StringBuffer buff = new StringBuffer();
    for(int i = 0; i < name.count(); i++){
      buff.append(name.get(i).asString());
      if(i < name.count() - 1){
        buff.append(SEPARATOR);
      }
    }
    return buff.toString();
  }
  /**
   * @see org.sapia.archie.NameParser#parse(java.lang.String)
   */
  public Name parse(String name) throws ProcessingException {
    Name         n   = new Name();
    StringBuffer buf = new StringBuffer();

    for (int i = 0; i < name.length(); i++) {
      if (name.charAt(i) == SEPARATOR) {
        n.add(parseNamePart(buf.toString()));
        buf.delete(0, buf.length());
      } else {
        buf.append(name.charAt(i));
      }
    }

    if (buf.length() > 0) {
      n.add(parseNamePart(buf.toString()));
    }

    return n;
  }
  /**
   * @see org.sapia.archie.NameParser#parseNamePart(java.lang.String)
   */
  public NamePart parseNamePart(String namePart) throws ProcessingException {
    AttributeNamePart anp = new AttributeNamePart();
    parseName(anp, namePart);
    return anp;
  }
  private void parseName(AttributeNamePart anp, String name) {
    int idx = name.indexOf(QMARK);

    if (idx < 0) {
      anp.setName(name);

      return;
    }

    anp.setName(name.substring(0, idx));

    if (idx == (name.length() - 1)) {
      return;
    }

    parseProperties(anp, name.substring(idx + 1));
  }

  /* takes prop1=val1&prop2=val2&prop3=val3 */
  private void parseProperties(AttributeNamePart anp, String props) {
    StringTokenizer st    = new StringTokenizer(props, AMP);
    String          token;

    while (st.hasMoreTokens()) {
      token = st.nextToken();
      parseProperty(anp, token);
    }
  }

  /* takes prop1=val1 */
  private void parseProperty(AttributeNamePart anp, String prop) {
    String name  = null;
    String value = null;

    int    idx = prop.indexOf(EQ);

    if (idx < 0) {
      if (prop.length() > 0) {
        name = prop;
      }
    } else {
      name = prop.substring(0, idx);

      if (idx != (prop.length() - 1)) {
        value = prop.substring(idx + 1);
      }
    }

    if (name != null && value != null) {
      anp.addProperty(name, value);
    }
  }  
}
