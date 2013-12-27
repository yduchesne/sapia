package org.sapia.cocoon.generation.chunk.template;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import org.apache.excalibur.source.Source;
import org.sapia.cocoon.generation.chunk.template.content.ContentParser;
import org.sapia.cocoon.generation.chunk.template.content.TemplateContent;
import org.sapia.cocoon.generation.chunk.template.node.CDataNode;
import org.sapia.cocoon.generation.chunk.template.node.DocumentNode;
import org.sapia.cocoon.generation.chunk.template.node.PrefixMappingNode;
import org.sapia.cocoon.generation.chunk.template.node.ProcessingInstructionNode;
import org.sapia.cocoon.generation.chunk.template.node.SkippedEntityNode;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * An instance of this class parses XML templates, creating the corresponding
 * {@link Template} instances.
 * 
 * @author yduchesne
 *
 */
public class TemplateParser {
  
  private ParserContext context;
  
  public TemplateParser(ParserConfig conf) {
    ContentParser contentParser = new ContentParser();
    contentParser.setStartDelim(conf.getStartVarDelim());
    contentParser.setEndDelim(conf.getEndVarDelim());
    context = new ParserContext(contentParser);
  }
  
  /**
   * @param src the {@link Source} of a template.
   * @return the {@link Template} that was parsed.
   * @throws SAXException
   * @throws IOException
   */
  public Template parse(Source src) throws SAXException, IOException{
    return parse(src.getInputStream());
  }

  /**
   * This method closes the stream that is passed in before returning.
   * 
   * @param src the {@link InputStream} corresponding to a template.
   * @return the {@link Template} that was parsed.
   * @throws SAXException
   * @throws IOException
   */
  public Template parse(InputStream src) throws SAXException, IOException{
    TemplateContentHandler handler = new TemplateContentHandler();
    XMLReader reader = XMLReaderFactory.createXMLReader();
    reader.setContentHandler(handler);
    reader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
    try{
      reader.parse(new InputSource(src));
    }finally{
      if(src != null){
        src.close();
      }
    }
    
    Node root = handler.getRoot();
    return new XMLTemplate(root);
  }
  
  ///////////////// inner classes ///////////////////
  
  class TemplateContentHandler extends DefaultHandler2{
    
    Node root;
    Stack<Node> nodeStack = new Stack<Node>();
    StringBuilder cdata = new StringBuilder();
   
    @Override
    public void startDocument() throws SAXException {
      clearCdata();
      DocumentNode root = new DocumentNode();
      nodeStack.push(root);
    }

    @Override
    public void endDocument() throws SAXException {
      root = nodeStack.pop();
    }
    
    Node getRoot(){
      return root;
    }


    @Override
    public void startElement(String uri, String localName, String name,
        Attributes atts) throws SAXException {

      // adding to current node before starting new one
      addCdata();
      
      ElementInfo info = new ElementInfo(localName, name, uri, atts);
      Node element = NodeFactory.createElementNode(context, info);
      add(element);
    }
    
    @Override
    public void endElement(String uri, String localName, String name)
        throws SAXException {
      
      // adding to current node before starting new one
      addCdata();

      nodeStack.pop();
    }
    
    @Override
    public void startCDATA() throws SAXException {
      cdata.append("<![CDATA[");
    }
    
    @Override
    public void endCDATA() throws SAXException {
      cdata.append("]]>");
    }

    @Override
    public void characters(char[] ch, int start, int length)
    throws SAXException {
      String chars = new String(ch, start, length);
      cdata.append(chars);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length)
    throws SAXException {
      String chars = new String(ch, start, length);
      cdata.append(chars);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri)
        throws SAXException {
      TemplateContent prefixContent = context.getContentParser().parse(prefix);
      TemplateContent uriContent = context.getContentParser().parse(uri);
      PrefixMappingNode node = new PrefixMappingNode(prefixContent, uriContent);
      add(node);
    }
    
    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
      nodeStack.pop();
    }
    
    @Override
    public void processingInstruction(String target, String data)
        throws SAXException {
      ProcessingInstructionNode node = 
        new ProcessingInstructionNode(
            context.getContentParser().parse(target), 
            context.getContentParser().parse(data));
      add(node);
    }
    
    @Override
    public void setDocumentLocator(Locator locator) {
      /*LocatorNode node = new LocatorNode(locator);
      add(node);*/
    }
    
    @Override
    public void skippedEntity(String name) throws SAXException {
      SkippedEntityNode node = new SkippedEntityNode(name);
      add(node);
    }
    
    private void addCdata(){
      CDataNode cdn = new CDataNode(context.getContentParser().parse(cdata.toString()));
      add(cdn);
      clearCdata();
    }
    
    private void clearCdata(){
      cdata.delete(0, cdata.length());
    }
    
    private void add(Node child){
      add(nodeStack.peek(), child);
    }
    private void add(Node parent, Node child){
      if(parent instanceof ComplexNode){
        ((ComplexNode)parent).addChild(child);
        if(child instanceof ComplexNode){
          nodeStack.push(child);
        }
      }
      else{
        throw new IllegalArgumentException(parent + " not a complex node; cannot have a child");
      }
    }
  }

}
