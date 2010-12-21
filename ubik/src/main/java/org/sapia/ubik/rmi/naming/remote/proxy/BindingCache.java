package org.sapia.ubik.rmi.naming.remote.proxy;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;

import org.sapia.ubik.mcast.DomainName;
import org.sapia.ubik.rmi.naming.remote.StubTweaker;
import org.sapia.ubik.rmi.server.Stub;
import org.sapia.ubik.rmi.server.StubInvocationHandler;


/**
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class BindingCache implements Externalizable {
  
  private List<BoundRef> _services = new CopyOnWriteArrayList<BoundRef>();

  public BindingCache() {
  }

  public synchronized void add(String domainName, Name name, Object o) {
    _services.add(new BoundRef(domainName, name, o));
  }

  public synchronized List<BoundRef> cachedRefs() {
    return _services;
  }

  public void copyTo(Context ctx, DomainName domain, String baseUrl, String mcastAddress, int mcastPort) {
    BoundRef ref;

    for (int i = 0; i < _services.size(); i++) {
      ref = (BoundRef) _services.get(i);

      if (ref.obj == null) {
        continue;
      }

      try {
        if ((ref.domainName != null) && domain.contains(ref.domainName)) {
          Object toBind;
          if(ref.obj instanceof SoftReference<?>){
            toBind = ((SoftReference<?>)ref.obj).get();
          }
          else{
            toBind = ref.obj;
          }
          if(toBind != null){
            toBind = StubTweaker.tweak(baseUrl, 
                ref.name, 
                ref.domainName, 
                mcastAddress, 
                mcastPort, 
                toBind);
            ctx.rebind(ref.name, toBind);
          }
        }
      } catch (NamingException e) {
        //noop;
      }
    }
  }

  @SuppressWarnings(value="unchecked")
  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException {
    _services = (List<BoundRef>) in.readObject();
  }

  public void writeExternal(ObjectOutput out) throws IOException {
    BoundRef ref;

    synchronized (_services) {
      for (int i = 0; i < _services.size(); i++) {
        ref = (BoundRef) _services.get(i);

        if (ref.isNull()) {
          _services.remove(i);
          --i;
        }
      }
    }

    out.writeObject(_services);
  }

  public static class BoundRef implements Externalizable {
    public Name     name;
    public Object     obj;
    public DomainName domainName;

    /**
     * Do not use this constructor; meant for externalization.
     */
    public BoundRef() {
    }

    BoundRef(String domainName, Name name, Object o) {
      this.domainName   = DomainName.parse(domainName);
      this.name         = name;
      this.obj          = new SoftReference<Object>(o);
    }

    public void readExternal(ObjectInput in)
      throws IOException, ClassNotFoundException {
      this.name         = (Name)in.readObject();
      this.obj          = in.readObject();
      this.domainName   = (DomainName) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
      out.writeObject(name);

      Object toWrite;

      if (obj instanceof SoftReference<?>) {
        toWrite = ((SoftReference<?>)obj).get();
      } else {
        toWrite = obj;
      }

      if (toWrite instanceof Stub && Proxy.isProxyClass(toWrite.getClass())) {
        InvocationHandler handler = Proxy.getInvocationHandler(toWrite);

        if (handler instanceof StubInvocationHandler) {
          toWrite = ((StubInvocationHandler) handler).toStubContainer(toWrite);
        }
      }

      out.writeObject(toWrite);
      out.writeObject(domainName);
    }

    public boolean isNull() {
      if (obj == null) {
        return true;
      }

      if (obj instanceof SoftReference<?>) {
        return ((SoftReference<?>) obj).get() == null;
      }

      return false;
    }
  }
}
