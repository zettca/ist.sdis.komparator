package org.komparator.supplier.ws;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

import javax.xml.ws.Endpoint;

/** Main class that starts the Supplier Web Service. */
public class SupplierApp {

	public static void main(String[] args) throws Exception {
		// Check arguments
		if (args.length < 3) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + SupplierApp.class.getName() + " wsURL");
			return;
		}

		String wsURL = args[0];
		String uddiURL = args[1];
		String wsName = args[2];

		// Create server implementation object
		SupplierEndpointManager endpoint = new SupplierEndpointManager(wsURL, uddiURL, wsName);
		try {
			endpoint.start();
			endpoint.awaitConnections();
		} finally {
			endpoint.stop();
		}
	}

}
