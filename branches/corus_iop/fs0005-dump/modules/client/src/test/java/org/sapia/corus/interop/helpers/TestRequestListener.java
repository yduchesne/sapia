package org.sapia.corus.interop.helpers;

import org.sapia.corus.interop.AbstractCommand;
import org.sapia.corus.interop.ConfirmDump;
import org.sapia.corus.interop.ConfirmShutdown;
import org.sapia.corus.interop.Poll;
import org.sapia.corus.interop.Process;
import org.sapia.corus.interop.Restart;
import org.sapia.corus.interop.Status;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class TestRequestListener implements RequestListener {
  boolean confirmShutdown;
  boolean confirmDump;
  boolean poll;
  boolean restart;
  boolean status;

  public void onConfirmShutdown(Process proc, ConfirmShutdown confirm) throws Exception {
    confirmShutdown = true;
  }

  public List<AbstractCommand> onPoll(Process proc, Poll poll) throws Exception {
    this.poll = true;

    return new ArrayList<AbstractCommand>();
  }

  public void onRestart(Process proc, Restart res) throws Exception {
    this.restart = true;
  }

  public List<AbstractCommand> onStatus(Process proc, Status stat) throws Exception {
    this.status = true;

    return new ArrayList<AbstractCommand>();
  }

  @Override
  public void onConfirmDump(Process proc, ConfirmDump confirm) throws Exception {
    confirmDump = true;
  }
}
