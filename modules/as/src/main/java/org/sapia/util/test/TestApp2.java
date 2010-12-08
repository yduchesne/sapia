package org.sapia.util.test;

/**
 *
 *
 * @author Jean-Cedric Desrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class TestApp2 {
  public static void main(String[] args) {
    long aStart = System.currentTimeMillis();
    try {
      StringBuffer aBuffer = new StringBuffer();
      aBuffer.append(System.currentTimeMillis()-aStart).append(" [").
              append(Thread.currentThread().getName()).append("] ").
              append("Excuting the TestApp2.main() method");
      System.out.println(aBuffer.toString());

      aBuffer = new StringBuffer();
      aBuffer.append(System.currentTimeMillis()-aStart).append(" [").
              append(Thread.currentThread().getName()).append("] ").
              append("With the arguments: ");
      if (args.length == 0) {
        aBuffer.append("[]");
      } else {
        for (int i = 0 ; i < args.length; i++) {
          aBuffer.append("[").append(args[i]).append("] ");
        }
      }
      System.out.println(aBuffer.toString());

      aBuffer = new StringBuffer();
      aBuffer.append(System.currentTimeMillis()-aStart).append(" [").
              append(Thread.currentThread().getName()).append("] ").
              append("TestApp2 was loaded from the classloader ").append(TestApp2.class.getClassLoader());
      System.out.println(aBuffer.toString());

      aBuffer = new StringBuffer();
      aBuffer.append(System.currentTimeMillis()-aStart).append(" [").
              append(Thread.currentThread().getName()).append("] ").
              append("The context class loader of the thread is ").append(Thread.currentThread().getContextClassLoader());
      System.out.println(aBuffer.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}