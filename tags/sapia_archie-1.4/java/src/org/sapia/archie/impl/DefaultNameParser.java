package org.sapia.archie.impl;

import org.sapia.archie.Name;
import org.sapia.archie.NameParser;
import org.sapia.archie.NamePart;
import org.sapia.archie.ProcessingException;


/**
 * Implements the {@link NameParser} interface. Parses names whose parts are
 * delimited by '/', such as in: some/object/name, where "some/object/name" is
 * a {@link Name}, and "some", "object" and "name" are {@link NamePart}s.
 * 
 * @see org.sapia.archie.impl.DefaultNamePart
 * 
 * @author Yanick Duchesne
 */
public class DefaultNameParser implements NameParser {
  public static final char SEPARATOR = '/';


  /**
   * @see NameParser#parse(String) 
   */
  public Name parse(String name) throws ProcessingException {
    Name         n   = new Name();
    StringBuffer buf = new StringBuffer();

    for (int i = 0; i < name.length(); i++) {
      if (name.charAt(i) == SEPARATOR) {
        n.add(new DefaultNamePart(buf.toString()));
        buf.delete(0, buf.length());
      } else {
        buf.append(name.charAt(i));
      }
    }

    if (buf.length() > 0) {
      n.add(new DefaultNamePart(buf.toString()));
    }

    return n;
  }
  
  /**
   * @see org.sapia.archie.NameParser#parseNamePart(java.lang.String)
   */
  public NamePart parseNamePart(String namePart) throws ProcessingException {
    return new DefaultNamePart(namePart);
  }
  
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
}
