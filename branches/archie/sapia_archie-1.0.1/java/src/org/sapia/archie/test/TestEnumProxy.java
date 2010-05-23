package org.sapia.archie.test;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.sapia.archie.jndi.proxy.EnumProxy;

/**
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class TestEnumProxy extends EnumProxy{
	
	public TestEnumProxy(Name parentContextName, NamingEnumeration enumeration){
		super(parentContextName, enumeration);
	}
	
	public Object onNext(Name contextName, Object next) throws NamingException{
  	if(next instanceof Binding && ((Binding)next).getObject() instanceof Context){
			Binding b = (Binding)next;
			b.setObject(new TestContextProxy((Context)b.getObject()));
			return b;
		}
		else if(next instanceof Context){
			return new TestContextProxy((Context)next);
		}
		else{
			return next;		
		}
	}
}
