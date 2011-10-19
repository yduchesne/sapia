package org.sapia.soto.state;

/**
 * @author Yanick Duchesne
 *         <dl>
 *         <dt><b>Copyright: </b>
 *         <dd>Copyright &#169; 2002-2003 <a
 *         href="http://www.sapia-oss.org">Sapia Open Source Software </a>. All
 *         Rights Reserved.</dd>
 *         </dt>
 *         <dt><b>License: </b>
 *         <dd>Read the license.txt file of the jar or visit the <a
 *         href="http://www.sapia-oss.org/license.html">license page </a> at the
 *         Sapia OSS web site</dd>
 *         </dt>
 *         </dl>
 */
public class TestErrFlow implements State {
  private String  _id;
  private boolean _handle;

  public TestErrFlow(String id, boolean handle) {
    _id = id;
    _handle = handle;
  }

  public String getId() {
    return _id;
  }

  public void execute(Result st) {
    st.error("error !!!");

    if(_handle) {
      st.handleError();
    }
  }
}
