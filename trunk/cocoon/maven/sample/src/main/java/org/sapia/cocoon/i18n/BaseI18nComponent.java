package org.sapia.cocoon.i18n;

import java.io.IOException;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.ResourceNotFoundException;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.sitemap.SitemapModelComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.excalibur.source.Source;
import org.xml.sax.SAXException;

public class BaseI18nComponent implements SitemapModelComponent{
  
  public static final String DEFAULT_LOCALE_STR = "en";
  
  public  static final String PARAM_LOCALE = "locale";

  protected CacheKey key;
  protected i18nHelper helper = new i18nHelper();
  protected SourceResolver resolver;
  protected Map objectModel;
  
  protected Log log = LogFactory.getLog(getClass());
  
  
  public void setup(SourceResolver resolver, Map objectModel, String src, Parameters parameters) throws ProcessingException, SAXException, IOException {
    this.resolver = resolver;
    this.objectModel = objectModel;
    this.resolver = resolver;
    String localeStr = parameters.getParameter(PARAM_LOCALE, DEFAULT_LOCALE_STR);

    i18nInfo info = i18nInfo.parse(src);
    
    // locale info is present in the URI
    if(info.getLocale() != null){
      key = new CacheKey(info.getSrcExt(), info.getLocale());
    }
    // no locale info in URI, use the one passed in
    else{
      key = new CacheKey(info.getSrcExt(), localeStr);      
    }    
    
    resolve();
  }

  private Source resolve() throws ProcessingException, SAXException, IOException{
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
      throw new ResourceNotFoundException(key.getSrc());
    }
    if(log.isDebugEnabled()){
      log.debug("found source: " + sourceObj.getURI());
    }
    key.setSource(sourceObj);
    return sourceObj;
  }
  
}
