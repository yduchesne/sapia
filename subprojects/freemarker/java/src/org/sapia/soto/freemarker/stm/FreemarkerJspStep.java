package org.sapia.soto.freemarker.stm;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sapia.soto.state.Context;
import org.sapia.soto.state.MVC;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.web.AbstractStmServlet;
import org.sapia.soto.state.web.WebConsts;

import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.template.ObjectWrapper;

public class FreemarkerJspStep extends FreemarkerStep implements WebConsts {

  static TaglibFactory _factory;
  static GenericServlet _fmkServlet;

  static Object _lock = new Object();

  public void execute(Result res) {
    Context ctx = res.getContext();
    Object servletCtxObj = ctx.get(AbstractStmServlet.SERVLET_CONTEXT_KEY,
        VIEW_SCOPE);
    if (servletCtxObj != null && servletCtxObj instanceof ServletContext) {
      ServletContext servletCtx = (ServletContext) servletCtxObj;
      TaglibFactory factory = null;
      try{
        factory = createFactory(servletCtx);
      }catch(Exception e){
        res.error("Could not create TaglibFactory instance", e);
        return;
      }
      ctx.put(FreemarkerServlet.KEY_APPLICATION, new ServletContextHashModel(
          _fmkServlet, ObjectWrapper.DEFAULT_WRAPPER), VIEW_SCOPE);

      HttpServletRequest request = (HttpServletRequest) ctx.get(
          AbstractStmServlet.REQUEST_KEY, VIEW_SCOPE);
      HttpServletResponse response = (HttpServletResponse) ctx.get(
          AbstractStmServlet.RESPONSE_KEY, VIEW_SCOPE);

      ctx.put(FreemarkerServlet.KEY_REQUEST, new HttpRequestHashModel(request,
          response, ObjectWrapper.DEFAULT_WRAPPER), VIEW_SCOPE);
      ctx.put(FreemarkerServlet.KEY_JSP_TAGLIBS, factory, VIEW_SCOPE);
    }
    super.execute(res);
  }
  
  TaglibFactory createFactory(ServletContext ctx) throws Exception{
    if (_factory == null) {
      synchronized (_lock) {
        if (_factory == null) {
          _fmkServlet = new FmkServlet();
          _fmkServlet.init(new FmkServletConfig(ctx));
          _factory = new TaglibFactory(ctx);
        }
      }
    }
    return _factory;

  }
  
  protected void onPreRender(Result res) {
    if(res.getContext() instanceof MVC){
      Map model = ((MVC)res.getContext()).getViewParams();
    
      Iterator it = model.entrySet().iterator();
      while (it.hasNext()) {
        Map.Entry entry = (Map.Entry) it.next();
        if (!(entry.getKey() instanceof String)) {
            throw new IllegalArgumentException("Invalid key [" + entry.getKey() +
                        "] in model Map - only Strings allowed as model keys");
        }
        String modelName = (String) entry.getKey();
        Object modelValue = entry.getValue();
        if (modelValue != null) {
          res.getContext().put(modelName, modelValue, REQUEST_SCOPE);
        } 
      }
    }
  }  
  
  ///////////////////// INNER CLASSES ////////////////////

  public static class FmkServlet extends GenericServlet {
    public void service(ServletRequest servletRequest,
        ServletResponse servletResponse) {
    }
  }

  private class FmkServletConfig implements ServletConfig {
    private ServletContext ctx;

    FmkServletConfig(ServletContext ctx) {
      this.ctx = ctx;
    }

    public String getServletName() {
      return FmkServlet.class.getName();
    }

    public ServletContext getServletContext() {
      return ctx;
    }

    public String getInitParameter(String distinguishedName) {
      return null;
    }

    public Enumeration getInitParameterNames() {
      return new Enumeration() {
        public boolean hasMoreElements() {
          return false;
        }

        public Object nextElement() {
          return null;
        }
      };
    }
  }
}
