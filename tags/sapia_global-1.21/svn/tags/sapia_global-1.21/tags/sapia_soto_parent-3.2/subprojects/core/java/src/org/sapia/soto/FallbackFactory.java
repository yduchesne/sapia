package org.sapia.soto;

import java.io.IOException;
import java.io.InputStream;

import org.sapia.soto.config.NullObjectImpl;
import org.sapia.soto.util.CompositeObjectFactoryEx;
import org.sapia.soto.util.Defs;
import org.sapia.soto.util.Namespace;
import org.sapia.util.xml.ProcessingException;
import org.sapia.util.xml.confix.CreationStatus;
import org.sapia.util.xml.confix.Dom4jProcessor;
import org.sapia.util.xml.confix.NullObject;
import org.sapia.util.xml.confix.ObjectCreationException;
import org.sapia.util.xml.confix.ReflectionFactory;

class FallbackFactory extends CompositeObjectFactoryEx{
  
  static final String SOTO_DEFS = "soto_defs.xml";
  static final String SOTO_PREFIX = "soto";
  static final NullObject NULL = new NullObjectImpl();
  
  private ReflectionFactory  _fac        = new ReflectionFactory(new String[0]);
  
  FallbackFactory(){
    super.setMapToPrefix(true); 
  }
  
  public CreationStatus newObjectFor(String aPrefix, String aNamespaceURI, String anElementName, Object aParent) throws ObjectCreationException {
    if(aPrefix != null && aPrefix.length() > 0){
      if(aPrefix.equals("soto")){
        if(anElementName.equals(SotoApplicationFactory.NAMESPACE)){
          return CreationStatus.create(new Namespace());
        }
        else if(anElementName.equals(SotoApplicationFactory.DEFS)){
          return CreationStatus.create(new Defs(this)); 
        }
        else{
          return super.newObjectFor(aPrefix,aNamespaceURI, anElementName, aParent);
        }
      }
      else{
        return super.newObjectFor(aPrefix, aNamespaceURI, anElementName, aParent);
      }
    }
    else{
      return _fac.newObjectFor(aPrefix,aNamespaceURI, anElementName, aParent);
    }
  }
  
  public void loadSotoDefs() throws RuntimeException{
    Dom4jProcessor proc = new Dom4jProcessor(this);
    InputStream is = getClass().getResourceAsStream(SOTO_DEFS);
    if(is == null){
      throw new IllegalStateException("Default Soto object definitions not found");
    }
    try{
      proc.process(this, is);
    }catch(ProcessingException e){
      throw new RuntimeException("Could not process default Soto object definitions from: " + SOTO_DEFS, e);
    }finally{
      try{
        is.close();
      }catch(IOException e){
        //noop
      }
    }
  }
  
 }
