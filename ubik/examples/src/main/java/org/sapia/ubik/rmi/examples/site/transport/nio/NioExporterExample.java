package org.sapia.ubik.rmi.examples.site.transport.nio;

import org.sapia.ubik.rmi.server.transport.mina.NioServerExporter;

public class NioExporterExample {
	
	public static void main(String[] args) {
	  
		NioServerExporter exporter = new NioServerExporter();
		exporter.port(7070);
		// Hello stub = exporter.export(new HelloImpl());
		
		
		
		
  }

}
