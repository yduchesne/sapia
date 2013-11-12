package org.sapia.corus.client.cli.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.sapia.console.AbortException;
import org.sapia.console.InputException;
import org.sapia.console.table.Row;
import org.sapia.console.table.Table;
import org.sapia.corus.client.Result;
import org.sapia.corus.client.Results;
import org.sapia.corus.client.cli.CliContext;
import org.sapia.corus.client.cli.CliError;
import org.sapia.corus.client.common.NameValuePair;
import org.sapia.corus.client.services.configurator.Configurator.PropertyScope;
import org.sapia.ubik.net.ServerAddress;
import org.sapia.ubik.util.Collections2;

public class Conf extends CorusCliCommand{
  
  public static final String ARG_ADD 				= "add";
  public static final String ARG_DEL 				= "del";
  public static final String ARG_LS  				= "ls";
  public static final String ARG_EXPORT     = "export";
  
  public static final String OPT_PROPERTY 	= "p";
  public static final String OPT_FILE       = "f";
  public static final String OPT_TAG 				= "t";
  public static final String OPT_SCOPE 			= "s";
  public static final String OPT_SCOPE_SVR 	= "s";
  public static final String OPT_SCOPE_PROC = "p";
  public static final String OPT_CLEAR 			= "clear";

  private static final int COL_PROP_TAG   = 0;
  private static final int COL_PROP_NAME  = 0;
  private static final int COL_PROP_VALUE = 1;
  
  public enum Op{
    ADD,
    DELETE,
    LIST,
    EXPORT
  }
  
  @Override
  protected void doExecute(CliContext ctx) throws AbortException, InputException{
    
    Op op = null;
    if(ctx.getCommandLine().hasNext() && ctx.getCommandLine().isNextArg()){
      String opArg = ctx.getCommandLine().assertNextArg().getName();
      if(opArg.equalsIgnoreCase(ARG_ADD)){
        op = Op.ADD;
      } else if(opArg.equalsIgnoreCase(ARG_DEL)){
        op = Op.DELETE;
      } else if(opArg.equalsIgnoreCase(ARG_LS)){
        op = Op.LIST;
      } else if(opArg.equalsIgnoreCase(ARG_EXPORT)){
        op = Op.EXPORT;
      } else{
        throw new InputException("Unknown argument " + opArg + "; expecting one of: add | del | ls");
      }
    } else{
      throw new InputException("Missing argument; expecting one of: add | del | ls");
    }

    if(op != null){
      if(ctx.getCommandLine().containsOption(OPT_TAG, false)){
        try {
          handleTag(op, ctx);
        } catch (IOException e) {
          throw new InputException(e.getMessage());
        }
      } else{
        handlePropertyOp(op, ctx);
      }
    }
  }
  
  private void handleTag(Op op, CliContext ctx) throws InputException, IOException {
    if(op == Op.ADD){
      String      tagString  = ctx.getCommandLine().assertOption(OPT_TAG, true).getValue();
      Set<String> toAdd      = new HashSet<String>();
      File        tagFile    = ctx.getFileSystem().getFile(tagString);
      if (tagFile.exists()) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(tagFile)));
        String line = null;
        try {
          while ((line = reader.readLine()) != null) {
            toAdd.addAll(Collections2.arrayToSet(line.split(",")));
          }
        } finally {
          reader.close();
        }
        ctx.getConsole().println("Added tags from file: " + tagFile.getAbsolutePath());
      } else {
        toAdd.addAll(Collections2.arrayToSet(tagString.split(",")));
      }
      ctx.getCorus().getConfigFacade().addTags(toAdd, getClusterInfo(ctx));
    } else if(op == Op.DELETE){
      String toRemove = ctx.getCommandLine().assertOption(OPT_TAG, true).getValue();
      ctx.getCorus().getConfigFacade().removeTag(toRemove, getClusterInfo(ctx));
    } else if(op == Op.LIST){
      displayTagResults(ctx.getCorus().getConfigFacade().getTags(getClusterInfo(ctx)), ctx);
    } else if(op == Op.EXPORT){
      exportTagResults(ctx.getCorus().getConfigFacade().getTags(getClusterInfo(ctx)), ctx);
    }
  }
  
  private void handlePropertyOp(Op op, CliContext ctx) throws InputException{ 
    PropertyScope scope = PropertyScope.PROCESS;
    if(ctx.getCommandLine().containsOption(OPT_SCOPE, true)){
      String scopeOpt = ctx.getCommandLine().assertOption(OPT_SCOPE, true).getValue();
      if(scopeOpt.startsWith(OPT_SCOPE_PROC)){
        scope = PropertyScope.PROCESS;
      }
      else if(scopeOpt.startsWith(OPT_SCOPE_SVR)){
        scope = PropertyScope.SERVER;
      }
      else{
        throw new InputException("Scope (-s) not valid, expecting s[vr] or p[roc]");
      }
    }
    if(op == Op.ADD){
      String pair = ctx.getCommandLine().assertOption(OPT_PROPERTY, true).getValue();
      
      if(!pair.contains("=") && pair.endsWith(".properties")){
        File propFile = ctx.getFileSystem().getFile(pair);
        if(!propFile.exists()){
          throw new InputException("File does not exist: " + pair);
        }
        if(propFile.isDirectory()){
          throw new InputException("File is a directory: " + pair);
        }
        
        Properties props = new Properties();
        InputStream input = null;
        try{
          input = new FileInputStream(propFile);
          props.load(input);
          boolean clearExisting = ctx.getCommandLine().containsOption(OPT_CLEAR, false);
          ctx.getCorus().getConfigFacade().addProperties(scope, props, clearExisting, getClusterInfo(ctx));          
        } catch(IOException e) {
          CliError err = ctx.createAndAddErrorFor(this, e);
          ctx.getConsole().println(err.getSimpleMessage());
        } finally {
          try{
            input.close();
          }catch(IOException e){}
        }
      } else{
        String[] nameValue = pair.split("=");
        if(nameValue.length != 2){
          throw new InputException("Invalid property format; expected: <name>=<value>");
        } else{
          ctx.getCorus().getConfigFacade().addProperty(scope, nameValue[0], nameValue[1], getClusterInfo(ctx));
        }
      }
    } else if(op == Op.DELETE){
      String name = ctx.getCommandLine().assertOption(OPT_PROPERTY, true).getValue();
      ctx.getCorus().getConfigFacade().removeProperty(scope, name, getClusterInfo(ctx));
    } else if(op == Op.LIST){
      Results<List<NameValuePair>> results = ctx.getCorus().getConfigFacade().getProperties(scope, getClusterInfo(ctx));
      displayPropertyResults(results, ctx);
    } else if(op == Op.EXPORT){
      Results<List<NameValuePair>> results = ctx.getCorus().getConfigFacade().getProperties(scope, getClusterInfo(ctx));
      exportPropertyResults(results, ctx);
    }
  }
  
  private void exportTagResults(Results<Set<String>> res, CliContext ctx) throws InputException {
    File        exportFile = ctx.getFileSystem().getFile(ctx.getCommandLine().assertOption(OPT_FILE, true).getValue());
    try  {
      PrintWriter writer     = new PrintWriter(new FileOutputStream(exportFile));
      try  {
        while (res.hasNext()) {
          Result<Set<String>> result = res.next();
          for(String tag:result.getData()){
            writer.println(tag);
          }
        }
      } finally {
        writer.flush();
        writer.close();
      }
      ctx.getConsole().println("Tags exported to: " + exportFile.getAbsolutePath());
    } catch (IOException e) {
      CliError err = ctx.createAndAddErrorFor(this, e);
      ctx.getConsole().println(err.getSimpleMessage());
    }
  
  }
  
  private void displayTagResults(Results<Set<String>> res, CliContext ctx) {
    while (res.hasNext()) {
      Result<Set<String>> result = res.next();
      displayTagsHeader(result.getOrigin(), ctx);
      for(String tag:result.getData()){
        displayTag(tag, ctx);
      }
    }
  }
  
  private void displayTagsHeader(ServerAddress addr, CliContext ctx) {
    Table         hostTable;
    Table         distTable;
    Row           row;
    Row           headers;

    hostTable = new Table(ctx.getConsole().out(), 1, 78);
    hostTable.drawLine('=');
    row = hostTable.newRow();
    row.getCellAt(0).append("Host: ").append(addr.toString());
    row.flush();

    hostTable.drawLine(' ');

    distTable = new Table(ctx.getConsole().out(), 1, 78);
    distTable.getTableMetaData().getColumnMetaDataAt(COL_PROP_TAG).setWidth(78);
    
    headers = distTable.newRow();

    headers.getCellAt(COL_PROP_TAG).append("Tag");
    headers.flush();
  }
  
  private void displayTag(String tag, CliContext ctx) {
    Table         distTable = new Table(ctx.getConsole().out(), 1, 78);
    Row           row;
    
    distTable.getTableMetaData().getColumnMetaDataAt(COL_PROP_TAG).setWidth(78);

    distTable.drawLine('-');

    row = distTable.newRow();
    row.getCellAt(COL_PROP_TAG).append(tag);
    row.flush();
  }
  
  // --------------------------------------------------------------------------

  private void exportPropertyResults(Results<List<NameValuePair>> res, CliContext ctx) throws InputException {
    File        exportFile = ctx.getFileSystem().getFile(ctx.getCommandLine().assertOption(OPT_FILE, true).getValue());
    try {
      PrintWriter writer     = new PrintWriter(new FileOutputStream(exportFile));
      try  {
        while (res.hasNext()) {
          Result<List<NameValuePair>> result = res.next();
          for(NameValuePair props:result.getData()){
            writer.println(props.getName() + "=" + props.getValue());
          }
        }
      } finally {
        writer.flush();
        writer.close();
      }
      ctx.getConsole().println("Properties exported to: " + exportFile.getAbsolutePath());
    } catch (IOException e) {
      CliError err = ctx.createAndAddErrorFor(this, e);
      ctx.getConsole().println(err.getSimpleMessage());
    }
    
  }
  
  private void displayPropertyResults(Results<List<NameValuePair>> res, CliContext ctx) {
    while (res.hasNext()) {
      Result<List<NameValuePair>> result = res.next();
      displayPropertiesHeader(result.getOrigin(), ctx);
      for(NameValuePair props:result.getData()){
        displayProperties(props, ctx);
      }
    }
  }
  
  private void displayPropertiesHeader(ServerAddress addr, CliContext ctx) {
    Table         hostTable;
    Table         distTable;
    Row           row;
    Row           headers;

    hostTable = new Table(ctx.getConsole().out(), 1, 78);
    hostTable.drawLine('=');
    row = hostTable.newRow();
    row.getCellAt(0).append("Host: ").append(addr.toString());
    row.flush();

    hostTable.drawLine(' ');

    distTable = new Table(ctx.getConsole().out(), 2, 35);
    distTable.getTableMetaData().getColumnMetaDataAt(COL_PROP_NAME).setWidth(40);
    distTable.getTableMetaData().getColumnMetaDataAt(COL_PROP_VALUE).setWidth(30);

    headers = distTable.newRow();

    headers.getCellAt(COL_PROP_NAME).append("Name");
    headers.getCellAt(COL_PROP_VALUE).append("Value");
    headers.flush();
  }
  
  private void displayProperties(NameValuePair prop, CliContext ctx) {
    Table         distTable = new Table(ctx.getConsole().out(), 2, 35);
    Row           row;
    
    distTable.getTableMetaData().getColumnMetaDataAt(COL_PROP_NAME).setWidth(40);
    distTable.getTableMetaData().getColumnMetaDataAt(COL_PROP_VALUE).setWidth(30);

    distTable.drawLine('-');

    row = distTable.newRow();
    row.getCellAt(COL_PROP_NAME).append(prop.getName());
    row.getCellAt(COL_PROP_VALUE).append(prop.getValue());
    row.flush();
  }
  
}