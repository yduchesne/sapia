package org.sapia.ubik.mcast;

/**
 * @author Yanick Duchesne
 *         <dl>
 *         <dt><b>Copyright:</b>
 *         <dd>Copyright &#169; 2002-2003 <a
 *         href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All
 *         Rights Reserved.</dd></dt>
 *         <dt><b>License:</b>
 *         <dd>Read the license.txt file of the jar or visit the <a
 *         href="http://www.sapia-oss.org/license.html">license page</a> at the
 *         Sapia OSS web site</dd></dt>
 *         </dl>
 */
public class TestEventListener implements AsyncEventListener, SyncEventListener {
  boolean called;

  /**
   * Constructor for TestEventListener.
   */
  public TestEventListener() {
    super();
  }

  /**
   * @see org.sapia.ubik.mcast.EventListener#onEvent(MulticastEvent)
   */
  public void onAsyncEvent(RemoteEvent evt) {
    called = true;
  }

  void reset() {
    called = false;
  }

  public Object onSyncEvent(RemoteEvent evt) {
    return "test";
  }
}
