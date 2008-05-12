package org.sapia.cocoon.i18n;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.cocoon.environment.SourceResolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.excalibur.source.Source;

public class i18nHelper {
  
  private Log log = LogFactory.getLog(getClass());

  public Source resolveURI(SourceResolver resolver, List<String> uris) throws IOException{
    if(log.isDebugEnabled()){    
      log.debug("Resolving " + uris);
    }
    for(String uri:uris){
      Source aSource = resolver.resolveURI(uri);
      if(aSource.exists()){
        return aSource;
      }
    }
    return null;
  }
  
  public List<String> getFileURIsFor(String src, String someLocale){
    List<String> locales = extractLocales(someLocale);
    List<String> uris = new ArrayList<String>(locales.size());

    // check if extension
    int i = src.lastIndexOf('.');
    String baseUri = null;
    String ext = null;
    if(i > 0){
      ext = src.substring(i);
      baseUri = new StringBuffer(src.substring(0, i)).toString(); 
    }
    else{
      baseUri = new StringBuffer(src).toString();
    }    
    
    for(String locale:locales){
      StringBuffer uri = new StringBuffer(baseUri);
      uri.append("_").append(locale);
      if(ext != null){
        uri.append(ext);
      }
      uris.add(uri.toString());
    }
    return uris;
  }
  
  public List<String> getDirURIsFor(String src, String someLocale){
    List<Locale> locales = extractLocaleObjects(someLocale);
    List<String> uris = new ArrayList<String>(locales.size());
    
    // check if multi-segment in path

    int i = src.lastIndexOf('/');
    String baseUri = null;
    String ext = null;    
    
    // format: some/path/to/dir/src.txt    
    if(i > 0){
      baseUri = new StringBuffer(src.substring(0, i)).append("/_").toString();
      src = src.substring(i+1);      
    }
    // format: src.txt    
    else{
      baseUri = "/_";
    }
    
    i = src.lastIndexOf('.');
    if(i > 0){
      ext = src.substring(i);
      src = src.substring(0, i);
    }
    for(Locale locale:locales){
      StringBuffer uri = new StringBuffer(baseUri);
      uri.append(locale.getLanguage()).append("/");
      if(!isNull(locale.getCountry()) && !isNull(locale.getVariant())){
        uri.append(src).append("_").append(locale.getCountry()).append("_").append(locale.getVariant());
      }
      else if(!isNull(locale.getCountry())){
        uri.append(src).append("_").append(locale.getCountry());
      }
      else{
        uri.append(src);
      }
      
      if(ext != null){
        uri.append(ext);
      }      
      uris.add(uri.toString());

    }
    return uris;
  }  
  
  protected List<String> extractLocales(String localeStr){
    StringTokenizer tokenizer = new StringTokenizer(localeStr, "_");
    
    String lang    = null;
    String country = null;
    String variant = null;
    
    // lang
    if(tokenizer.hasMoreTokens()){
      lang = tokenizer.nextToken();
    }
    if(tokenizer.hasMoreTokens()){
      country = tokenizer.nextToken();
    }
    if(tokenizer.hasMoreTokens()){
      variant = tokenizer.nextToken();
    }
    List<String> toReturn = new ArrayList<String>(3);
    if(country != null && variant != null){
      toReturn.add(new StringBuffer().append(lang).append("_").append(country).append("-").append(variant).toString());
    }
    if(country != null){
      toReturn.add(new StringBuffer().append(lang).append("_").append(country).toString());
    }
    toReturn.add(new StringBuffer().append(lang).toString());
    return toReturn;
  }
  
  public List<Locale> extractLocaleObjects(String localeStr){
    StringTokenizer tokenizer = new StringTokenizer(localeStr, "_");
    
    String lang    = null;
    String country = null;
    String variant = null;
    
    // lang
    if(tokenizer.hasMoreTokens()){
      lang = tokenizer.nextToken();
    }
    if(tokenizer.hasMoreTokens()){
      country = tokenizer.nextToken();
    }
    if(tokenizer.hasMoreTokens()){
      variant = tokenizer.nextToken();
    }
    List<Locale> toReturn = new ArrayList<Locale>(3);
    if(country != null && variant != null){
      toReturn.add(new Locale(lang, country, variant));;
    }
    if(country != null){
      toReturn.add(new Locale(lang, country));
    }
    toReturn.add(new Locale(lang));
    return toReturn;
  }
  
  public boolean isNull(String str){
    return str == null || str.length() == 0;
  }
  
}
