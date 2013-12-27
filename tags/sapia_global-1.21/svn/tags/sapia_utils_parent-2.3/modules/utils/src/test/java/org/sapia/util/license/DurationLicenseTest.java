package org.sapia.util.license;

import java.util.Date;

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
public class DurationLicenseTest extends TestCase{
  
  public DurationLicenseTest(String name){
    super(name);
  }
  
  public void testIsValidDays() throws Exception{
    DurationLicense license = new DurationLicense(30);
    super.assertTrue("license should be valid", license.isValid(DurationLicense.computeEndDate(new Date(), 29)));
    super.assertTrue("license should be valid", !license.isValid(DurationLicense.computeEndDate(new Date(), 30)));    
    super.assertTrue("license should not be valid", !license.isValid(DurationLicense.computeEndDate(new Date(), 31)));    
  }
  
  public void testIsValidYear() throws Exception{
    DurationLicense license = new DurationLicense(365);
    super.assertTrue("license should be valid", license.isValid(DurationLicense.computeEndDate(new Date(), 364)));
    super.assertTrue("license should be valid", !license.isValid(DurationLicense.computeEndDate(new Date(), 365)));    
    super.assertTrue("license should not be valid", !license.isValid(DurationLicense.computeEndDate(new Date(), 366)));    
  }
  
  public void testIsValidMultiYears() throws Exception{
    DurationLicense license = new DurationLicense(800);
    super.assertTrue("license should be valid", license.isValid(DurationLicense.computeEndDate(new Date(), 799)));
    super.assertTrue("license should be valid", !license.isValid(DurationLicense.computeEndDate(new Date(), 800)));    
    super.assertTrue("license should not be valid", !license.isValid(DurationLicense.computeEndDate(new Date(), 801)));    
  }   

}
