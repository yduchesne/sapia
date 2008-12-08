package org.sapia.regis.gui.widgets;

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.sapia.gumby.RenderContext;
import org.sapia.gumby.factory.ContextAware;
import org.sapia.gumby.jelly.JellyHelper;
import org.sapia.regis.gui.GlobalContext;
import org.sapia.regis.gui.GuiConsts;
import org.sapia.regis.gui.model.NodeModel;
import org.sapia.regis.impl.PropertyImpl;
import org.sapia.util.xml.confix.ConfigurationException;

public class NodePanel extends JPanel implements ContextAware, GuiConsts{
  
  private RenderContext _ctx;
  
  public NodePanel(){
    setLayout(new FlowLayout(FlowLayout.LEFT));
  }
  
  
  public synchronized void fireCurrentNodeChange(){
    super.removeAll();
    NodeModel nodeModel = GlobalContext.getInstance().getModelManager().getNodeModel();
    if(nodeModel.getNode() == null) return;
    
    RenderContext child = _ctx.newChildInstance();
    child.getEnv().put("Model", nodeModel, "local");
    try{
      JComponent component = (JComponent)JellyHelper.render(child, child.getSettings().resolveURL("org/sapia/regis/gui/screens/nodePane.jelly.xml"));
      super.add(component);
      // THIS IS A HACK TO TRIGGER PROPER TAB REFRESH AFTER ADDING PROPERTY
      JComponent comp = (JComponent)GlobalContext.getInstance().getWidget(TABBED_PANE);
      comp.requestFocus();      
    }catch(ConfigurationException e){
      GlobalContext.getInstance().error(e);
    }
    
  }
  
  public synchronized List getProperties(){
    JComponent panel = (JComponent)super.getComponent(0);
    Component[] comps = panel.getComponents();
    List props = new ArrayList();
    for(int i = 0; i < comps.length; i++){
      if(comps[i] instanceof JTextField){
        JTextField field = (JTextField)comps[i];
        String value = field.getText();
        if(value == null || value.trim().length() == 0){
          continue;
        }
        PropertyImpl prop = new PropertyImpl(field.getName(), field.getText());
        props.add(prop);
      }
    }
    return props;
  }
  
  public void handleContext(RenderContext context) {
    _ctx = context;
  }
}
