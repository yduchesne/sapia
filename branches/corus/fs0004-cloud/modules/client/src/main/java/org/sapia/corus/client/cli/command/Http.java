package org.sapia.corus.client.cli.command;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.sapia.console.AbortException;
import org.sapia.console.InputException;
import org.sapia.corus.client.Result;
import org.sapia.corus.client.Results;
import org.sapia.corus.client.cli.CliContext;
import org.sapia.corus.client.common.Arg;
import org.sapia.corus.client.common.ArgFactory;
import org.sapia.corus.client.services.port.PortRange;
import org.sapia.ubik.rmi.server.transport.http.HttpAddress;
import org.sapia.ubik.util.Function;

/**
 * This command provides various HTTP-related utilities:
 * 
 * <li>
 *   <ul>The ability to check given HTTP endpoints (either by specifying URLs, or by specifying port ranges).
 *   <ul>The ability to perform a HTTP post on a given URL.
 * </li>
 * 
 * @author yduchesne
 *
 */
public class Http extends CorusCliCommand {
  
  private static final String CHECK_ARG        = "check";
  private static final String POST_ARG         = "post";
  private static final String URL_OPT          = "u";
  private static final String MAX_ATTEMPTS_OPT = "m";
  private static final String INTERVAL_OPT     = "t";
  private static final String STATUS_OPT       = "s";
  private static final String PORT_RANGE_OPT   = "p";
  private static final String CONTEXT_PATH_OPT = "c";

  
  private static final int DEFAULT_STATUS       = 200;
  private static final int DEFAULT_MAX_ATTEMPTS = Integer.MAX_VALUE;
  private static final int DEFAULT_INTERVAL     = 10;
  
  @Override
  protected void doExecute(CliContext ctx) throws AbortException,
      InputException {
    
    if (ctx.getCommandLine().isNextArg()) {
      String subCommand = ctx.getCommandLine().assertNextArg().getName();
      if (subCommand.equalsIgnoreCase(CHECK_ARG)) {
        doCheck(ctx);
      } else if (subCommand.equalsIgnoreCase(POST_ARG)) {
        doPost(ctx);
      } else {
        throw new InputException("Unknown argument");
      }
      
    } else {
      throw new InputException("Missing argument");
    }
  }
  
  // --------------------------------------------------------------------------
  // subcommand methods
  
  private void doCheck(CliContext ctx) throws InputException {
    int maxAttempts = getOpt(ctx, MAX_ATTEMPTS_OPT, "" + DEFAULT_MAX_ATTEMPTS).asInt();
    int interval    = getOpt(ctx, INTERVAL_OPT, "" + DEFAULT_INTERVAL).asInt();
    int expected    = getOpt(ctx, STATUS_OPT, "" + DEFAULT_STATUS).asInt();
    
    if (ctx.getCommandLine().containsOption(URL_OPT, true)) {
      String url = ctx.getCommandLine().assertOption(URL_OPT, true).getValue();
      doCheck(url, maxAttempts, expected, interval);
    } else if (ctx.getCommandLine().containsOption(PORT_RANGE_OPT, false)) {
      String contextPath = getOptValue(ctx, CONTEXT_PATH_OPT);
      Results<List<PortRange>> portRangesByNode = ctx.getCorus().getPortManagementFacade().getPortRanges(getClusterInfo(ctx));
      List<Arg> portRangePatterns = getOptValues(ctx, PORT_RANGE_OPT, new Function<Arg, String>() {
        @Override
        public Arg call(String arg) {
          return ArgFactory.parse(arg);
        }
      });
      
      while (portRangesByNode.hasNext()) {
        Result<List<PortRange>> ranges = portRangesByNode.next();
        for (PortRange r : ranges.getData()) {
          if (isIncluded(portRangePatterns, r)) {
            HttpAddress address = (HttpAddress) ranges.getOrigin();
            for (Integer port : r.getActive()) {
              String url = "http://" + address.getHost() + ":" + port;
              if (contextPath != null) {
                if (contextPath.startsWith("/")) {
                  url = url + contextPath;
                } else {
                  url = url + "/" + contextPath;
                }
              }
              doCheck(url, maxAttempts, expected, interval);
            }
          }
        }
      } 
    }
  }
  
  static void doPost(CliContext ctx) throws InputException {
    String url = ctx.getCommandLine().assertOption(URL_OPT, true).getValue();
    int expected = -1;
    if (ctx.getCommandLine().containsOption(STATUS_OPT, true)) {
      expected = ctx.getCommandLine().assertOption(STATUS_OPT, true).asInt();
    }

    HttpClient client = new DefaultHttpClient();
    HttpPost   post   = new HttpPost(url);
    try {
      HttpResponse response = client.execute(post);
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode != expected && expected > -1) {
        throw new AbortException();
      }
      EntityUtils.consume(response.getEntity());
    } catch (IOException e) {
      throw new AbortException();
    } 
  }

  // --------------------------------------------------------------------------
  // utility methods
  
  static boolean isIncluded(List<Arg> portRangePatterns, PortRange r) {
    if (portRangePatterns.isEmpty()) {
      return true;
    } else {
      for (Arg p : portRangePatterns) {
        if (p.matches(r.getName())) {
          return true;
        }
      }
      return false;
    }
  }
  
  static void doCheck(String url, int  maxAttempts, int expected, int interval) {
    HttpClient client = new DefaultHttpClient();

    int count = 0;
    while (count < maxAttempts) {
      HttpGet get = new HttpGet(url);
      try {
        HttpResponse response = client.execute(get);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == expected) {
          break;
        } else if (count == maxAttempts) {
          throw new AbortException();
        }
        EntityUtils.consume(response.getEntity());
      } catch (Exception e) {
        if (count == maxAttempts) {
          throw new AbortException();
        }
      } 
      count++;
      sleep(TimeUnit.MILLISECONDS.convert(interval, TimeUnit.SECONDS));
    }
  }
}