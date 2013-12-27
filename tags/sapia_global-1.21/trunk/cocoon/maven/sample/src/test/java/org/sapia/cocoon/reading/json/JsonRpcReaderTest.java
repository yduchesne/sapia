package org.sapia.cocoon.reading.json;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.sapia.cocoon.reading.json.JsonRpcReader.Arg;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import junit.framework.TestCase;

public class JsonRpcReaderTest extends TestCase {

  public void testDoInvokeFull() throws Throwable{
    JsonRpcReader reader = new JsonRpcReader();
    TestBean bean = TestBean.create();
    JSONObject arg1 = (JSONObject)JSONSerializer.toJSON("{'json-arg-value':10}");
    JSONObject arg2 = (JSONObject)JSONSerializer.toJSON("{'json-arg-value':'foo'}");    
    JSONObject arg3 = (JSONObject)JSONSerializer.toJSON("{'json-arg-value':"+JSONObject.fromObject(new Date()).toString()+"}");
    JSONObject arg4 = (JSONObject)JSONSerializer.toJSON(bean);
    
    List<Arg> args = new ArrayList<Arg>();
    args.add(new Arg(0, arg1.toString()));
    args.add(new Arg(1, arg2.toString()));
    args.add(new Arg(2, arg3.toString()));
    args.add(new Arg(3, arg4.toString()));    
    
    Object value = reader.doInvoke(this, "invoke", args, new HashMap());
    JsonResult result = new JsonResult();
    result.setValue(value);
    reader.serialize(result, System.out);

  }
  
  public void testDoInvokeNull() throws Throwable{
    JsonRpcReader reader = new JsonRpcReader();
    TestBean bean = TestBean.create();
    JSONObject arg1 = (JSONObject)JSONSerializer.toJSON("{'json-arg-value':null}");
    JSONObject arg2 = (JSONObject)JSONSerializer.toJSON("{'json-arg-value':'null'}");    
    JSONObject arg3 = (JSONObject)JSONSerializer.toJSON("{'json-arg-value':"+JSONObject.fromObject(new Date()).toString()+"}");
    JSONObject arg4 = (JSONObject)JSONSerializer.toJSON(bean);
    
    List<Arg> args = new ArrayList<Arg>();
    args.add(new Arg(0, arg1.toString()));
    args.add(new Arg(1, arg2.toString()));
    args.add(new Arg(2, arg3.toString()));
    args.add(new Arg(3, arg4.toString()));    
    
    Object value = reader.doInvoke(this, "invoke", args, new HashMap());
    JsonResult result = new JsonResult();
    result.setValue(value);
    reader.serialize(result, System.out);

  }  
  
  
  public int invoke(int i, String s, Date d, TestBean bean){
    return 100;
  }
  
}
