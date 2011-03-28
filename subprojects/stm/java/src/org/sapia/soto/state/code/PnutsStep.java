/*
 * PnutsStep.java
 *
 * Created on April 5, 2005, 2:57 PM
 */

package org.sapia.soto.state.code;

import org.apache.commons.lang.ClassUtils;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.Step;

import pnuts.compiler.Compiler;
import pnuts.lang.Context;
import pnuts.lang.Package;
import pnuts.lang.Pnuts;
import pnuts.lang.PnutsException;

/**
 *
 * @author yduchesne
 */
public class PnutsStep implements Step{
  
  private Pnuts _script;
  private static final String RESULT_KEY = "result";
  
  /** Creates a new instance of PnutsStep */
  public PnutsStep() {
  }
  
  public void setText(String src) throws Exception{
    src = "result = getContext().get(\"result\"); \n" + src;
    Compiler comp = new Compiler();
    _script = comp.compile(src);
  }
  
  public String getName(){
    return ClassUtils.getShortClassName(getClass());
  }
  
  public void execute(Result result){
    Package pck = new Package(getName());
    Context ctx = new Context(pck);
    ctx.set(RESULT_KEY, result);
    try{
      _script.run(ctx);    
    }catch(PnutsException e){
      Throwable err = e.getThrowable();
      if(err != null){
        result.error(err);
      }
      else{
        result.error(e);
      }
    }
  }
}
