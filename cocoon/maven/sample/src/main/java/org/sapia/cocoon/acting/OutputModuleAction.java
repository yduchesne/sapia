package org.sapia.cocoon.acting;

import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.Action;
import org.apache.cocoon.components.modules.output.OutputModule;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * This action outputs a given value to a specified {@link OutputModule}.
 * @author yduchesne
 *
 */
public class OutputModuleAction implements Action, BeanFactoryAware {
  
  public static final String PARAM_MODULE = "module";
  public static final String PARAM_NAME   = "name";
  public static final String PARAM_VALUE  = "value";
  
  private BeanFactory _factory;
  
  public void setBeanFactory(BeanFactory factory) throws BeansException {
    _factory = factory;
  }

  public Map act(Redirector redirector, SourceResolver resolver,
      Map objectModel, String src, Parameters parameters) throws Exception {
    
    OutputModule module = (OutputModule)_factory.getBean(OutputModule.ROLE+"/"+parameters.getParameter(PARAM_MODULE));
    
    module.setAttribute(null, objectModel, parameters.getParameter(PARAM_NAME), parameters.getParameter(PARAM_VALUE));
    module.commit(null, objectModel);

    return null;
  }

}
