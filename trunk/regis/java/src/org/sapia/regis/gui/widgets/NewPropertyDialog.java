package org.sapia.regis.gui.widgets;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;

import org.sapia.gumby.RenderContext;
import org.sapia.gumby.event.EventManager;
import org.sapia.regis.Property;
import org.sapia.regis.gui.GlobalContext;
import org.sapia.regis.gui.GuiConsts;
import org.sapia.regis.gui.event.NewNodePropertyCreationEvent;
import org.sapia.regis.gui.event.PropertyCreationEvent;
import org.sapia.regis.impl.PropertyImpl;

public class NewPropertyDialog extends JDialog{
  
  RenderContext ctx;
  boolean newNode;

  public NewPropertyDialog(boolean newNode) throws Exception{
    super((JFrame)GlobalContext.getInstance().getWidget(GuiConsts.FRAME_KEY), true);
    super.setTitle("New Property");
    this.newNode = newNode;
    ctx = GlobalContext.getInstance().getRenderContext().newChildInstance();
    ctx.getEnv().put("dialog", this, "local");
    super.getContentPane().add((JComponent)ctx.render(
        ctx.getSettings().resolveResource("org/sapia/regis/gui/screens/newPropertyDialog.xml")));
  }
  
  public void addProperty(){
    JTextField name  = (JTextField)ctx.getEnv().get("name", "local");
    JTextField value = (JTextField)ctx.getEnv().get("value", "local");
    
    if(name.getText() != null){
      String strName = name.getText().trim();
      if(strName.length() > 0){
        String strValue = value.getText();
        if(strValue != null){
          strValue = strValue.trim();
          Property prop = new PropertyImpl(strName, strValue);
          if(newNode){
            EventManager.getInstance().dispatchEvent(new NewNodePropertyCreationEvent(prop));
          }
          else{
            EventManager.getInstance().dispatchEvent(new PropertyCreationEvent(prop));
          }
        }
      }
    }
  }
  
}
