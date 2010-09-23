package org.sapia.util.license;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;

/**
 * This factory is used to create <code>LicenseRegistration</code>s. The created
 * registrations are handled by client applications. Registrations should normally be kept in a
 * database, on a per-client basis, on the vendor's side.
 * <p>
 * The code snippet below illlustrates how an instance of this class can be used:
 * <pre>
 * DurationLicense     license      = new DurationLicense(new Date(), 30);
 * LicenseRegistration registration = LicenseRegistrationFactory.createRegistration(someVendorIdentifier, license);
 * 
 * // ... here save registration as blob in database ...
 * 
 * // the license record below is normally sent to the licensee (for example, it can be sent
 * // by mail, as a serialized object (in an attached file), or in some form of proprietary
 * // marshalling format. A LicenseRecord is normally processed within application code at the client,
 * // to enforce usage rights.
 * LicenseRecord       toSend = registration.getLicenseRecord();
 * 
 * </pre> 
 * <p>
 * Note that an instance of this class keeps an application-specific vendor identifier. That identifier is also encapsulated
 * within license records sent to clients. The vendor identifier is kept on the client side to make sure that eventual licenses
 * on the client side are eventually updated with licenses coming from the original vendor.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class LicenseRegistrationFactory {
  
  private SecurityFactory _fac;
  
  public LicenseRegistrationFactory(SecurityFactory fac){
    _fac = fac;
  }
  
  public LicenseRegistrationFactory(){
    this(new SecurityFactory());
  }
  
  /**
   * @return the <code>SecurityFactory</code> that this instance uses.
   */
  public SecurityFactory getSecurityFactory(){
    return _fac;
  }
  
  /**
   * Creates a <code>LicenseRegistration</code> and returns it.
   * @param serializableVendorId the identifier of the vendor that emits the <code>License</code>.
   * @param license a <code>License</code> object.
   * @param data an <code>Map</code> of arbitrary serializable bindings kept within the returned registration.
   * @param currentDate a <code>Date</code> object representing the current date.
   * @param idFactory the <code>LicenseIdFactory</code> internally used to create the license identifier.  
   * @return a <code>LicenseRegistration</code>.
   * @throws IOException if an IO problem occurs while creating the registration.
   * @throws Exception if some low-level problem occurs.
   */
  public LicenseRegistration createRegistration(Object serializableVendorId, License license, Date currentDate, LicenseIdFactory idFactory) throws IOException, Exception{
    return createRegistration(serializableVendorId, 1024, license, currentDate, idFactory);
  }
  
  /**
   * Creates a <code>LicenseRegistration</code> and returns it.
   * @param serializableVendorId the identifier of the vendor that emits the <code>License</code>.
   * @param license a <code>License</code> object.
   * @param serializableData an <code>Map</code> of arbitrary serializable bindings kept within the returned registration.
   * @param currentDate a <code>Date</code> object representing the current date.
   * @param idFactory the <code>LicenseIdFactory</code> internally used to create the license identifier.
   * @return a <code>LicenseRegistration</code>.
   * @throws IOException if an IO problem occurs while creating the registration.
   * @throws Exception if some low-level problem occurs.
   */  
  public LicenseRegistration createRegistration(Object serializableVendorId, 
                                                License license, 
                                                Map serializableData, 
                                                Date currentDate,
                                                LicenseIdFactory idFactory) throws IOException, Exception{
    return createRegistration(serializableVendorId, 1024, license, serializableData, currentDate, idFactory);
  }  
  
  /**
   * Creates a <code>LicenseRegistration</code> and returns it.
   * 
   * @param serializableVendorId the identifier of the vendor that emits the <code>License</code>.
   * @param license a <code>License</code> object.
   * @param currentDate a <code>Date</code> object representing the current date.
   * @param idFactory the <code>LicenseIdFactory</code> internally used to create the license identifier. 
   * @return a <code>LicenseRegistration</code>.
   * @throws IOException if an IO problem occurs while creating the registration.
   * @throws Exception if some low-level problem occurs.
   */
  public LicenseRegistration createRegistration(Object serializableVendorId, 
                                                int strength, 
                                                License license, 
                                                Date currentDate,
                                                LicenseIdFactory idFactory) throws IOException, Exception{
    return createRegistration(serializableVendorId, strength, license, null, currentDate, idFactory);
  }
  
  /**
   * Creates a <code>LicenseRegistration</code> and returns it.
   * @param strength the "strength" (number of bytes) of the internal encryption keys.
   * @param serializableVendorId the identifier of the vendor that emits the <code>License</code>.
   * @param license a <code>License</code>
   * @param serializableData an <code>Map</code> of arbitrary serializable bindings kept within the returned registration.
   * @param currentDate a <code>Date</code> object representing the current date.
   * @param idFactory the <code>LicenseIdFactory</code> internally used to create the license identifier.
   * @return a <code>LicenseRegistration</code>
   * @throws IOException if an IO problem occurs while creating the registration.
   * @throws Exception if some low-level problem occurs.
   */
  public LicenseRegistration createRegistration(Object serializableVendorId, 
                                                int strength, 
                                                License license, 
                                                Map serializableData, 
                                                Date currentDate,
                                                LicenseIdFactory idFactory) throws IOException, Exception{
    KeyPairGenerator kpGen = _fac.newKeyPairGenerator("DSA");
    kpGen.initialize(strength, new SecureRandom());
    KeyPair kp = kpGen.generateKeyPair();
    LicenseRecord rec = LicenseRecord.newInstance(serializableVendorId, license, kp.getPublic(), kp.getPrivate(), _fac, idFactory);
    return new LicenseRegistration(serializableVendorId, rec, kp, serializableData, currentDate);
  }  
  
}
