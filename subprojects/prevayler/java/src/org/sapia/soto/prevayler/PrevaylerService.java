package org.sapia.soto.prevayler;

import java.io.IOException;

import org.prevayler.Clock;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.Query;
import org.prevayler.Transaction;
import org.prevayler.TransactionWithQuery;
import org.sapia.soto.Service;

/**
 * This class implements the <code>Prevayler</code> interface. An instance of
 * this class in fact encapsulates a <code>PrevaylerFactory</code>, that is
 * internally used to create a <code>Prevayler</code> to which the instance
 * will delegate its calls. An instance of this class can be configured through
 * the provided accessors (that in fact delegate their calls to the underlying
 * Prevaler factory). In addition, this class can be extended; inheriting
 * classes are given access to the internal <code>PrevaylerFactory</code>
 * through a protected method - to allow further configuring the factory.
 * <p>
 * The encapsulated <code>Prevayler</code> is created when an instance of this
 * class has its <code>init()</code> method called.
 * <p>
 * <b>IMPORTANT: </b> an instance of this class MUST at least have a prevalent
 * system object specified (see the <code>setPrevalentSystem()</code> method).
 * <p>
 * For more info, see the <a href="http://www.prevayler.org">Prevayler </a> web
 * site (appropriate documentation is contained in the Prevayler distribution).
 * 
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class PrevaylerService implements Prevayler, Service {

  private PrevaylerFactory _fac = new PrevaylerFactory();
  private Prevayler        _prevayler;

  ///////////////////////////////// INSTANCE METHODS
  // /////////////////////////////////

  /**
   * @param clock
   *          sets the <code>Clock</code> instance to use.
   */
  public void setClock(Clock clock) {
    _fac.configureClock(clock);
  }

  /**
   * This method <code>MUST</code> be called.
   * 
   * @param prevalentSystem
   *          an "empty" prevaltent system used at first startup.
   */
  public void setPrevalentSystem(Object prevalentSystem) {
    _fac.configurePrevalentSystem(prevalentSystem);
  }

  /**
   * @param intervalMillis
   *          the time interval at which snapshot is taken - in millis.
   */
  public void setTxSnapshotInterval(long intervalMillis) {
    _fac.configureTransactionLogFileAgeThreshold(intervalMillis);
  }

  /**
   * @param logFileByteSize
   *          the maximum size of the transaction log - if size is reached, a
   *          snapshot occurs.
   */
  public void setTxSnapshotSize(long logFileByteSize) {
    _fac.configureTransactionLogFileSizeThreshold(logFileByteSize);
  }

  /**
   * @param isTransient
   *          if <code>true</code>, indicates that this instance should
   *          operate in transient mode.
   */
  public void setTransient(boolean isTransient) {
    _fac.configureTransientMode(isTransient);
  }

  /**
   * @param prevaylerBase
   *          the base directory of the Prevayler instance.
   */
  public void setBaseDir(String prevaylerBase) {
    _fac.configurePrevalenceBase(prevaylerBase);
  }

  ///////////////////////////////// PREVAYLER METHODS
  // /////////////////////////////////

  /**
   * @see org.prevayler.Prevayler#clock()
   */
  public Clock clock() {
    return _prevayler.clock();
  }

  /**
   * @see org.prevayler.Prevayler#close()
   */
  public void close() throws IOException {
    _prevayler.close();
  }

  /**
   * @see org.prevayler.Prevayler#execute(org.prevayler.Query)
   */
  public Object execute(Query query) throws Exception {
    return _prevayler.execute(query);
  }

  /**
   * @see org.prevayler.Prevayler#execute(org.prevayler.Transaction)
   */
  public void execute(Transaction tx) {
    _prevayler.execute(tx);
  }

  /**
   * @see org.prevayler.Prevayler#execute(org.prevayler.TransactionWithQuery)
   */
  public Object execute(TransactionWithQuery txq) throws Exception {
    return _prevayler.execute(txq);
  }

  /**
   * @see org.prevayler.Prevayler#prevalentSystem()
   */
  public Object prevalentSystem() {
    return _prevayler.prevalentSystem();
  }

  /**
   * @see org.prevayler.Prevayler#takeSnapshot()
   */
  public void takeSnapshot() throws IOException {
    _prevayler.takeSnapshot();
  }

  ///////////////////////////////// SOTO SERVICE METHODS
  // /////////////////////////////////

  /**
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {
    _prevayler = _fac.create();
  }

  /**
   * @see org.sapia.soto.Service#start()
   */
  public void start() throws Exception {
  }

  /**
   * @see org.sapia.soto.Service#dispose()
   */
  public void dispose() {
    try {
      _prevayler.close();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * @return the <code>PrevaylerFactory</code> that this instance
   *         encapsulates.
   */
  protected PrevaylerFactory getFactory() {
    return _fac;
  }
}
