package org.sapia.cocoon.generation.chunk;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.ResourceNotFoundException;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.generation.Generator;
import org.apache.cocoon.sitemap.SitemapModelComponent;
import org.apache.cocoon.xml.XMLConsumer;
import org.apache.excalibur.source.Source;
import org.sapia.cocoon.CocoonConsts;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class ChunkGenerator implements Generator, SitemapModelComponent{

  // http://www.sapia-oss.org/cocoon/generator/chunk/2.2
  public static final String NAMESPACE = CocoonConsts.NAMESPACE_URI + "/generator/chunk/2.2";
  public static final String CHUNCK_ELEMENT_NAME = "chunk";
  public static final String SRC_ATTR_LOCAL_NAME = "src";
  public static final String SRC_ATTR_NAMESPACE = "";

  static final long DEFAULT_EXPIRES = -1;
  
  private XMLConsumer _consumer;
  private Source _source;
  private SourceResolver _resolver;
  
  public void setConsumer(XMLConsumer consumer) {
    _consumer = consumer;
  }
  
  public void setup(SourceResolver resolver, Map objectModel, String src, Parameters params) throws ProcessingException, SAXException, IOException {
    _resolver = resolver;
    _source = resolver.resolveURI(src);
    if(!_source.exists()){
      throw new ResourceNotFoundException(src);
    }
  }
  
  public void generate() throws IOException, SAXException, ProcessingException {
    GeneratorContentHandler handler = new GeneratorContentHandler();
    XMLReader reader = XMLReaderFactory.createXMLReader();
    reader.setContentHandler(handler);
    InputStream is = null;
    try{
      is = _source.getInputStream();
      reader.parse(new InputSource(is));
    }finally{
      if(is != null){
        is.close();
      }
    }
  }
  
  /////// INNER CLASSES 
  
  public class GeneratorContentHandler implements ContentHandler{
    
    private int _depth = 0;
    
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
      if(uri != null && uri.length() > 0 && uri.equals(NAMESPACE) && localName.equals(CHUNCK_ELEMENT_NAME)){
        String src = atts.getValue(SRC_ATTR_NAMESPACE, SRC_ATTR_LOCAL_NAME);
        if(src == null){
          throw new SAXException(SRC_ATTR_LOCAL_NAME + " attribute not set on " + CHUNCK_ELEMENT_NAME + "element");
        }
        Source nested = null;
        try{
          nested = _resolver.resolveURI(src);
        }catch(IOException e){
          throw new SAXException("Could not resolve " + src, e);
        }
        InputStream is = null; 
        try{
          is = nested.getInputStream();
          InputSource input = new InputSource(is);
          XMLReader reader = XMLReaderFactory.createXMLReader();
          reader.setContentHandler(this);
          reader.parse(input);
        }catch(IOException e){
          throw new SAXException("Could not open " + src, e);
        }finally{
          if(is != null){
            try{
              is.close();
            }catch(IOException e){
              // noop
            }
          }
        }
        
      }
      else{
        _consumer.startElement(uri, localName, qName, atts);
      }
    }
    
    public void endElement(String uri, String localName, String qName) throws SAXException {
      if(uri != null && uri.length() > 0 && uri.equals(NAMESPACE) && localName.equals(CHUNCK_ELEMENT_NAME)){
        // noop
      }
      else{
        _consumer.endElement(uri, localName, qName);
      }
    }

    public void startDocument() throws SAXException {
      if(_depth == 0){
        _consumer.startDocument();
      }
      _depth++;      
    }

    public void endDocument() throws SAXException {
      _depth--;      
      if(_depth == 0){
        _consumer.endDocument();
      }
    }
    
    public void characters(char[] ch, int start, int length) throws SAXException {
      _consumer.characters(ch, start, length);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
      _consumer.endPrefixMapping(prefix);
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
      _consumer.ignorableWhitespace(ch, start, length);
    }

    public void processingInstruction(String target, String data) throws SAXException {
      _consumer.processingInstruction(target, data);
    }

    public void setDocumentLocator(Locator locator) {
      _consumer.setDocumentLocator(locator);
    }

    public void skippedEntity(String name) throws SAXException {
      _consumer.skippedEntity(name);
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
      _consumer.startPrefixMapping(prefix, uri);
    }
  }
}
