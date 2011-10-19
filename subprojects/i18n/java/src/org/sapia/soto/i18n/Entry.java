/*
 * Entry.java
 *
 * Created on June 3, 2005, 7:55 PM
 */

package org.sapia.soto.i18n;

import java.util.HashMap;
import java.util.Map;

import org.sapia.util.text.MapContext;
import org.sapia.util.text.SystemContext;
import org.sapia.util.text.TemplateContextIF;
import org.sapia.util.text.TemplateElementIF;
import org.sapia.util.text.TemplateFactory;

/**
 * An instance of this class holds a bit of text.
 *
 * @author yduchesne
 */
public class Entry {
  
  private TemplateElementIF _templateElement;
  private String _lang, _country, _variant;

  private static TemplateContextIF SYSTEM_CONTEXT = new SystemContext();  
  private static TemplateContextIF EMPTY_CONTEXT = new MapContext(new HashMap(), SYSTEM_CONTEXT , false);
  
  /** Creates a new instance of Entry */
  public Entry() {
  }
  
  /**
   * @param lang the language to which this instance corresponds.
   */
  public void setLa(String lang){
    _lang = lang;
  }
  
  /**
   * @param co the country to which this instance corresponds.
   */  
  public void setCo(String country){
    _country = country;
  }
  
  /**
   * @param variant the variant to which this instance corresponds.
   */  
  public void setVa(String variant){
    _variant = variant;
  }
  
  /**
   * @return the language to which this instance corresponds.
   */  
  public String getLanguage(){
    return _lang;
  }
  
  /**
   * @return the country to which this instance corresponds.
   */  
  public String getCountry(){
    return _country;
  }
  
  /**
   * @return the variant to which this instance corresponds.
   */  
  public String getVariant(){
    return _variant;
  }
  
  /**
   * @param text the text that this instance holds.
   */  
  public void setText(String text){
    TemplateFactory fac = new TemplateFactory();
    _templateElement = fac.parse(text);
  }
  
  /**
   * @return the text that this instance holds.
   */  
  public String getText() throws Exception{
    if(_templateElement == null){
      return null;
    }
    return _templateElement.render(EMPTY_CONTEXT);
  }
  
  /**
   * @return the text that this instance holds.
   *
   * @param params a <code>Map</code> of values that should be used to interpolate
   * this instance's variables.
   */  
  public String getText(Map params) throws Exception{
    if(_templateElement == null){
      return null;
    }
    return _templateElement.render(new MapContext(params, SYSTEM_CONTEXT, false));
  }
  
  /**
   * @return the text that this instance holds.
   *
   * @param ctx a <code>TemplateContextIF</code> holding the values that should 
   * be used to interpolate this instance's variables.
   */  
  public String getText(TemplateContextIF ctx) throws Exception{
    if(_templateElement == null){
      return null;
    }
    return _templateElement.render(ctx);
  }  
  
  void bind(Entry entry){
    if(_lang != null){
    }
  }
  
}
