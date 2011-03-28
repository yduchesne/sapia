/*
 * JythonAdapterLayer.java
 *
 * Created on December 1, 2005, 2:37 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.sapia.soto.jython;

import org.sapia.soto.Layer;

/**
 *
 * @author yduchesne
 */
public class JythonAdapterLayer implements Layer{
  
  /** Creates a new instance of JythonAdapterLayer */
  public JythonAdapterLayer() {
  }
  
  public void init(org.sapia.soto.ServiceMetaData meta) throws Exception {
    JythonAdapter adapter = (JythonAdapter)meta.getService();
    meta.setService(adapter.getPythonObjectInstance());
  }  

  public void start(org.sapia.soto.ServiceMetaData meta) throws Exception {
  }

  public void dispose(){}
  
}
