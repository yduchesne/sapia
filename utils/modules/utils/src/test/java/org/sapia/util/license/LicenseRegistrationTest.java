package org.sapia.util.license;

import java.io.ByteArrayInputStream;
import java.util.Date;

import org.sapia.util.license.test.TestLicense;

import junit.framework.TestCase;

/**
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class LicenseRegistrationTest extends TestCase{
  
  public LicenseRegistrationTest(String name){
    super(name);
  }
  
  public void testRegisterLicense() throws Exception{
    LicenseRegistrationFactory fac = new LicenseRegistrationFactory();
    TestLicense license1 = new TestLicense();
    LicenseRegistration reg = fac.createRegistration("foo", license1, new Date(), new SystemTimeIdFactory());
    super.assertTrue(!reg.getLicenseRecord().isValid(null, fac.getSecurityFactory()));
    TestLicense license2 = new TestLicense();
    license2.valid = true;
    reg.registerNewLicense(license2, new Date(), fac.getSecurityFactory(), new SystemTimeIdFactory());
    super.assertTrue(reg.getLicenseRecord().isValid(null, fac.getSecurityFactory()));    
  }
  
  public void testSerialization() throws Exception{
    TestLicense license = new TestLicense();
    LicenseRegistrationFactory fac = new LicenseRegistrationFactory();    
    license.valid = true;
    LicenseRegistration reg = fac.createRegistration("foo", license, new Date(), new SystemTimeIdFactory());
    super.assertTrue(reg.getLicenseRecord().isValid(null, fac.getSecurityFactory()));
    byte[] data = LicenseUtils.toBytes(reg);
    reg = (LicenseRegistration)LicenseUtils.fromBytes(new ByteArrayInputStream(data));
    super.assertTrue(reg.getLicenseRecord().isValid(null, fac.getSecurityFactory()));    
  }  

}
