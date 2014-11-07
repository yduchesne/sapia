package org.sapia.corus.interop;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface that defines primitives for encoding and decoding interop commands.
 * 
 * @author jcdesrochers
 */
public interface InteropCommandCodec {

  public void encode(Object o, OutputStream anOutput) throws Exception;
  
  public Object decode(InputStream anInput) throws Exception;
  
}
