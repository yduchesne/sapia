package org.sapia.archie.jndi.proxy;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NamingEnumeration;

import org.sapia.archie.impl.DefaultNode;
import org.sapia.archie.jndi.JndiContext;

import junit.framework.TestCase;

/**
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class ProxyTest extends TestCase {
	
	Context _root;
	
  /**
   * @param arg0
   */
  public ProxyTest(String arg0) {
    super(arg0);
  }
  
	public void setUp() throws Exception{
		_root = new JndiContext(new DefaultNode());
		_root.bind("path1/object1", "Object1");
		_root.bind("path1/object2", "Object2");  	
		_root.bind("path1/path2/object3", "Object3");		
		_root.bind("path1/path3/object4", "Object4");		
	}
  
  public void testListBindings() throws Exception{
  	Context ctx = new TestContextProxy(_root);
  	
  	NamingEnumeration enumeration = (TestEnumProxy)ctx.listBindings("/");
  	int count = 0;
  	while(enumeration.hasMore()){
  		Binding b = (Binding)enumeration.next();
  		super.assertEquals("path1", b.getName());
  		TestContextProxy child = (TestContextProxy)b.getObject();
  		TestEnumProxy childEnum = (TestEnumProxy)child.listBindings("");
  		while(childEnum.hasMore()){
  			b = (Binding)childEnum.next();
				super.assertTrue(
				  b.getName().equals("path2") || 
				  b.getName().equals("path3") ||
				  b.getName().equals("object1") ||
				  b.getName().equals("object2"));
				if(b.getName().startsWith("path")){
					TestContextProxy proxy = (TestContextProxy)b.getObject();
				}
			  
	 	  }
  	}
  }
  
  public void testCreateSubContext() throws Exception{
		Context ctx = new TestContextProxy(_root);  	
  	TestContextProxy child = (TestContextProxy)ctx.createSubcontext("path1/path4");
		child = (TestContextProxy)ctx.createSubcontext("path1/path3");  	
  }
  
  public void testList() throws Exception{
		Context ctx = new TestContextProxy(_root);
  	
		NamingEnumeration enumeration = (TestEnumProxy)ctx.listBindings("/");
		int count = 0;
		while(enumeration.hasMore()){
			Object b = enumeration.next();
			
			
			super.assertEquals("/path1", ((Context)((Binding)b).getObject()).getNameInNamespace());
			TestContextProxy child = (TestContextProxy)((Binding)b).getObject();
			TestEnumProxy childEnum = (TestEnumProxy)child.listBindings("");
			while(childEnum.hasMore()){
				Object nextChild = childEnum.next();
				if(nextChild instanceof Context){
					TestContextProxy childContext = (TestContextProxy)childEnum.next();
					super.assertTrue(
					  childContext.getNameInNamespace().equals("/path1/path2") || 
					  childContext.getNameInNamespace().equals("/path1/path3"));
				}
			}
		}
  	
  }
  

}
