package org.sapia.archie.test;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.sapia.archie.jndi.proxy.ContextProxy;

/**
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class TestContextProxy extends ContextProxy{
	
	public TestContextProxy(Context ctx) throws NamingException{
		super(ctx);
	}
	
	public Context onSubContext(Name name, Context ctx) throws NamingException{
		return new TestContextProxy(ctx);
	}
	
	public NamingEnumeration onEnum(Name n, NamingEnumeration enumeration) throws NamingException{
		return new TestEnumProxy(n, enumeration);
	}
	
	

}
