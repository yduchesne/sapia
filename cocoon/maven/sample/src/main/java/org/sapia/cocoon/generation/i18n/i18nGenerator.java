package org.sapia.cocoon.generation.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.ResourceNotFoundException;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.generation.Generator;
import org.apache.cocoon.xml.XMLConsumer;
import org.apache.excalibur.source.SourceValidity;
import org.sapia.cocoon.i18n.BaseI18nComponent;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


/**
 * This generator parses XML resources whose names can optionally hold internationalization information
 * (language, country, variant). The generator allows retrieving files based on given locales, as specified below:
 * 
 * <ul>
 *   <li>a locale parameter may be passed to an instance of this generator (in the sitemap configuration). This indicates
 *   to the generator for which locale a resource should preferrably be retrieved.
 *   <li>locale information may be passed as part of the resource name. In this case also, the resource that most closely 
 *   matches the thus passed locale will be parsed.   
 * </ul>
 * 
 * This generator searches resources using the algorithm explained below (resource search stops as soon as one is found):
 * 
 * <ul>
 *   <li>Given a resource named <code>site/index.html</code>, and the <code>en_US</code> locale passed as a sitemap parameter, this generator will first search for <code>site/index_en_US.html</code>,
 *   then <code>site/index_en.html</code>. As soon as one of the above is found, search stops, and the resource is parsed.
 *   <li>If no resource was found through the above method, then the following are searched: <code>site/_en/index_US.html</code>, <code>site/_en/index.html</code>. If one of these
 *   resources is found, search stops and generation completes (the resource is parsed). 
 *   <li>If still no resource was found, the generator attempts finding <code>site/index.html</code>. If that resource is found, generation completes. Otherwise, at this
 *   point, a {@link ResourceNotFoundException} is thrown.
 * </ul>
 * 
 * Note that the locale may also be passed in the name of the resource. For example, let's say that the <code>site/index_en_US.html</code>
 * resource name is passed; this generator will extract the locale information that in the URI. It will thus search for  
 * <code>site/index.html</code> "under" the <code>en_US</code> locale, using the same algorithm as above. If locale information is passed in
 * the URI in this way, it overrides locale information that is passed as a sitemap parameter.
 *
 * <p>
 * <b>Example Spring configuration:</b>
 * </p>
 * <pre>
 *    &lt;bean name="org.apache.cocoon.generation.Generator/i18n"
 *       class="org.sapia.cocoon.generation.i18n.i18nGenerator" 
 *       scope="prototype" /&gt;   
 * </pre>  
 * <b>Example Sitemap configuration:</b>
 * </p>
 * <pre>
 *      &lt;map:match pattern="**.html">
 *        &lt;map:generate src="resource/content/{1}.xhtml" type="i18n"&gt;
 *           &lt;map:parameter name="locale" value="{state:user.locale}" /&gt;
 *         &lt;/map:generate&gt;
 *         &lt;map:serialize type="html" /&gt;
 *     &lt;/map:match&gt;
 * </pre>
 * @author yduchesne
 *
 */
public class i18nGenerator extends BaseI18nComponent implements Generator, CacheableProcessingComponent{
  
  private XMLConsumer consumer;
  
  public void setConsumer(XMLConsumer consumer) {
    this.consumer = consumer;
  }

  public void setup(SourceResolver resolver, Map model, String src, Parameters params) throws ProcessingException, SAXException, IOException {
    super.setup(resolver, objectModel, src, params);
  }
  
  public void generate() throws IOException, SAXException, ProcessingException {
    XMLReader reader = XMLReaderFactory.createXMLReader();
    reader.setContentHandler(consumer);
    InputStream is = null;
    try{
      reader.parse(new InputSource(is = key.getSource().getInputStream()));
    }finally{
      if(is != null){
        is.close();
      }
    }
  }
  
  /////////// Cacheable contract
  
  public Serializable getKey() {
    return key;
  }
  
  public SourceValidity getValidity() {
    return key.getValidity();
  }  
}
