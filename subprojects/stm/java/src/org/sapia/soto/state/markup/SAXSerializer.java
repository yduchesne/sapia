package org.sapia.soto.state.markup;

import java.io.PrintWriter;
import java.util.List;

import org.sapia.soto.Settings;
import org.sapia.soto.state.Context;
import org.sapia.soto.state.xml.XMLContext;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.AttributesImpl;

/**
 * An instance of this class serializes markup data to the passed in {@link org.sapia.soto.state.Context}.
 * The context instance is in fact expected to be an {@link org.sapia.soto.state.xml.XMLContext}.
 * <p>
 * More precisely, the markup will be serialized as SAX events to the XML context's <code>ContentHandler</code>.
 * 
 * @see org.sapia.soto.state.xml.XMLContext#getContentHandler()
 *  
 * @author yduchesne
 *
 */
public class SAXSerializer extends AbstractSerializer{
  
  static final String EMPTY_STRING = "";
  static final AttributesImpl EMPTY_ATTRS = new AttributesImpl();
  boolean outputNamespace, xmlElements;
  
  public SAXSerializer(Settings settings){
    super(settings);
    outputNamespace = MarkupSerializerFactory.isOutputNamespace(settings, false);
    xmlElements = MarkupSerializerFactory.isEncodingElements(settings, true);    
  }
  
  public void start(MarkupInfo info, Context ctx, PrintWriter pw) throws Exception {
    ContentHandler handler = ((XMLContext)ctx).getContentHandler();
    Attributes attrs = outputAttributes(info.getAttributes());    
    if(info.isNamespaceEnabled() && outputNamespace){
      handler.startElement(info.getUri(), info.getName(), 
          new StringBuffer(info.getPrefix()).append(":").append(info.getName()).toString(), 
          attrs);            
    }
    else{
      handler.startElement(EMPTY_STRING, info.getName(), info.getName(), attrs);
    }
    if(xmlElements){
      outputElements(handler, info.getAttributes());
    }
  }
  
  public void text(String txt, Context ctx, PrintWriter pw) throws Exception {
    if(txt != null){
      ContentHandler handler = ((XMLContext)ctx).getContentHandler();      
      char[] chars = txt.toCharArray(); 
      handler.characters(txt.toCharArray(), 0, chars.length);
    }
  }
  
  public void end(MarkupInfo info, Context ctx, PrintWriter pw) throws Exception {
    ContentHandler handler = ((XMLContext)ctx).getContentHandler();
    if(info.isNamespaceEnabled() && outputNamespace){
      handler.endElement(info.getUri(), info.getName(), 
          new StringBuffer(info.getPrefix()).append(":").append(info.getName()).toString());
    }    
    else{
      handler.endElement(EMPTY_STRING, info.getName(), info.getName());      
    }
  }
  
  private Attributes outputAttributes(List attrInfos){
    AttributesImpl attrs = new AttributesImpl();
    for(int i = 0; i < attrInfos.size(); i++){
      MarkupInfo.Attribute attr = (MarkupInfo.Attribute)attrInfos.get(i);
      if(attr.encodeAsAttribute || !xmlElements){
        attrs.addAttribute(EMPTY_STRING, attr.getName(), EMPTY_STRING, EMPTY_STRING, attr.getValue());
      }
    }
    return attrs;
  }
  
  private void outputElements(ContentHandler handler, List attrInfos) throws Exception{
    for(int i = 0; i < attrInfos.size(); i++){
      MarkupInfo.Attribute attr = (MarkupInfo.Attribute)attrInfos.get(i);
      if(!attr.encodeAsAttribute){
        handler.startElement(EMPTY_STRING, attr.getName(), attr.getName(), EMPTY_ATTRS);      
        handler.endElement(EMPTY_STRING, attr.getName(), attr.getName());
      }
    }
  }  

}
