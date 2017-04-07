package org.komparator.mediator.ws.it;

import java.io.IOException;
import java.util.Properties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.komparator.mediator.ws.cli.MediatorClient;
import org.komparator.supplier.ws.ProductView;
import org.komparator.supplier.ws.cli.SupplierClient;
import org.komparator.supplier.ws.cli.SupplierClientException;

public class BaseIT {

	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps;

	protected static MediatorClient mediatorClient;
	protected static SupplierClient supplier1;
	protected static SupplierClient supplier2;

	@BeforeClass
	public static void oneTimeSetup() throws Exception {
		testProps = new Properties();
		try {
			testProps.load(BaseIT.class.getResourceAsStream(TEST_PROP_FILE));
			System.out.println("Loaded test properties:");
			System.out.println(testProps);
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
			throw e;
		}

		String uddiEnabled = testProps.getProperty("uddi.enabled");
		String uddiURL = testProps.getProperty("uddi.url");
		String wsName = testProps.getProperty("ws.name");
		String wsURL = testProps.getProperty("ws.url");

		if ("true".equalsIgnoreCase(uddiEnabled)) {
			mediatorClient = new MediatorClient(uddiURL, wsName);
		} else {
			mediatorClient = new MediatorClient(wsURL);
		}

	}

	@AfterClass
	public static void cleanup() {
	}

	@BeforeClass
	public static void populate() throws SupplierClientException {
		supplier1 = new SupplierClient("http://t50:WkyodoJT@uddi.sd.rnl.tecnico.ulisboa.pt:9090/",
				"SupplierTest1");
		supplier2 = new SupplierClient("http://t50:WkyodoJT@uddi.sd.rnl.tecnico.ulisboa.pt:9090/",
				"SupplierTest2");

		ProductView product1 = new ProductView();
		ProductView product2 = new ProductView();
		ProductView product3 = new ProductView();
		ProductView product4 = new ProductView();

		product1.setId("Batata");
		product1.setDesc("Batatas sabem bem");
		product1.setPrice(1);
		product1.setQuantity(10);

		product2.setId("Maca");
		product2.setDesc("Macas sabem bem");
		product2.setPrice(2);
		product2.setQuantity(5);

		product3.setId("Banana");
		product3.setDesc("Bananas sabem bem");
		product3.setPrice(3);
		product3.setQuantity(30);

		product4.setId("Batata");
		product4.setDesc("Batatas sabem bem");
		product4.setPrice(2);
		product4.setQuantity(20);

		try {
			supplier1.createProduct(product1);
			supplier1.createProduct(product2);
			supplier2.createProduct(product3);
			supplier2.createProduct(product4);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}