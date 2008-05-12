package org.sapia.cocoon.source.replace;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Map;

import org.apache.cocoon.components.modules.input.InputModule;
import org.apache.cocoon.components.source.util.SourceUtil;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceFactory;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceValidity;
import org.sapia.cocoon.util.ChainedStrLookup;
import org.sapia.cocoon.util.InputModuleStrLookup;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * This class implements a {@link SourceFactory} that performs variable subsitution
 * in text resources of any format (plain, XML, css, etc.). Variables are embedded in the
 * underlying text resources using the following notation:
 * <pre>
 *  ${variable_key}
 * </pre>
 * Variables values are looked up in:
 * <ol>
 *   <li>The parameters passed to an instance of this class at runtime (see {@link #getSource(String, Map)}).
 *   <li>Cocoon's input modules.
 * <ol>
 * Variable values are inserted at the place of the corresponding variables in the content.
 * <p>
 * Note that when values are to be looked up from {@link InputModule}s, the
 * following notation must be used in the content:
 * <pre>
 *  ${input_module_name:variable_name}
 * </pre>
 * 
 * @author yduchesne
 *
 */
public class ReplaceSourceFactory implements SourceFactory, BeanFactoryAware{

  BeanFactory _factory;
  
  public void setBeanFactory(BeanFactory factory) throws BeansException {
    _factory = factory;
  }

  public Source getSource(String uri, Map parameters) throws IOException, MalformedURLException {
    uri = SourceUtil.getPath(uri);
    SourceResolver resolver = (SourceResolver)_factory.getBean(SourceResolver.ROLE);
		Source src = resolver.resolveURI(uri);
    if(src.exists()){
      InputStream is = new BufferedInputStream(src.getInputStream(), 2048);
      try{
        byte[] bytes = replaceVars(is, parameters);
        return new ReplacementSource(src, bytes);
      }finally{
        is.close();
      }
    }
    else{
      return new ReplacementSource(src, null);
    }
  }

  public void release(Source src) {
  }
  
  private byte[] replaceVars(InputStream is, Map parameters) throws IOException{
    String content = IOUtils.toString(is);
    ChainedStrLookup lookup = new ChainedStrLookup();
    StrSubstitutor sub = new StrSubstitutor(
      lookup.add(StrLookup.mapLookup(parameters))
            .add(new InputModuleStrLookup(_factory))
    );
    sub.setEscapeChar('\\');
    sub.setVariablePrefix("$[");
    sub.setVariableSuffix(']');
    return sub.replace(content).getBytes();
  }  
  
  /////// INNER CLASSES
  
  static class ReplacementSource implements Source{
    
    private Source _delegate;
    private byte[] _bytes;
    
    ReplacementSource(Source delegate, byte[] bytes){
      _delegate = delegate;
      _bytes = bytes;
    }
    
    public boolean exists() {
      return _delegate.exists();
    }
    
    public long getContentLength() {
      if(_bytes == null){
        return 0;
      }
      else{
        return _bytes.length;
      }
    }
    
    public InputStream getInputStream() throws IOException, SourceNotFoundException {
      if(_bytes == null){
        throw new SourceNotFoundException(_delegate.getURI());
      }
      return new ByteArrayInputStream(_bytes);
    }
    
    public long getLastModified() {
      return _delegate.getLastModified();
    }
    
    public String getMimeType() {
      return _delegate.getMimeType();
    }
    
    public String getScheme() {
      return _delegate.getScheme();
    }
    
    public String getURI() {
      return _delegate.getURI();
    }
    
    public SourceValidity getValidity() {
      return _delegate.getValidity();
    }
    
    public void refresh() {
      _delegate.refresh();
    }
    
  }

}
