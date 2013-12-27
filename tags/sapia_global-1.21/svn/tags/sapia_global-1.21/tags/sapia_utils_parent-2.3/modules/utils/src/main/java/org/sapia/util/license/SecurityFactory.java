package org.sapia.util.license;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * This class is a convenient factory of <code>Signature</code> and <code>KeyPairGenerator</code> instances.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class SecurityFactory {
  
  /**
   * This constant identifies the key used to lookup the <code>java.security.Provider</code>
   * class that will be used to create <code>Signature</code> objects.
   * <p>
   * This constant's value is: <code>org.sapia.license.security.provider</code>.
   */
  public static final String SECURITY_PROVIDER_CLASS = "org.sapia.license.security.provider";
  
  private static Provider _defaultProvider;
  
  private Provider _provider;
  
  static{
    String providerClass = System.getProperty(SECURITY_PROVIDER_CLASS);
    if(providerClass != null){
      try{
        _defaultProvider = (Provider)Class.forName(providerClass).newInstance();
      }catch(Throwable t){
        System.err.println("*** Could not load provider: " + providerClass);
        t.printStackTrace(System.err);
      }
    }
  }
  
  /**
   * @param provider the security <code>Provider</code>
   * that this instance should use.
   */
  public void setProvider(Provider provider){
    _provider = provider;
  }
  
  /**
   * @return the security <code>Provider</code> that this instance
   * uses.
   */
  public Provider getProvider(){
    return _provider;
  }
  
  /**
   * This method returns the default security <code>Provider</code> that an instance
   * should use to create <code>Signature</code>s.
   * 
   * @return the default security <code>Provider</code>.
   * 
   * @see #getInstance(String)
   */
  public static Provider getDefaultProvider(){
    return _defaultProvider;
  }
  
  /**
   * Creates a signature object and returns it. If this instance
   * has been assigned a security provider, that provider is used
   * to create the signature; otherwise, this method attempts using the
   * "default" provider (configured through the 
   * <code>org.sapia.license.security.provider</code>
   * system property). If again no such provider has been configured,
   * then one of the providers configured as part of the <code>java.security</code> 
   * file will be used).
   * 
   * @param algo an algorithm identifier.
   * @return a <code>Signature</code>.
   * @throws NoSuchAlgorithmException if no security provider supports
   * the given algorithm.
   */
  public Signature newSignature(String algo) throws NoSuchAlgorithmException{
    if(_provider != null){
      return Signature.getInstance(algo, _provider);
    }
    else if(_defaultProvider != null){
      return Signature.getInstance(algo, _provider);
    }
    return Signature.getInstance(algo);
  }
  
  /**
   * Creates a signature object with the security provider whose
   * identifier is given.
   * 
   * @param algo an algorithm identifier.
   * @param provider a provider identifier.
   * @return a <code>Signature</code>.
   * @throws NoSuchAlgorithmException if no security provider supports
   * the given algorithm.
   * @throws NoSuchProviderException if the desired provider does not exist.
   */
  public Signature newSignature(String algo, String provider) throws NoSuchAlgorithmException, NoSuchProviderException{
    return Signature.getInstance(algo, provider);
  }
  
  /**
   * Creates a key pair generator and returns it. If this instance
   * has been assigned a security provider, that provider is used
   * to create the pair; otherwise, this method attempts using the
   * "default" provider (configured through the 
   * <code>org.sapia.license.security.provider</code>
   * system property). If again no such provider has been configured,
   * then one of the providers configured as part of the <code>java.security</code> 
   * file will be used).
   * 
   * @param algo an algorithm identifier.
   * @return a <code>KeyPairGenerator</code>.
   * @throws NoSuchAlgorithmException if no security provider supports
   * the given algorithm.
   */
  public KeyPairGenerator newKeyPairGenerator(String algo) throws NoSuchAlgorithmException{
    if(_provider != null){
      return KeyPairGenerator.getInstance(algo, _provider);
    }
    else if(_defaultProvider != null){
      return KeyPairGenerator.getInstance(algo, _provider);
    }
    return KeyPairGenerator.getInstance(algo);
  }
  
  /**
   * Creates a key pair generator with the security provider whose
   * identifier is given.
   * 
   * @param algo an algorithm identifier.
   * @param provider a provider identifier.
   * @return a <code>KeyPairGenerator</code>.
   * @throws NoSuchAlgorithmException if no security provider supports
   * the given algorithm.
   * @throws NoSuchProviderException if the desired provider does not exist.
   */
  public KeyPairGenerator newKeyPairGenerator(String algo, String provider) throws NoSuchAlgorithmException, NoSuchProviderException{
    return KeyPairGenerator.getInstance(algo, provider);
  }
  
  public PublicKey generatePublic(byte[] bytes) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException{
   KeyFactory keyFactory;
   if(_provider != null)
     keyFactory = KeyFactory.getInstance("DSA", getProvider());
   else
     keyFactory = KeyFactory.getInstance("DSA");
   
   EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
   return keyFactory.generatePublic(spec);       
  }
  
  public PrivateKey generatePrivate(byte[] bytes) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException{
    KeyFactory keyFactory;
    if(_provider != null)
      keyFactory = KeyFactory.getInstance("DSA", getProvider());
    else
      keyFactory = KeyFactory.getInstance("DSA");
    
    EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
    return keyFactory.generatePrivate(spec);       
   }  


}
