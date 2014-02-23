package org.sapia.dataset.cli;

import groovy.lang.GroovyShell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.sapia.dataset.Datatype;
import org.sapia.dataset.help.Help;
import org.sapia.dataset.io.Console;
import org.sapia.dataset.io.ConsoleOutput;
import org.sapia.dataset.io.csv.Csv;
import org.sapia.dataset.io.text.Texts;
import org.sapia.dataset.math.Sum;
import org.sapia.dataset.stat.Stats;
import org.sapia.dataset.transform.filter.Filters;
import org.sapia.dataset.transform.formula.Formulas;
import org.sapia.dataset.transform.index.Indices;
import org.sapia.dataset.transform.join.Joins;
import org.sapia.dataset.transform.merge.Merges;
import org.sapia.dataset.transform.pivot.Pivots;
import org.sapia.dataset.transform.range.Ranges;
import org.sapia.dataset.transform.slice.Slices;
import org.sapia.dataset.transform.sort.Sorts;
import org.sapia.dataset.transform.view.Views;
import org.sapia.dataset.util.ChainOR;
import org.sapia.dataset.util.Data;
import org.sapia.dataset.util.Settings;

public class Cli {

  private GroovyShell         shell;
  private StringBuilder       buffer   = new StringBuilder();
  private ChainOR<CmdContext> commands = CommandChainFactory.getDefaultCommands();
  private CliSessionImpl      session;
  
  private Cli() {
    CompilerConfiguration config = new CompilerConfiguration();
    ImportCustomizer imports = new ImportCustomizer();
    imports.addImports(
        
        // Misc
        Datatype.class.getName(),
        Data.class.getName(),
        Settings.class.getName(),
        Help.class.getName(),
        
        // IO
        Csv.class.getName(),
        Texts.class.getName(),
        
        // Computation
        Stats.class.getName(),
        Sum.class.getName(),
        
        // Transformation
        Filters.class.getName(),
        Formulas.class.getName(),
        Indices.class.getName(),
        Joins.class.getName(),
        Merges.class.getName(),
        Pivots.class.getName(),
        Ranges.class.getName(),
        Slices.class.getName(),
        Sorts.class.getName(),
        Views.class.getName()
    );
    
    imports.addStaticStars(
        Data.class.getName(),
        Datatype.class.getName(),
        Help.class.getName(),
        Settings.class.getName()
    );
    
    config.addCompilationCustomizers(imports);
    shell   = new GroovyShell(config);
    session = new CliSessionImpl(shell, new ConsoleOutput() {
      @Override
      public void println(Object content) {
        Console.println(content);
      }
      
      @Override
      public void print(Object content) {
        Console.print(content);
      }
    });
  }
  
  /**
   * @return the Groovy shell.
   */
  public GroovyShell getShell() {
    return shell;
  }
  
  public static void main(String[] args) throws Exception {
    Cli cli = new Cli();
    cli.run();
  }
  
  private void run() throws IOException {
    Console.println("Datasun command-line interface. Type 'exit' to terminate.");
    BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    while (true) {
      Console.print(">> ");
      String line = input.readLine();
      try {
        doRun(line);
      } catch (Exception e) {
        this.session.getErrorBuffer().addError(e);
        if (e.getMessage() == null) {
          session.message("Error occurred: " + e.getClass().getSimpleName() + " - type 'err' to see exception stack trace");
        } else {
          session.message(e.getMessage() + " - type 'err' to see exception stack trace");
        }
      }
    }
  }
  
  private void doRun(String line) throws Exception {
    if (line != null) {
      line = line.trim();
      
      CmdContext context = new CmdContext(session, line);
      CliCommand cmd = (CliCommand) commands.select(context);
      if (cmd != null) {
        cmd.run(context);
      } else {
        if (line.endsWith(";")) {
          Object returnValue = shell.evaluate(line);
          buffer.delete(0, buffer.length());
          if (returnValue != null) {
            session.getOutput().println(returnValue);
          }
        } else if (line.length() == 0 && buffer.length() != 0) {
          Object returnValue = shell.evaluate(buffer.toString());
          buffer.delete(0, buffer.length());
          if (returnValue != null) {
            session.getOutput().println(returnValue);
          }
        } else {
          buffer.append(line).append(System.lineSeparator());
        } 
      }
    }
  }
  
}
