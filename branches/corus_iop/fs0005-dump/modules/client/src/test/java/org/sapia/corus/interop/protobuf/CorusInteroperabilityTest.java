package org.sapia.corus.interop.protobuf;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.google.protobuf.ExtensionRegistry;


public class CorusInteroperabilityTest {
  
  private ExtensionRegistry registry;
  
  @Before
  public void setUp() throws Exception {
    registry = ExtensionRegistry.newInstance();
    CorusInteroperability.registerAllExtensions(registry);
  }
  
  protected CorusInteroperability.Message cloneWithSerialization(CorusInteroperability.Message aMessage) throws Exception {
    byte[] data = aMessage.toByteArray();
    
    CorusInteroperability.Message clone = CorusInteroperability.Message.newBuilder().
        mergeFrom(data, registry).
        build();
    
    return clone;
  }
  
  @Test
  public void testPollRequest() throws Exception {
    CorusInteroperability.Poll pollRequest = CorusInteroperability.Poll.newBuilder().
        setCommandId("675432").
        build();
    
    CorusInteroperability.Process process = CorusInteroperability.Process.newBuilder().
        setRequestId("113344").
        setCorusPid("2045").
        build();
    
    CorusInteroperability.Message message = CorusInteroperability.Message.newBuilder().
        setHeader(ProtobufSerializer.wrapHeader(process)).
        addCommands(ProtobufSerializer.wrapCommand(pollRequest)).
        build();
    
    CorusInteroperability.Message deserializedMessage = cloneWithSerialization(message);
    Assert.assertNotNull(deserializedMessage);
    
    assertProcessHeader("113344", "2045", deserializedMessage.getHeader());
    Assert.assertEquals(1, deserializedMessage.getCommandsCount());
    assertPollCommand("675432", deserializedMessage.getCommands(0));
  }
  
  @Test
  public void testStatusRequest_singleContext() throws Exception {
    CorusInteroperability.Status.Context context = CorusInteroperability.Status.Context.newBuilder().
        setName("root/sna/foo").
        addParams(CorusInteroperability.Status.Param.newBuilder().setName("prop1").setValue("value1").build()).
        build();
    
    CorusInteroperability.Status statusRequest = CorusInteroperability.Status.newBuilder().
        setCommandId("445577").
        addContexts(context).
        build();
    
    CorusInteroperability.Process process = CorusInteroperability.Process.newBuilder().
        setRequestId("7897988").
        setCorusPid("568").
        build();
    
    CorusInteroperability.Message message = CorusInteroperability.Message.newBuilder().
        setHeader(ProtobufSerializer.wrapHeader(process)).
        addCommands(ProtobufSerializer.wrapCommand(statusRequest)).
        build();
    
    CorusInteroperability.Message deserializedMessage = cloneWithSerialization(message);
    Assert.assertNotNull(deserializedMessage);
    
    assertProcessHeader("7897988", "568", deserializedMessage.getHeader());
    Assert.assertEquals(1, deserializedMessage.getCommandsCount());
    assertStatusCommand("445577", new String[] { "root/sna/foo" }, new String[][] {{ "prop1", "value1" }}, deserializedMessage.getCommands(0));
  }
  
  @Test
  public void testStatusRequest_multipleContexts() throws Exception {
    CorusInteroperability.Status.Context context1 = CorusInteroperability.Status.Context.newBuilder().
        setName("root/sna/foo").
        addParams(CorusInteroperability.Status.Param.newBuilder().setName("prop1").setValue("value1").build()).
        addParams(CorusInteroperability.Status.Param.newBuilder().setName("prop2").setValue("value2").build()).
        build();

    CorusInteroperability.Status.Context context2 = CorusInteroperability.Status.Context.newBuilder().
        setName("jvm memory").
        addParams(CorusInteroperability.Status.Param.newBuilder().setName("min").setValue("32").build()).
        addParams(CorusInteroperability.Status.Param.newBuilder().setName("max").setValue("64").build()).
        build();
    
    CorusInteroperability.Status statusRequest = CorusInteroperability.Status.newBuilder().
        setCommandId("444898").
        addContexts(context1).
        addContexts(context2).
        build();
    
    CorusInteroperability.Process process = CorusInteroperability.Process.newBuilder().
        setRequestId("2114").
        setCorusPid("34879").
        build();
    
    CorusInteroperability.Message message = CorusInteroperability.Message.newBuilder().
        setHeader(ProtobufSerializer.wrapHeader(process)).
        addCommands(ProtobufSerializer.wrapCommand(statusRequest)).
        build();
    
    CorusInteroperability.Message deserializedMessage = cloneWithSerialization(message);
    Assert.assertNotNull(deserializedMessage);
    
    assertProcessHeader("2114", "34879", deserializedMessage.getHeader());
    Assert.assertEquals(1, deserializedMessage.getCommandsCount());
    assertStatusCommand("444898", new String[] { "root/sna/foo", "jvm memory" },
        new String[][] { { "prop1", "value1", "prop2", "value2" }, { "min", "32", "max", "64" }  }, deserializedMessage.getCommands(0));
  }
  
  @Test
  public void testRestartRequest() throws Exception {
    CorusInteroperability.Restart restartRequest = CorusInteroperability.Restart.newBuilder().
        setCommandId("46532").
        build();
    
    CorusInteroperability.Process process = CorusInteroperability.Process.newBuilder().
        setRequestId("124578").
        setCorusPid("11255").
        build();
    
    CorusInteroperability.Message message = CorusInteroperability.Message.newBuilder().
        setHeader(ProtobufSerializer.wrapHeader(process)).
        addCommands(ProtobufSerializer.wrapCommand(restartRequest)).
        build();
    
    CorusInteroperability.Message deserializedMessage = cloneWithSerialization(message);
    Assert.assertNotNull(deserializedMessage);
    
    assertProcessHeader("124578", "11255", deserializedMessage.getHeader());
    Assert.assertEquals(1, deserializedMessage.getCommandsCount());
    assertRestartCommand("46532", deserializedMessage.getCommands(0));
  }
  
  @Test
  public void testConfirmShutdownRequest() throws Exception {
    CorusInteroperability.ConfirmShutdown confirmRequest = CorusInteroperability.ConfirmShutdown.newBuilder().
        setCommandId("46535").
        build();
    
    CorusInteroperability.Process process = CorusInteroperability.Process.newBuilder().
        setRequestId("124579").
        setCorusPid("11255").
        build();
    
    CorusInteroperability.Message message = CorusInteroperability.Message.newBuilder().
        setHeader(ProtobufSerializer.wrapHeader(process)).
        addCommands(ProtobufSerializer.wrapCommand(confirmRequest)).
        build();
    
    CorusInteroperability.Message deserializedMessage = cloneWithSerialization(message);
    Assert.assertNotNull(deserializedMessage);
    
    assertProcessHeader("124579", "11255", deserializedMessage.getHeader());
    Assert.assertEquals(1, deserializedMessage.getCommandsCount());
    assertConfirmShutdownCommand("46535", deserializedMessage.getCommands(0));
  }
  
  @Test
  public void testConfirmDumpRequest() throws Exception {
    CorusInteroperability.ConfirmDump confirmDumpRequest = CorusInteroperability.ConfirmDump.newBuilder().
        setCommandId("55658").
        build();
    
    CorusInteroperability.Process process = CorusInteroperability.Process.newBuilder().
        setRequestId("44580").
        setCorusPid("35645").
        build();
    
    CorusInteroperability.Message message = CorusInteroperability.Message.newBuilder().
        setHeader(ProtobufSerializer.wrapHeader(process)).
        addCommands(ProtobufSerializer.wrapCommand(confirmDumpRequest)).
        build();
    
    CorusInteroperability.Message deserializedMessage = cloneWithSerialization(message);
    Assert.assertNotNull(deserializedMessage);
    
    assertProcessHeader("44580", "35645", deserializedMessage.getHeader());
    Assert.assertEquals(1, deserializedMessage.getCommandsCount());
    assertConfirmDumpCommand("55658", deserializedMessage.getCommands(0));
  }

  @Test
  public void testAckResponse() throws Exception {
    CorusInteroperability.Ack ackResponse = CorusInteroperability.Ack.newBuilder().
        setCommandId("889988").
        build();
    
    CorusInteroperability.Server server = CorusInteroperability.Server.newBuilder().
        setRequestId("88456").
        setProcessingTime(221).
        build();
    
    CorusInteroperability.Message message = CorusInteroperability.Message.newBuilder().
        setHeader(ProtobufSerializer.wrapHeader(server)).
        addCommands(ProtobufSerializer.wrapCommand(ackResponse)).
        build();
    
    CorusInteroperability.Message deserializedMessage = cloneWithSerialization(message);
    Assert.assertNotNull(deserializedMessage);
    
    assertServerHeader("88456", 221, deserializedMessage.getHeader());
    Assert.assertEquals(1, deserializedMessage.getCommandsCount());
    assertAckCommand("889988", deserializedMessage.getCommands(0));
  }
  
  @Test
  public void testShutdownRequest() throws Exception {
    CorusInteroperability.Shutdown shutdownRequest = CorusInteroperability.Shutdown.newBuilder().
        setCommandId("55655").
        setRequestor(CorusInteroperability.Shutdown.RequestorActor.ADMIN).
        build();
    
    CorusInteroperability.Server server = CorusInteroperability.Server.newBuilder().
        setRequestId("88477").
        setProcessingTime(0).
        build();
    
    CorusInteroperability.Message message = CorusInteroperability.Message.newBuilder().
        setHeader(ProtobufSerializer.wrapHeader(server)).
        addCommands(ProtobufSerializer.wrapCommand(shutdownRequest)).
        build();
    
    CorusInteroperability.Message deserializedMessage = cloneWithSerialization(message);
    Assert.assertNotNull(deserializedMessage);
    
    assertServerHeader("88477", 0, deserializedMessage.getHeader());
    Assert.assertEquals(1, deserializedMessage.getCommandsCount());
    assertShutdownCommand("55655", CorusInteroperability.Shutdown.RequestorActor.ADMIN, deserializedMessage.getCommands(0));
  }
  
  @Test
  public void testDumpRequest() throws Exception {
    CorusInteroperability.Dump dumpRequest = CorusInteroperability.Dump.newBuilder().
        setCommandId("55657").
        setType(CorusInteroperability.Dump.DumpType.THREAD).
        setOutputFile("thread-dump.txt").
        build();
    
    CorusInteroperability.Server server = CorusInteroperability.Server.newBuilder().
        setRequestId("88479").
        setProcessingTime(0).
        build();
    
    CorusInteroperability.Message message = CorusInteroperability.Message.newBuilder().
        setHeader(ProtobufSerializer.wrapHeader(server)).
        addCommands(ProtobufSerializer.wrapCommand(dumpRequest)).
        build();
    
    CorusInteroperability.Message deserializedMessage = cloneWithSerialization(message);
    Assert.assertNotNull(deserializedMessage);
    
    assertServerHeader("88479", 0, deserializedMessage.getHeader());
    Assert.assertEquals(1, deserializedMessage.getCommandsCount());
    assertDumpCommand("55657", CorusInteroperability.Dump.DumpType.THREAD, "thread-dump.txt", deserializedMessage.getCommands(0));
  }
  
  @Test
  public void testErrorResponse() throws Exception {
    CorusInteroperability.Server server = CorusInteroperability.Server.newBuilder().
        setRequestId("1").
        setProcessingTime(10).
        build();
    
    CorusInteroperability.Message message = CorusInteroperability.Message.newBuilder().
        setHeader(ProtobufSerializer.wrapHeader(server)).
        setErrorCode("1009").
        setErrorMessage("Some error occured").
        setErrorDetail("... some detailed error message ...").
        build();
    
    CorusInteroperability.Message deserializedMessage = cloneWithSerialization(message);
    Assert.assertNotNull(deserializedMessage);
    
    assertServerHeader("1", 10, deserializedMessage.getHeader());
    Assert.assertEquals(0, deserializedMessage.getCommandsCount());
    Assert.assertEquals("1009", deserializedMessage.getErrorCode());
    Assert.assertEquals("Some error occured", deserializedMessage.getErrorMessage());
    Assert.assertEquals("... some detailed error message ...", deserializedMessage.getErrorDetail());
  }
  
  public static void assertProcessHeader(String eRequestId, String eCorusPid, CorusInteroperability.Header actual) {
    Assert.assertNotNull(actual);
    Assert.assertEquals(CorusInteroperability.Header.HeaderType.PROCESS, actual.getType());

    CorusInteroperability.Process dProcess = actual.getExtension(CorusInteroperability.Process.header);
    Assert.assertNotNull(dProcess);
    Assert.assertEquals(eRequestId, dProcess.getRequestId());
    Assert.assertEquals(eCorusPid, dProcess.getCorusPid());
  }
  
  public static void assertServerHeader(String eRequestId, int eProcessingTime, CorusInteroperability.Header actual) {
    Assert.assertNotNull(actual);
    Assert.assertEquals(CorusInteroperability.Header.HeaderType.SERVER, actual.getType());

    CorusInteroperability.Server dServer = actual.getExtension(CorusInteroperability.Server.header);
    Assert.assertNotNull(dServer);
    Assert.assertEquals(eRequestId, dServer.getRequestId());
    Assert.assertEquals(eProcessingTime, dServer.getProcessingTime());
  }
  
  public static void assertPollCommand(String eCommandId, CorusInteroperability.Command actual) {
    Assert.assertNotNull(actual);
    Assert.assertEquals(CorusInteroperability.Command.CommandType.POLL, actual.getType());

    CorusInteroperability.Poll aCommand = actual.getExtension(CorusInteroperability.Poll.command);
    Assert.assertNotNull(aCommand);
    Assert.assertEquals(eCommandId, aCommand.getCommandId());
  }
  
  public static void assertStatusCommand(String eCommandId, String[] eContextNames, String[][] eParams, CorusInteroperability.Command actual) {
    Assert.assertNotNull(actual);
    Assert.assertEquals(CorusInteroperability.Command.CommandType.STATUS, actual.getType());

    CorusInteroperability.Status aCommand = actual.getExtension(CorusInteroperability.Status.command);
    Assert.assertNotNull(aCommand);
    Assert.assertEquals(eCommandId, aCommand.getCommandId());
    Assert.assertEquals(eContextNames.length, aCommand.getContextsCount());

    for (int i = 0; i < eContextNames.length; i++) {
      CorusInteroperability.Status.Context aContext = aCommand.getContexts(i);
      Assert.assertNotNull(aContext);
      Assert.assertEquals(eContextNames[i], aContext.getName());
      Assert.assertEquals(eParams[i].length/2, aContext.getParamsCount());

      for (int j = 0; j < eParams[i].length/2; j++) {
        CorusInteroperability.Status.Param aParam = aContext.getParams(j);
        Assert.assertNotNull(aParam);
        Assert.assertEquals(eParams[i][j*2], aParam.getName());
        Assert.assertEquals(eParams[i][j*2+1], aParam.getValue());
      }
    }
  }
  
  public static void assertRestartCommand(String eCommandId, CorusInteroperability.Command actual) {
    Assert.assertNotNull(actual);
    Assert.assertEquals(CorusInteroperability.Command.CommandType.RESTART, actual.getType());

    CorusInteroperability.Restart aCommand = actual.getExtension(CorusInteroperability.Restart.command);
    Assert.assertNotNull(aCommand);
    Assert.assertEquals(eCommandId, aCommand.getCommandId());
  }
  
  public static void assertConfirmShutdownCommand(String eCommandId, CorusInteroperability.Command actual) {
    Assert.assertNotNull(actual);
    Assert.assertEquals(CorusInteroperability.Command.CommandType.CONFIRM_SHUTDOWN, actual.getType());

    CorusInteroperability.ConfirmShutdown aCommand = actual.getExtension(CorusInteroperability.ConfirmShutdown.command);
    Assert.assertNotNull(aCommand);
    Assert.assertEquals(eCommandId, aCommand.getCommandId());
  }
  
  public static void assertConfirmDumpCommand(String eCommandId, CorusInteroperability.Command actual) {
    Assert.assertNotNull(actual);
    Assert.assertEquals(CorusInteroperability.Command.CommandType.CONFIRM_DUMP, actual.getType());

    CorusInteroperability.ConfirmDump aCommand = actual.getExtension(CorusInteroperability.ConfirmDump.command);
    Assert.assertNotNull(aCommand);
    Assert.assertEquals(eCommandId, aCommand.getCommandId());
  }
  
  public static void assertAckCommand(String eCommandId, CorusInteroperability.Command actual) {
    Assert.assertNotNull(actual);
    Assert.assertEquals(CorusInteroperability.Command.CommandType.ACK, actual.getType());

    CorusInteroperability.Ack aCommand = actual.getExtension(CorusInteroperability.Ack.command);
    Assert.assertNotNull(aCommand);
    Assert.assertEquals(eCommandId, aCommand.getCommandId());
  }
  
  public static void assertShutdownCommand(String eCommandId, CorusInteroperability.Shutdown.RequestorActor eRequestor,
      CorusInteroperability.Command actual) {
    Assert.assertNotNull(actual);
    Assert.assertEquals(CorusInteroperability.Command.CommandType.SHUTDOWN, actual.getType());

    CorusInteroperability.Shutdown aCommand = actual.getExtension(CorusInteroperability.Shutdown.command);
    Assert.assertNotNull(aCommand);
    Assert.assertEquals(eCommandId, aCommand.getCommandId());
    Assert.assertEquals(eRequestor, aCommand.getRequestor());
  }
  
  public static void assertDumpCommand(String eCommandId, CorusInteroperability.Dump.DumpType eType, String eOutputFile,
      CorusInteroperability.Command actual) {
    Assert.assertNotNull(actual);
    Assert.assertEquals(CorusInteroperability.Command.CommandType.DUMP, actual.getType());

    CorusInteroperability.Dump aCommand = actual.getExtension(CorusInteroperability.Dump.command);
    Assert.assertNotNull(aCommand);
    Assert.assertEquals(eCommandId, aCommand.getCommandId());
    Assert.assertEquals(eType, aCommand.getType());
    Assert.assertEquals(eOutputFile, aCommand.getOutputFile());
  }
    
}
