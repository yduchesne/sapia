package org.sapia.cocoon.generation.chunk;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.generation.Generator;
import org.apache.cocoon.sitemap.SitemapModelComponent;
import org.apache.cocoon.xml.XMLConsumer;
import org.apache.excalibur.source.Source;
import org.sapia.cocoon.generation.chunk.exceptions.TemplateNotFoundException;
import org.sapia.cocoon.generation.chunk.template.Template;
import org.sapia.cocoon.generation.chunk.template.TemplateContext;
import org.sapia.cocoon.generation.chunk.template.TemplateResolver;
import org.sapia.cocoon.util.InputModuleStrLookup;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class ChunkGenerator implements Generator, SitemapModelComponent, BeanFactoryAware{

  static final long DEFAULT_EXPIRES = -1;
  
  private XMLConsumer _consumer;
  private SourceResolver _sources;
  private TemplateResolver _templates;
  private BeanFactory _beans;
  private String _src;
  private Map _objectModel;
  
  public void setConsumer(XMLConsumer consumer) {
    _consumer = consumer;
  }
  
  public void setTemplates(TemplateResolver templates){
    _templates = templates;
  }
  
  public void setBeanFactory(BeanFactory beans) throws BeansException {
    _beans = beans; 
  }
  
  public void setup(SourceResolver resolver, Map objectModel, String src, Parameters params) throws ProcessingException, SAXException, IOException {
    if(_templates == null){
      throw new IllegalStateException("TemplateResolver not set");
    }
    _sources = resolver;
    _src = src;
    _objectModel = objectModel;
  }
  
  public void generate() throws IOException, SAXException, ProcessingException {
    Template t = _templates.resolveTemplate(_src, _sources);
    TemplateContext ctx = new TemplateContextImpl(
        _consumer, 
        _sources,
        _templates, 
        new InputModuleStrLookup(_beans, _objectModel));
    t.render(ctx);
  }
  
  static class TemplateContextImpl implements TemplateContext{
    private SourceResolver sources;
    private TemplateResolver templates;
    private ContentHandler handler;
    private InputModuleStrLookup lookup;
    private boolean lenient = false;
    
    public TemplateContextImpl(
        ContentHandler handler, 
        SourceResolver sources,
        TemplateResolver templates, 
        InputModuleStrLookup lookup) {
      this.handler = new ContentHandlerWrapper(handler);
      this.sources = sources;
      this.templates = templates;
      this.lookup = lookup;
    }
    
    public ContentHandler getContentHandler() {
      return handler;
    }
    
    public Template resolveTemplate(String uri) 
      throws TemplateNotFoundException, SAXException, IOException{
      return templates.resolveTemplate(uri, sources);
    }
    
    public void include(String uri) throws SAXException, IOException {
      XMLReader reader = XMLReaderFactory.createXMLReader();
      reader.setContentHandler(handler);
      Source s = sources.resolveURI(uri);
      InputStream src = sources.resolveURI(s.getURI()).getInputStream();
      try{
        reader.parse(new InputSource(src));
      }finally{
        if(src != null){
          src.close();
        }
        sources.release(s);
      }
    }
    
    public Object getValue(String prefix, String name) {
      try{
        return lookup.lookup(prefix, name);
      }catch(ConfigurationException e){
        throw new IllegalStateException("Could not acquire variable", e);
      }
    }
    
    public void setLenient(boolean lenient) {
      this.lenient = lenient;
    }
    
    public boolean isLenient() {
      return lenient;
    }
    
  }

  public static class ContentHandlerWrapper implements ContentHandler{
    
    private ContentHandler delegate;
    private int level;
    
    public ContentHandlerWrapper(ContentHandler delegate) {
      this.delegate = delegate;
    }
    

    public void startDocument() throws SAXException {
      if(level == 0){
        delegate.startDocument();
      }
      level++;
    }
    
    public void endDocument() throws SAXException {
       level--;
       if(level == 0){
         delegate.endDocument();
       }
    }

    public void startElement(String uri, String localName, String name,
        Attributes atts) throws SAXException {
      delegate.startElement(uri, localName, name, atts);
    }

    
    public void endElement(String uri, String localName, String name)
        throws SAXException {
      delegate.endElement(uri, localName, name);
    }
    
    public void characters(char[] ch, int start, int length)
    throws SAXException {
      delegate.characters(ch, start, length);
    }

    public void startPrefixMapping(String prefix, String uri)
    throws SAXException {
      delegate.startPrefixMapping(prefix, uri);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
      delegate.endPrefixMapping(prefix);
    }
    
    public void ignorableWhitespace(char[] ch, int start, int length)
        throws SAXException {
      delegate.ignorableWhitespace(ch, start, length);
    }
    
    public void processingInstruction(String target, String data)
        throws SAXException {
      delegate.processingInstruction(target, data);
    }
    
    public void setDocumentLocator(Locator locator) {
      delegate.setDocumentLocator(locator);
    }
    
    public void skippedEntity(String name) throws SAXException {
      delegate.skippedEntity(name);
    }
    
  }
}
