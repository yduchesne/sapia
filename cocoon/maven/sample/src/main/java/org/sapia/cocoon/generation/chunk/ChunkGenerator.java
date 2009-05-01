package org.sapia.cocoon.generation.chunk;

import java.io.IOException;
import java.util.Map;

import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.generation.Generator;
import org.apache.cocoon.sitemap.SitemapModelComponent;
import org.apache.cocoon.xml.XMLConsumer;
import org.sapia.cocoon.generation.chunk.exceptions.TemplateNotFoundException;
import org.sapia.cocoon.generation.chunk.template.Template;
import org.sapia.cocoon.generation.chunk.template.TemplateContext;
import org.sapia.cocoon.generation.chunk.template.TemplateResolver;
import org.sapia.cocoon.util.InputModuleStrLookup;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

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
      this.handler = handler;
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

}
