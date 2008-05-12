package org.sapia.cocoon.php;

import com.caucho.quercus.env.Env;
import com.caucho.quercus.page.InterpretedPage;
import com.caucho.quercus.page.QuercusPage;
import com.caucho.quercus.program.QuercusProgram;
import com.caucho.vfs.*;

import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.Writer;

public class CocoonQuercusCompiledScript extends CompiledScript {
  private final CocoonQuercusScriptEngine _engine;

  private final QuercusProgram _program;

  CocoonQuercusCompiledScript(CocoonQuercusScriptEngine engine,
      QuercusProgram program) {
    _engine = engine;
    _program = program;
  }

  /**
   * evaluates based on a reader.
   */
  public Object eval(ScriptContext cxt) throws ScriptException {
    try {
      Writer writer = cxt.getWriter();

      WriteStream out;

      if (writer != null) {
        ReaderWriterStream s = new ReaderWriterStream(null, writer);
        WriteStream os = new WriteStream(s);

        try {
          os.setEncoding("utf-8");
        } catch (Exception e) {
        }

        out = os;
      } else
        out = new NullWriteStream();

      QuercusPage page = new InterpretedPage(_program);

      Env env = new Env(_engine.getQuercus(), page, out, null, null);

      env.setScriptContext(cxt);

      Object value = _program.execute(env).toJavaObject();

      out.flushBuffer();
      out.free();

      return value;
      /*
       * } catch (ScriptException e) { throw e;
       */
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new ScriptException(e);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns the script engine.
   */
  public ScriptEngine getEngine() {
    return _engine;
  }

  public String toString() {
    return "QuercusCompiledScript[]";
  }
}
