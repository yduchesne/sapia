package org.sapia.ubik.rmi.naming.remote;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.naming.NameParser;

import junit.framework.TestCase;

import org.sapia.archie.impl.DefaultNameParser;
import org.sapia.archie.jndi.JndiNameParser;
import org.sapia.ubik.rmi.naming.remote.proxy.BindingCache;


/**
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class BindingCacheTest extends TestCase {
  
  NameParser parser;
  
  public BindingCacheTest(String arg0) {
    super(arg0);
  }
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    parser = new JndiNameParser(new DefaultNameParser());
  }

  public void testAdd() throws Exception{
    BindingCache bc = new BindingCache();
    Object       o;
    bc.add("junit", parser.parse("someObject"), new Object());
    super.assertEquals(1, bc.cachedRefs().size());
  }

  public void testSerializeNotNull() throws Exception {
    BindingCache bc = new BindingCache();
    Object       o;
    bc.add("junit", parser.parse("someObject"), "theObject");

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream    ous = new ObjectOutputStream(bos);
    ous.writeObject(bc);
    ous.flush();
    ous.close();

    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
          bos.toByteArray()));
    bc = (BindingCache) ois.readObject();
    super.assertEquals(1, bc.cachedRefs().size());

    BindingCache.BoundRef ref = (BindingCache.BoundRef) bc.cachedRefs().get(0);
    super.assertEquals(parser.parse("someObject"), ref.name);
    super.assertEquals("theObject", ref.obj);
  }

  public void testSerializeNull() throws Exception {
    BindingCache bc = new BindingCache();
    Object       o;
    bc.add("junit", parser.parse("someObject"), null);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream    ous = new ObjectOutputStream(bos);
    ous.writeObject(bc);
    ous.flush();
    ous.close();

    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
          bos.toByteArray()));
    bc = (BindingCache) ois.readObject();
    super.assertEquals(0, bc.cachedRefs().size());
  }
}