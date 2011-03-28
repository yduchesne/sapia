package org.sapia.soto.jgroups;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.StringTokenizer;

import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 * 
 *
 * @author Jean-Cedric Desrochers
 */
public class ProtocolStackDef implements ObjectCreationCallback {
  
  /** The type of this protocol stack definition. */
  private String _type;
  
  /** The string of the protocol stack definition. */
  private String _protocolStackString;

  public String getType() {
    return _type;
  }
  
  public void setType(String aType) {
    _type = aType;
  }
  
  public String getProtocolStackString() {
    return _protocolStackString;
  }
  
  public void setProtocolStackString(String aString) throws Exception {
    if (aString == null) {
      throw new ConfigurationException("The protocol stack string is null");
    }
    
    _protocolStackString = trimLines(aString);
  }
  
  public void setText(String aContent) throws Exception {
    setProtocolStackString(aContent);
  }
  
  /**
   * Internal utility that parses the string received and trim the empty spaces of each line.
   * 
   * @param aContent The text to trim.
   * @return The string that results of the trim operation. 
   * @throws IOException If an error occurs reading the content.
   */
  protected String trimLines(String aContent) throws IOException {
    LineNumberReader reader = new LineNumberReader(new StringReader(aContent));
    boolean isCompleted = false;
    StringBuffer buffer = new StringBuffer();
    
    while (!isCompleted) {
      String line = reader.readLine();
      if (line != null) {
        for (StringTokenizer tokenizer = new StringTokenizer(line, " "); tokenizer.hasMoreTokens(); ) {
          buffer.append(tokenizer.nextToken().trim());
        }
      } else {
        isCompleted = true;
      }
    }

    return buffer.toString();
  }
  
  /* (non-Javadoc)
   * @see org.sapia.util.xml.confix.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    if (_type == null) {
      throw new ConfigurationException("The type of the protocol stack def is null");
    } else if (_protocolStackString == null) {
      throw new ConfigurationException("The protocol string of the protocol stack def is null");
    }
    
    return this;
  }
}
