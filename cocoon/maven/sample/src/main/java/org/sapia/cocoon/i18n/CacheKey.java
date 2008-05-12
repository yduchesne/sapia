package org.sapia.cocoon.i18n;

import java.io.Serializable;

import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceValidity;
import org.sapia.cocoon.util.NullValidity;

public class CacheKey implements Serializable{
  
  static final long serialVersionUID = 1L;
  
  static final SourceValidity NULL_VALIDITY = new NullValidity();
  
  String src, locale;
  Source source;
  
  public CacheKey(String src, String locale){
    this.src = src;
    this.locale = locale;
  }
  
  public String getSrc(){
    return src;
  }
  
  public String getLocale(){
    return locale;
  }
  
  public Source getSource() {
    return source;
  }

  public void setSource(Source source) {
    this.source = source;
  }  
  
  public int hashCode() {
    int hash = src.hashCode() ^ locale.hashCode();
    return hash;
  }
  
  public boolean equals(Object obj) {
    boolean equals = doEquals(obj);
    return equals;
  }
  private boolean doEquals(Object obj){
    if(obj instanceof CacheKey){
      CacheKey other = (CacheKey)obj;
      return src.equals(other.src) && locale.equals(other.locale);
    }
    else{
      return false;
    }
  }
  
  public SourceValidity getValidity(){
    if(source != null){
      return source.getValidity();
    }
    else{
      System.out.println(toString());      
      return NULL_VALIDITY;
    }
  }
  
  public String toString(){
    return "[src=" + src + ", source=" + (source != null ? source.getURI() : source) + ", locale="+locale+"]"; 
  }

}
