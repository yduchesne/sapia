package org.sapia.archie.jndi;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

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
public class DefaultContextFactoryTest extends TestCase{
	
	public DefaultContextFactoryTest(String name){
		super(name);
	}
	
	public void testCreate() throws Exception{
		Properties props = new Properties();
		props.setProperty(Context.INITIAL_CONTEXT_FACTORY, DefaultContextFactory.class.getName());
		InitialContext ctx = new InitialContext(props);
	}

}
