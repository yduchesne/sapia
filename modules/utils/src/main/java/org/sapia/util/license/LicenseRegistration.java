package org.sapia.util.license;

import java.io.IOException;
import java.io.Serializable;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.sapia.util.CompositeRuntimeException;

/**
 * A license registration holds all data pertaining to a given license. An instance of this class internally keeps a <code>LicenseRecord</code>
 * that should normally be given to a specific licensee. It also holds the <code>KeyPair</code> (private and public keys) that belongs to the
 * licensee. It is normally kept at the licensing party, and associated to some licensee account (typically, an instance of this class could be
 * kept in a database, as a blob).
 * <p>
 * An instance of this class can be used to acquire the license corresponding to a licensee, or to create a new license for that licensee.
 * Typically, applications will create a new license for a licensee programmatically, and send that license electronically (normally by email)
 * to that licensee. 
 * 
 * @author Yanick Duchesne
 * 
 * @see org.sapia.util.license.LicenseRegistrationFactory
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class LicenseRegistration implements Serializable{
  
  static final long serialVersionUID = 1L;
  
  private LicenseRecord _license;
  private transient PublicKey _pub;
  private transient PrivateKey _priv;
  private byte[] _pubBytes, _privBytes;
  private Map _data;
  private Date _lastUpdate;
  private Object _vendorId;
  
  LicenseRegistration(Object vendorId, LicenseRecord license, KeyPair keys, Map data, Date createDate){
    _vendorId = vendorId;
    _license = license;
    _pubBytes = keys.getPublic().getEncoded();
    _privBytes = keys.getPrivate().getEncoded();
    _data = data;
    _lastUpdate = createDate;
  }
  
  LicenseRegistration(Object vendorId, LicenseRecord license, KeyPair keys, Date createDate){
    this(vendorId, license, keys, new HashMap(), createDate);  
  }
  
  /**
   * @return the <code>PublicKey</code> that this instance holds.
   */
  public PublicKey getPublic(SecurityFactory fac){
    initKeys(fac);
    return _pub;
  }
  
  /**
   * @return the <code>PrivateKey</code> that this instance holds.
   */  
  public PrivateKey getPrivateKey(SecurityFactory fac){
    initKeys(fac);    
    return _priv;
  }
  
  /**
   * @return the <code>Date</code> at which this instance was created, or
   * at which its encpasulated license was modified.
   * 
   * @see #registerNewLicense(License)
   */
  public Date getLastUpdate(){
    return _lastUpdate;
  }
  
  /**
   * @return the <code>LicenseRecord</code> that this instance holds.
   */
  public LicenseRecord getLicenseRecord(){
    return _license;
  }
  
  /**
   * @param name a binding name.
   * @return the <code>Object</code> for the given name.
   */
  public Object getBinding(String name){
    if(_data == null)
      return null;
    return _data.get(name);
  }
  
  /**
   * @param license a <code>License</code> object.
   * @param currentDate a <code>Date</code> object representing the current date.
   * @param factory  the <code>SignatureFactory</code> internally used to sign the license's bytes.
   * @param idFactory the <code>LicenseIdFactory</code> internally used to create the license identifier. 
   * @throws IOException if an IO problem occurs while registering the license.
   * @throws Exception if a low level problem occurs.
   */
  public void registerNewLicense(License license, Date currentDate, SecurityFactory factory, LicenseIdFactory idFactory) throws IOException, Exception{
    _lastUpdate = currentDate;
    initKeys(factory);
    _license.registerNewLicense(_vendorId, license, _pub, _priv, factory, idFactory);
  }
  
  private void initKeys(SecurityFactory fac){
    if(_pub == null || _priv == null){
      try{
        _pub  = (PublicKey)fac.generatePublic(_pubBytes);
        _priv = (PrivateKey)fac.generatePrivate(_privBytes);
      }catch(Exception e){
        throw new CompositeRuntimeException("Could not generate key from bytes", e);
      }      
    }
  }

}
