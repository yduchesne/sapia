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
public class TestStep implements Step {
  public boolean exec;
  public boolean success;
  public int execCount;

  public TestStep(boolean success) {
    this.success = success;
  }

  public String getName() {
    return getClass().getName();
  }

  public void execute(Result st) {
    if(!success) {
      st.error("error");
    }
    execCount++;
    exec = true;
  }
}
