package org.sapia.soto.state.markup;

import java.io.PrintWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.sapia.soto.Settings;
import org.sapia.soto.state.Context;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * An instance of this class instantiates DOM nodes (elements and attributes) corresponding to the passed in
 * markup information. The resulting elements are pushed onto the passed in context's stack.
 * 
 * @author yduchesne
 *
 */
public class DOMSerializer extends AbstractSerializer{
  
  boolean outputNamespace, xmlElements;
  
  public DOMSerializer(Settings settings){
    super(settings);
    outputNamespace = MarkupSerializerFactory.isOutputNamespace(settings, false);
    xmlElements = MarkupSerializerFactory.isEncodingElements(settings, true);    
  }
  
  public void start(MarkupInfo info, Context ctx, PrintWriter pw) throws Exception {
    Element child;
    if(ctx.hasCurrentObject() && ctx.currentObject() instanceof Element){
      Element  parent = (Element)ctx.currentObject();
      Document owner = parent.getOwnerDocument();
      child = createElement(info, owner);
      parent.appendChild(child);
    }
    else{
      Document owner = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
      ctx.push(owner);
      child = createElement(info, owner);
      owner.appendChild(child);
    }
    outputAttributes(info, child.getOwnerDocument(), child);
    ctx.push(child);
  }
  
  public void text(String txt, Context ctx, PrintWriter pw) throws Exception {
    if(txt != null){
      Element elem = (Element)ctx.currentObject();
      Node cdata = elem.getOwnerDocument().createCDATASection(txt);
      elem.appendChild(cdata);
    }
  }
  
  public void end(MarkupInfo info, Context ctx, PrintWriter pw) throws Exception {
    ctx.pop();
  }
  
  private void outputAttributes(MarkupInfo info, Document owner, Element parent){
    List attributes = info.getAttributes();
    for(int i = 0; i < attributes.size(); i++){
      MarkupInfo.Attribute attr = (MarkupInfo.Attribute)attributes.get(i);
      if(attr.getValue() != null){
        Node child = createAttribute(attr, owner);
        parent.appendChild(child);
      }
    }
  }
  
  private Element createElement(MarkupInfo info, Document owner){
    if(info.isNamespaceEnabled() && outputNamespace){
      return owner.createElementNS(info.getUri(), new StringBuffer(info.getPrefix()).append(':')
          .append(info.getName()).toString());
    }
    else{
      return owner.createElement(info.getName());
    }    
  }

  
  private Node createAttribute(MarkupInfo.Attribute attr, Document owner){
    if(attr.encodeAsAttribute || !xmlElements){
      Attr attribute = owner.createAttribute(attr.getName());
      attribute.setNodeValue(attr.getValue());
      return attribute;
    }
    else{
      Element elem = owner.createElement(attr.getName());
      elem.appendChild(owner.createCDATASection(attr.getValue()));
      return elem;
    }
    
  }
  

}
