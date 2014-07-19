package org.sapia.corus.client.common;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.sapia.console.CmdLine;
import org.sapia.console.Option;
import org.sapia.corus.client.Result;
import org.sapia.corus.client.Results;
import org.sapia.corus.client.services.cluster.CorusHost;
import org.sapia.corus.client.services.cluster.CorusHost.RepoRole;
import org.sapia.corus.client.services.cluster.Endpoint;
import org.sapia.ubik.net.ServerAddress;
import org.sapia.ubik.net.Uri;
import org.sapia.ubik.rmi.server.transport.http.HttpAddress;

@RunWith(MockitoJUnitRunner.class)
public class CliUtilsTest {
  
  @Mock
  private ServerAddress channelAddress;

  @Test
  public void testParseServerAddresses() {
    Set<ServerAddress> addresses = CliUtils.parseServerAddresses("host1:33000,host2:33000");
    assertEquals(2, addresses.size());
    assertTrue(addresses.contains(HttpAddress.newDefaultInstance("host1", 33000)));
    assertTrue(addresses.contains(HttpAddress.newDefaultInstance("host2", 33000)));
  }

  @Test
  public void testCollectResultsPerHost() {
    Results<String> results = new Results<String>();
    results.setInvocationCount(5);
    for (int i = 0; i < 5; i++) {
      Endpoint ep = new Endpoint(new HttpAddress(Uri.parse("http://test_" + i)), channelAddress);
      CorusHost host = CorusHost.newInstance(ep, "os_" + i, "vm_" + i);
      host.setRepoRole(RepoRole.NONE);
      
      Result<String> r = new Result<String>(host, "data_i");
      results.addResult(r);
    }

    Map<ServerAddress, String> resultsPerHost = CliUtils.collectResultsPerHost(results);
    for (int i = 0; i < 5; i++) {
      assertEquals(resultsPerHost.get(new HttpAddress(Uri.parse("http://test_" + i))), "data_i");
    }
  }

  @Test
  public void testIsHelp() {
    assertTrue(CliUtils.isHelp(CmdLine.parse("-help")));
    assertTrue(CliUtils.isHelp(CmdLine.parse("--help")));
  }
  
  @Test
  public void testFromOption() {
    CmdLine cmd = CmdLine.parse("arg1 -opt1 val1 -opt2 val2 arg2");
    CmdLine from = CliUtils.fromOption("opt2", cmd);
    assertTrue(from.get(0) instanceof Option);
    assertEquals("opt2", from.get(0).getName());
    assertEquals("val2", ((Option)from.get(0)).getValue());
    assertEquals("arg2", from.last().getName());
  }

  @Test
  public void testFromOption_no_option_found() {
    CmdLine cmd = CmdLine.parse("arg1 -opt1 val1 -opt2 val2 arg2");
    CmdLine from = CliUtils.fromOption("opt3", cmd);
    assertEquals(0, from.size());
  }
  
  @Test
  public void testToOption() {
    CmdLine cmd = CmdLine.parse("arg1 -opt1 val1 -opt2 val2 arg2");
    CmdLine to = CliUtils.toOption("opt2", cmd);
    assertTrue(to.get(0) instanceof org.sapia.console.Arg);
    assertEquals("arg1", to.get(0).getName());
    assertEquals("opt1", to.get(1).getName());
    assertEquals("val1", ((Option)to.get(1)).getValue());
  }

  @Test
  public void testToOption_no_option_found() {
    CmdLine cmd = CmdLine.parse("arg1 -opt1 val1 -opt2 val2 arg2");
    CmdLine from = CliUtils.toOption("opt3", cmd);
    assertEquals(cmd.size(), from.size());
  }
  
}