package org.sapia.cocoon.generation.chunk.template;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.excalibur.source.Source;
import org.sapia.cocoon.TestSourceResolver;
import org.sapia.cocoon.generation.chunk.exceptions.TemplateNotFoundException;
import org.sapia.cocoon.generation.chunk.template.ParserConfig;
import org.sapia.cocoon.generation.chunk.template.Template;
import org.sapia.cocoon.generation.chunk.template.TemplateCache;
import org.sapia.cocoon.generation.chunk.template.TemplateContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class TestTemplateContext implements TemplateContext{
  
  private Map<String, Object> values = new HashMap<String, Object>();
  private ContentHandler handler;
  private boolean lenient = true;
  private TemplateCache cache;
  private TestSourceResolver resolver;
  
  public TestTemplateContext() {
    this(new HashMap<String, Object>(), null);
  }
  public TestTemplateContext(Map<String, Object> values, 
      ContentHandler handler) {
    this(values, handler, new TemplateCache(), new TestSourceResolver());
  }
  public TestTemplateContext(Map<String, Object> values, 
      ContentHandler handler, TemplateCache cache, TestSourceResolver sources) {
    this.values = values;
    this.handler = handler;
    this.cache = cache;
    this.resolver = sources;
  }
  
  public void include(String uri) throws SAXException, IOException {
    XMLReader reader = XMLReaderFactory.createXMLReader();
    reader.setContentHandler(handler);
    reader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
    Source s = resolver.resolveURI(uri);
    InputStream src = resolver.resolveURI(s.getURI()).getInputStream();
    try{
      reader.parse(new InputSource(src));
    }finally{
      if(src != null){
        src.close();
      }
      resolver.release(s);
    }
  }
  
  public TestTemplateContext put(String varName, Object value){
    values.put(varName, value);
    return this;
  }
  
  public ContentHandler getContentHandler() {
    return handler;
  }
  
  public Object getValue(String prefix, String name) {
    return values.get(name);
  }
  
  public boolean isLenient() {
    return lenient;
  }
  
  public void setLenient(boolean lenient) {
    this.lenient = lenient;
  }
  
  public Template resolveTemplate(String uri) 
    throws TemplateNotFoundException, SAXException, IOException {
    return cache.get(new ParserConfig(), uri, resolver);
  }
  

}
