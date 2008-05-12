package org.sapia.cocoon.source.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Locale;
import java.util.Map;

import org.apache.cocoon.components.source.util.SourceUtil;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceFactory;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceValidity;
import org.sapia.cocoon.i18n.CacheKey;
import org.sapia.cocoon.i18n.i18nHelper;
import org.sapia.cocoon.i18n.i18nInfo;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

public class i18nSourceFactory implements SourceFactory, BeanFactoryAware{

  private BeanFactory _factory;
  private String _localeStr = Locale.ENGLISH.getLanguage();
  private String _localeParam;

  public void setDefaultLocale(String locale){
    _localeStr = locale;
  }
  
  public void setLocaleParam(String queryParam){
    _localeParam = queryParam;
  }  
  
  public void setBeanFactory(BeanFactory factory) throws BeansException {
    _factory = factory;
  }
  
  public Source getSource(String uri, Map params) throws IOException, MalformedURLException {
    SourceResolver resolver = ((SourceResolver)_factory.getBean(SourceResolver.ROLE));
    
    String locale = null;
    
    if(_localeParam != null){
      if(params != null){
        locale = (String)params.get(_localeParam);
      }
      if(locale == null){
        String queryString = SourceUtil.getQuery(uri);
        int i = queryString.indexOf(_localeParam);
        if(i > -1){
          queryString = queryString.substring(i);
          i = queryString.indexOf('=');
          if(i < queryString.length() - 1 && i > -1){
            String value = queryString.substring(i+1);
            i = value.indexOf('&');
            if(i > -1){
              locale = value.substring(0, i);
            }
            else{
              locale = value;
            }
          }
        }
      }
    }
    if(locale == null){
      locale = _localeStr;
    }
    uri = SourceUtil.getPath(uri);
    i18nInfo info = i18nInfo.parse(uri);

    CacheKey key;
    // locale info is present in the URI
    if(info.getLocale() != null){
      key = new CacheKey(info.getSrcExt(), info.getLocale());
    }
    // no locale info in URI, use the one passed in
    else{
      key = new CacheKey(info.getSrcExt(), locale);      
    }    
  
    i18nHelper helper = new i18nHelper();
    
    Source sourceObj = helper.resolveURI(resolver, helper.getFileURIsFor(key.getSrc(), key.getLocale()));
    if(sourceObj == null){
      sourceObj = helper.resolveURI(resolver, helper.getDirURIsFor(key.getSrc(), key.getLocale()));
    }
    if(sourceObj == null){
      Source aSource = resolver.resolveURI(key.getSrc());
      if(aSource.exists()){
        sourceObj = aSource;
      }
    }
    if(sourceObj == null){
      throw new IOException("Source not found: " + uri);
    }
    key.setSource(sourceObj);
    return new i18nSource(sourceObj);
  }
  
  public void release(Source arg0) {}
  
 /////// INNER CLASSES
  
  static class i18nSource implements Source{
    
    private Source _delegate;
    
    i18nSource(Source delegate){
      _delegate = delegate;
    }
    
    public boolean exists() {
      return _delegate.exists();
    }
    
    public long getContentLength() {
      return _delegate.getContentLength();
    }
    
    public InputStream getInputStream() throws IOException, SourceNotFoundException {
      return _delegate.getInputStream();
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
