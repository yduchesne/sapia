/*
 * XMLIncludeInputSource.java
 *
 * Created on April 6, 2005, 11:25 AM
 */

package org.sapia.soto.state.xml;

import org.sapia.soto.state.Result;
import org.sapia.soto.state.StateExecException;
import org.sapia.soto.state.StatePath;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * An XML <code>InputSource</code> that works in conjunction with an
 * <code>XMLIncludeReader</code>.
 *
 * @author yduchesne
 */
public class XMLIncludeInputSource extends InputSource{
  
  /** Creates a new instance of XMLIncludeInputSource */
  public XMLIncludeInputSource() {
  }
  
  public void parse(StatePath path, Result result) throws SAXException{
    try{
      result.exec(path);
    }catch(StateExecException e){
      throw new SAXException("Could not perform include", e);    
    }
  }
}
