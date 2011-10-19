package org.sapia.soto.state.markup;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.sapia.soto.Settings;

/**
 * This class instantiates {@link org.sapia.soto.state.markup.MarkupSerializer}s based
 * on given configuration settings.
 * 
 * @see #newInstance(Settings)
 * 
 * @author yduchesne
 *
 */
public class MarkupSerializerFactory {
  
  public static final String SETTING_SERIALIZER = "soto.stm.markup.type";
  public static final String SETTING_OUTPUT_NAMESPACE = "soto.stm.markup.output.namespace";  
  public static final String SETTING_OUTPUT_ENCODE_ELEMENTS  = "soto.stm.markup.output.element";  
  
  public static final String SERIALIZER_DEFAULT = "default";  
  
  
  public static final String SERIALIZER_STREAM = "stream";  
  public static final String SERIALIZER_SAX = "sax";
  public static final String SERIALIZER_DOM = "dom";
  
  private static Map SERIALIZERS = Collections.synchronizedMap(new HashMap());
  
  static{
    try{
      SERIALIZERS.put(SERIALIZER_DEFAULT, DefaultSerializer.class.getConstructor(new Class[]{Settings.class}));
      SERIALIZERS.put(SERIALIZER_SAX, SAXSerializer.class.getConstructor(new Class[]{Settings.class}));    
      SERIALIZERS.put(SERIALIZER_STREAM, DefaultSerializer.class.getConstructor(new Class[]{Settings.class}));      
      SERIALIZERS.put(SERIALIZER_DOM, DOMSerializer.class.getConstructor(new Class[]{Settings.class}));      
    }catch(Exception e){
      throw new RuntimeException("Could not initialize", e);
    }
  }
  
  /**
   * Returns <code>true</code> if XML namespace information is to be part of the output.
   * 
   * @param settings the <code>Settings</code> to look up.
   * @param dflt a default value.
   * @return
   * @see #SETTING_OUTPUT_NAMESPACE
   */
  public static boolean isOutputNamespace(Settings settings, boolean dflt){
    return settings.getBoolean(SETTING_OUTPUT_NAMESPACE, dflt);
  }

  /**
   * Returns <code>true</code> if markup attributes must be encoded as elements.
   * 
   * @param settings
   * @param dflt
   * @return
   * @see #SETTING_OUTPUT_ENCODING
   */
  public static boolean isEncodingElements(Settings settings, boolean dflt){
    return settings.getBoolean(SETTING_OUTPUT_ENCODE_ELEMENTS, dflt);
  }
  
  /**
   * This method tries to instantiate a {@link MarkupSerializer} based on the
   * <code>soto.stm.markup.type</code> setting, passed through the 
   * {@link Settings} instance. 
   * <p>
   * This method currently supports the following values for such a:
   * <ul>
   *   <li><b>default</b>: corresponds to {@link DefaultSerializer}.
   *   <li><b>sax</b>: corresponds to {@link SAXSerializer}.
   * </ul>
   * <p>
   * If no value could be found for the setting, a <code>DefaultSerializer</code> is
   * returned.
   * <p>
   * If the setting was specified but is not recognized, an {@link IllegalArgumentException}
   * is thrown.
   * 
   * @param settings some <code>Settings</code>
   * @return a <code>MarkupSerializer</code>
   */
  public static MarkupSerializer newInstance(Settings settings){
    String type = settings.getString(SETTING_SERIALIZER, SERIALIZER_DEFAULT);
    return newInstance(type, settings);
  }
  
  /**
   * Instantiates a serializer corresponding to the given type.
   * 
   * @param type the serializer type.
   * @param settings some <code>Settings</code>.
   * @return a <code>MarkupSerializer</code>
   */
  public static MarkupSerializer newInstance(String type, Settings settings){
    Constructor cons = (Constructor)SERIALIZERS.get(type);
    if(cons == null){
      throw new IllegalArgumentException("No markup serializer found for type: " + type);
    }
    try{
      return (MarkupSerializer)cons.newInstance(new Object[]{settings});
    }catch(Exception e){
      throw new RuntimeException("Could not instantiate markup serializer for type: " + type, e);
    }
    
  }

}
