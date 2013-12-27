/*
 * XMLIncludeReader.java
 *
 * Created on April 6, 2005, 11:26 AM
 */

package org.sapia.soto.state.xml;

import java.io.IOException;

import org.sapia.soto.state.Result;
import org.sapia.soto.state.StatePath;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author yduchesne
 */
class XMLIncludeReader extends BaseXMLReader{

  private Result _result;
  private StatePath _src;
  
  XMLIncludeReader(StatePath src, Result result) {
    _result = result;
    _src = src;    
  }
  
  /**
   * @param input an <code>XMLIncludeInputSource</code> instance.
   */
  public void parse(InputSource input) throws IOException, SAXException{
    XMLContext ctx = (XMLContext)_result.getContext();
    ContentHandler current = ctx.getContentHandler();    
    try{
      ctx.setContentHandler(super.getContentHandler());
      ((XMLIncludeInputSource)input).parse(_src, _result);
    }catch(ClassCastException e){
      throw new SAXException("Expected an instance of " 
        + InputSource.class.getName() + "; got "
        + input.getClass().getName());
    }finally{
      ctx.setContentHandler(current);
    }
    
  }
  
}
