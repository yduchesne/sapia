package org.sapia.cocoon.php;

import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import com.caucho.quercus.Quercus;
import com.caucho.quercus.QuercusExitException;
import com.caucho.quercus.env.Env;
import com.caucho.quercus.page.InterpretedPage;
import com.caucho.quercus.page.QuercusPage;
import com.caucho.quercus.parser.QuercusParser;
import com.caucho.quercus.program.QuercusProgram;
import com.caucho.quercus.script.QuercusScriptEngineFactory;
import com.caucho.vfs.NullWriteStream;
import com.caucho.vfs.ReadStream;
import com.caucho.vfs.ReaderStream;
import com.caucho.vfs.WriteStream;
import com.caucho.vfs.WriterStreamImpl;

public class CocoonQuercusScriptEngine extends AbstractScriptEngine implements
    Compilable {
  private QuercusScriptEngineFactory _factory;

  private final Quercus _quercus;

  CocoonQuercusScriptEngine(QuercusScriptEngineFactory factory, Quercus quercus) {
    _factory = factory;
    _quercus = quercus;
  }

  /**
   * Returns the Quercus object.
   */
  Quercus getQuercus() {
    return _quercus;
  }

  /**
   * evaluates based on a reader.
   */
  public Object eval(Reader script, ScriptContext cxt) throws ScriptException {
    try {
      ReadStream reader = ReaderStream.open(script);

      QuercusProgram program = QuercusParser.parse(_quercus, null, reader);

      Writer writer = cxt.getWriter();

      WriteStream out;

      if (writer != null) {
        WriterStreamImpl s = new WriterStreamImpl();
        s.setWriter(writer);
        WriteStream os = new WriteStream(s);

        try {
          os.setEncoding("iso-8859-1");
        } catch (Exception e) {
        }

        out = os;
      } else
        out = new NullWriteStream();

      QuercusPage page = new InterpretedPage(program);

      Env env = new Env(_quercus, page, out, null, null);

      env.setScriptContext(cxt);

      // php/214c
      env.start();

      Object value = null;

      try {
        value = program.execute(env).toJavaObject();
      } catch (QuercusExitException e) {
        // php/2148
      }

      out.flushBuffer();
      out.free();

      // flush buffer just in case
      //
      // jrunscript in interactive mode does not automatically flush its
      // buffers after every input, so output to stdout will not be seen
      // until the output buffer is full
      //
      // http://bugs.caucho.com/view.php?id=1914
      writer.flush();

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
   * evaluates based on a script.
   */
  public Object eval(String script, ScriptContext cxt) throws ScriptException {
    return eval(new StringReader(script), cxt);
  }

  /**
   * compiles based on a reader.
   */
  public CompiledScript compile(Reader script) throws ScriptException {
    try {
      ReadStream reader = ReaderStream.open(script);

      QuercusProgram program = QuercusParser.parse(_quercus, null, reader);

      return new CocoonQuercusCompiledScript(this, program);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new ScriptException(e);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * evaluates based on a script.
   */
  public CompiledScript compile(String script) throws ScriptException {
    return compile(new StringReader(script));
  }

  /**
   * Returns the engine's factory.
   */
  public QuercusScriptEngineFactory getFactory() {
    return _factory;
  }

  /**
   * Creates a bindings.
   */
  public Bindings createBindings() {
    return new SimpleBindings();
  }

  public String toString() {
    return "QuercusScriptEngine[]";
  }
}
