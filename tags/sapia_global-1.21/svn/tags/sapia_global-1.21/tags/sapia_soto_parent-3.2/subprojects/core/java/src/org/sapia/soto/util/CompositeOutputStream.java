package org.sapia.soto.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * An instance of this class writes data to multiple streams at once.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2005 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class CompositeOutputStream extends OutputStream{
  
  private List _outputs = new ArrayList();
  
  public void addOutputStream(OutputStream out){
    _outputs.add(out);
  }
  
  public void close() throws IOException {
    for(int i = 0; i < _outputs.size(); i++){
      ((OutputStream)_outputs.get(i)).close();
    }
  }
  public void flush() throws IOException {
    for(int i = 0; i < _outputs.size(); i++){
      ((OutputStream)_outputs.get(i)).flush();
    }
  }
  public void write(byte[] b, int off, int len) throws IOException {
    for(int i = 0; i < _outputs.size(); i++){
      ((OutputStream)_outputs.get(i)).write(b,off,len);
    }
  }
  public void write(byte[] b) throws IOException {
    for(int i = 0; i < _outputs.size(); i++){
      ((OutputStream)_outputs.get(i)).write(b);
    }
  }
  public void write(int b) throws IOException {
    for(int i = 0; i < _outputs.size(); i++){
      ((OutputStream)_outputs.get(i)).write(b);
    }
  }
}
