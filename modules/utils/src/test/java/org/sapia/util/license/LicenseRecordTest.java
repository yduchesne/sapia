package org.sapia.util.license;

import java.io.ByteArrayInputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;

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
public class LicenseRecordTest extends TestCase{
  
  public LicenseRecordTest(String name){
    super(name);
  }
  
  public void testIsValid() throws Exception{
    TestLicense lic = new TestLicense();
    lic.valid = true;
    SecurityFactory fac = new SecurityFactory();
    KeyPairGenerator kpGen = fac.newKeyPairGenerator("DSA");
    kpGen.initialize(1024, new SecureRandom());
    KeyPair kp = kpGen.generateKeyPair();
    LicenseRecord rec = LicenseRecord.newInstance("test", lic, kp.getPublic(), kp.getPrivate(), fac, new SystemTimeIdFactory());
    super.assertTrue(rec.isValid(null, fac));
    byte[] bytes = LicenseUtils.toBytes(rec);
    rec = (LicenseRecord)LicenseUtils.fromBytes(new ByteArrayInputStream(bytes));
    super.assertTrue(rec.isValid(null, fac));
    super.assertTrue(!rec.update(rec, null));    
    DurationLicense license = new DurationLicense(1000);
    LicenseRecord rec2 = LicenseRecord.newInstance("test", lic, kp.getPublic(), kp.getPrivate(), fac, new SystemTimeIdFactory());
    super.assertTrue(rec.update(rec2, null));    
  }
}
