package org.komparator.supplier.ws;

/** Main class that starts the Supplier Web Service. */
public class SupplierApp {

	public static void main(String[] args) throws Exception {
		// Check arguments
		if (args.length < 3) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + SupplierApp.class.getName() + " WS-URL UDDI-URL WS-NAME");
			return;
		}

		String wsURL = args[0];
		String uddiURL = args[1];
		String wsName = args[2];

		// Create server implementation object
		SupplierEndpointManager endpoint = null;
		try {
			endpoint = new SupplierEndpointManager(wsURL, uddiURL, wsName);
			endpoint.start();
			endpoint.awaitConnections();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			endpoint.stop();
		}
	}

}
