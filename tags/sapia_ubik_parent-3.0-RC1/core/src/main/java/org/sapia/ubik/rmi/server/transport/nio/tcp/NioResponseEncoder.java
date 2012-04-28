package org.sapia.ubik.rmi.server.transport.nio.tcp;

import java.io.IOException;
import java.io.ObjectOutputStream;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.sapia.ubik.rmi.Consts;
import org.sapia.ubik.rmi.server.transport.MarshalStreamFactory;
import org.sapia.ubik.rmi.server.transport.RmiObjectOutput;
import org.sapia.ubik.util.ByteBufferOutputStream;
import org.sapia.ubik.util.Props;

/**
 * An encoder of Ubik server responses.
 * 
 * @author yduchesne
 *
 */
public class NioResponseEncoder implements ProtocolEncoder {
	
  private static final int BUFSIZE = Props.getSystemProperties().getIntProperty(
                                  			Consts.MARSHALLING_BUFSIZE, 
                                  			Consts.DEFAULT_MARSHALLING_BUFSIZE
                                  	 );

  private static final String ENCODER_STATE = "ENCODER_STATE";
  
  private static final int BYTES_PER_INT = 4;

  // --------------------------------------------------------------------------
  
  private static class EncoderState {
  	
    private ByteBuffer 				 outgoing;
    private ObjectOutputStream stream;
    
    private EncoderState() throws IOException {
    	outgoing = ByteBuffer.allocate(BUFSIZE);
    	outgoing.setAutoExpand(true);
    }
    
    private ObjectOutputStream getObjectOutputStream() throws IOException {
    	if (stream == null) {
    		stream = MarshalStreamFactory.createOutputStream(new ByteBufferOutputStream(outgoing));
    	}
    	return stream;
    }
    
  }
  
  // --------------------------------------------------------------------------
  
  public void encode(IoSession sess, Object toEncode, ProtocolEncoderOutput output) throws Exception {
    
    EncoderState es = (EncoderState)sess.getAttribute(ENCODER_STATE);
    if (es == null) {
      es = new EncoderState();
      sess.setAttribute(ENCODER_STATE, es);
    }
    es.outgoing.clear();
    NioResponse resp = (NioResponse)toEncode;
    es.outgoing.putInt(0); // reserve space for length header
    doEncode(resp, es.outgoing, es.getObjectOutputStream(), output);
  }
  
  void doEncode(NioResponse toEncode, ByteBuffer outputBuffer, ObjectOutputStream outputStream, ProtocolEncoderOutput output) throws Exception {
    ((RmiObjectOutput)outputStream).setUp(toEncode.getAssociatedVmId(), toEncode.getTransportType());
    outputStream.writeObject(toEncode.getObject());
    outputStream.flush();
    outputBuffer.putInt(0, outputBuffer.position() - BYTES_PER_INT); // setting length at reserved space
    outputBuffer.flip();
    output.write(outputBuffer);  	
  }
  
  public void dispose(IoSession sess) throws Exception {
    EncoderState es = (EncoderState)sess.getAttribute(ENCODER_STATE);  	
    if (es != null) {
      es.outgoing.release();
    }
  }
}
