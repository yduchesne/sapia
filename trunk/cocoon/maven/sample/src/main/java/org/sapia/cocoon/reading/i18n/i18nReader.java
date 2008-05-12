package org.sapia.cocoon.reading.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.environment.Context;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Response;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.reading.Reader;
import org.apache.excalibur.source.SourceValidity;
import org.sapia.cocoon.generation.i18n.i18nGenerator;
import org.sapia.cocoon.i18n.BaseI18nComponent;
import org.xml.sax.SAXException;

/**
 * A reader that output internationalized resources. It uses an algorithm
 * that is identical to the one of {@link i18nGenerator}.
 * 
 * <p>
 * <b>Example Spring configuration:</b>
 * </p>
 * <pre>
 *    &lt;bean name="org.apache.cocoon.reading.Reader/i18n"
 *       class="org.sapia.cocoon.reading.i18n.i18nReader" 
 *       scope="prototype" /&gt;   
 * </pre>  
 * 
 * <b>Example Sitemap configuration:</b>
 * </p>
 * <pre>
 *      &lt;map:match pattern="**.png"&gt;
 *        &lt;map:read src="resource/content/{1}.png" type="i18n"&gt;
 *           &lt;map:parameter name="locale" value="{state:user.locale}" /&gt;
 *         &lt;/map:read&gt;
 *     &lt;/map:match&gt;
 * </pre>
 * 
 * @see i18nGenerator
 * 
 * @author yduchesne
 *
 */
public class i18nReader extends BaseI18nComponent implements Reader, CacheableProcessingComponent{
  
  private OutputStream out;
  
  public void setOutputStream(OutputStream out) throws IOException {
    this.out = out;
    
  }
  
  public boolean shouldSetContentLength() {
    return false;
  }
  
  public void setup(SourceResolver resolver, Map model, String src, Parameters params) throws ProcessingException, SAXException, IOException {
    super.setup(resolver, model, src, params);
  }
  
  public void generate() throws IOException, SAXException, ProcessingException {
    Response res = ObjectModelHelper.getResponse(this.objectModel);
    res.setContentLength((int)key.getSource().getContentLength());
    byte[] buffer = new byte[2048];
    InputStream is = key.getSource().getInputStream();
    try{
      int numRead = 0;
      
      while((numRead = is.read(buffer)) > -1){
        out.write(buffer, 0, numRead);
        out.flush();
      }
    }finally{
      is.close();
    }
  }
  
  public long getLastModified() {
    if(key.getSource() != null){
      return key.getSource().getLastModified();
    }
    else{
      return 0;
    }
  }
  
  public String getMimeType() {
    Context ctx = ObjectModelHelper.getContext(objectModel);
    if (ctx != null) {
      String mimeType = ctx.getMimeType(key.getSrc());
      if (mimeType != null) {
        return mimeType;
      }
    }

    if (key.getSource() != null) {
      return key.getSource().getMimeType();
    } else {
      return null;
    }
  }  
  
  /////////// Cacheable contract
  
  public Serializable getKey() {
    return this.key;
  }
  
  public SourceValidity getValidity() {
    return key.getValidity();
  }  

}
