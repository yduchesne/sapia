package org.sapia.ubik.rmi.server.load;

import java.io.File;
import java.util.Properties;

import javax.naming.Context;

import org.sapia.ubik.rmi.naming.remote.JNDIConsts;
import org.sapia.ubik.rmi.naming.remote.JNDIContextBuilder;
import org.sapia.ubik.rmi.server.Hub;
import org.sapia.ubik.rmi.server.transport.http.HttpServerExporter;
import org.sapia.ubik.rmi.server.transport.netty.NettyServerExporter;
import org.sapia.ubik.rmi.server.transport.mina.MinaServerExporter;
import org.sapia.ubik.rmi.server.transport.socket.SocketServerExporter;
import org.sapia.ubik.util.PropUtil;
import org.sapia.ubik.util.cli.Cmd;
import org.sapia.ubik.util.cli.Cmd.OptionFilter;
import org.sapia.ubik.util.cli.Opt;

/**
 * Binds a {@link LoadService} instance to the JNDI. Type <code>-help</code> at
 * the command-line for help.
 * 
 * <p>
 * You can start a {@link LoadServer} with the <code>starter.sh</code> script
 * that comes with Ubik's distribution:
 * 
 * <pre>
 * ./starter.sh org.sapia.ubik.rmi.server.load.LoadServer [options]
 * </pre>
 * 
 * For help, type:
 * 
 * <pre>
 * ./starter.sh org.sapia.ubik.rmi.server.load.LoadServer -help
 * </pre>
 * 
 * The following connects to the JNDI server on <code>localhost:1099</code> and
 * loads properties from the specified file:
 * 
 * <pre>
 * ./starter.sh org.sapia.ubik.rmi.server.load.LoadServer -h localhost -p 1099 -f /some/path/to/load-server.properties
 * </pre>
 * 
 * See the documentation of the {@link LoadClient} class for more details about
 * using the <code>starter.sh</code> script.
 * 
 * @author yduchesne
 * 
 */
public class LoadServer {

  public static void main(String[] args) throws Exception {

    Cmd cmd = Cmd.fromArgs(args).filter(new OptionFilter() {
      @Override
      public boolean accepts(Opt option) {
        return !option.getName().contains(".sh");
      }
    });

    if (cmd.hasSwitch("help")) {
      System.out.println("-c: The fully qualified name of the LoadService interface implementation");
      System.out.println("    to bind to the JNDI. Defaults to " + WorkLoadService.class.getName() + ".");
      System.out.println("-h: The host of the JNDI server to connect to. Defaults to localhost.");
      System.out.println("-p: The port of the JNDI server to connect to. Defaults to " + JNDIConsts.DEFAULT_PORT);
      System.out.println("-f: The path to the Ubik properties file to load (OPTIONAL)");
      System.out.println("-t: The type of transport to use. Value must be either 'nio', 'standard', 'netty' or 'http', ");
      System.out.println("    without the quotes. Defaults to nio.");
      return;
    }

    LoadService impl = null;

    try {
      impl = (LoadService) Class.forName(cmd.hasSwitch("c") ? cmd.getOptWithValue("c").getTrimmedValueOrBlank() : NoopLoadService.class.getName())
          .newInstance();
    } catch (Exception e) {
      System.out.println("Could not instantiate LoadService implementation, aborting");
      e.printStackTrace();
      return;
    }

    Properties props = new Properties();
    if (cmd.hasSwitch("f")) {
      File f = new File(cmd.getOptWithValue("f").getTrimmedValueOrBlank());
      PropUtil.loadIntoPropertiesFrom(props, f);
    }
    PropUtil.copy(props, System.getProperties());

    Context context = JNDIContextBuilder.newInstance().properties(props)
        .port(cmd.hasSwitch("p") ? cmd.getOptWithValue("p").getIntValue() : JNDIConsts.DEFAULT_PORT)
        .host(cmd.hasSwitch("h") ? cmd.getOptWithValue("h").getValue() : "localhost").build();

    String transport = cmd.hasSwitch("t") ? cmd.getOptWithValue("t").getTrimmedValueOrBlank() : "nio";

    if (transport.equals("netty")) {
      NettyServerExporter exporter = new NettyServerExporter();
      context.bind(LoadService.JNDI_NAME, exporter.export(impl));
    } else if (transport.equals("nio")) {
      MinaServerExporter exporter = new MinaServerExporter();
      context.bind(LoadService.JNDI_NAME, exporter.export(impl));
    } else if (transport.equals("standard")) {
      SocketServerExporter exporter = new SocketServerExporter();
      context.bind(LoadService.JNDI_NAME, exporter.export(impl));
    } else if (transport.equals("http")) {
      HttpServerExporter exporter = new HttpServerExporter();
      context.bind(LoadService.JNDI_NAME, exporter.export(impl));
    } else {
      context.close();
      Hub.shutdown();
      System.out.println("Invalid transport: " + transport + ". Must be either nio, standard, or mplex");
      return;
    }
    context.close();

    // registering shutdown hook
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        Hub.shutdown();
      }
    });

    // pausing until JVM termination
    while (true) {
      Thread.sleep(100000);
    }

  }

}
