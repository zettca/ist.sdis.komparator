package org.komparator.mediator.ws.it;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.komparator.mediator.ws.cli.MediatorClient;
import org.komparator.supplier.ws.ProductView;
import org.komparator.supplier.ws.cli.SupplierClient;
import org.komparator.supplier.ws.cli.SupplierClientException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class BaseIT {

	private static final String TEST_PROP_FILE = "/test.properties";
	private static final String uddiURL = "http://t50:WkyodoJT@uddi.sd.rnl.tecnico.ulisboa.pt:9090/";
	static Properties testProps;

	static MediatorClient mediatorClient;
	static SupplierClient supplierAll;
	static SupplierClient supplierApple;

	protected static List<ProductView> productList = new ArrayList<>();

	@BeforeClass
	public static void setupProperties() throws Exception {
		testProps = new Properties();
		try {
			testProps.load(BaseIT.class.getResourceAsStream(TEST_PROP_FILE));
			System.out.println("Loaded test properties: ");
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

	@BeforeClass
	public static void setupSupplierClients() throws SupplierClientException {
		supplierAll = new SupplierClient(uddiURL, "T50_Supplier1");
		supplierApple = new SupplierClient(uddiURL, "T50_Supplier2");
	}

	@Before
	public void populateClients() {
		List<ProductView> appleProducts = new ArrayList<>();
		ProductView product1 = new ProductView();
		ProductView product2 = new ProductView();
		ProductView product3 = new ProductView();
		ProductView product4 = new ProductView();
		ProductView product5 = new ProductView();
		ProductView product6 = new ProductView();

		product1.setId("iPhone6");
		product1.setDesc("SmartPhone Apple iPhone 6");
		product1.setPrice(500);
		product1.setQuantity(30);
		productList.add(product1);
		appleProducts.add(product1);

		product2.setId("iPhone6S");
		product2.setDesc("SmartPhone Apple iPhone 6S");
		product2.setPrice(600);
		product2.setQuantity(20);
		productList.add(product2);
		appleProducts.add(product2);

		product3.setId("iPhone7+");
		product3.setDesc("SmartPhone Apple iPhone 7 Plus");
		product3.setPrice(700);
		product3.setQuantity(20);
		productList.add(product3);
		appleProducts.add(product3);


		product4.setId("PixelC");
		product4.setDesc("SmartPhone Google Pixel C");
		product4.setPrice(1200);
		product4.setQuantity(40);
		productList.add(product4);

		product5.setId("Pixel");
		product5.setDesc("SmartPhone Google Pixel");
		product5.setPrice(650);
		product5.setQuantity(10);
		productList.add(product5);

		product6.setId("PixelXL");
		product6.setDesc("SmartPhone Google Pixel XL");
		product6.setPrice(750);
		product6.setQuantity(20);
		productList.add(product6);

		try {
			for (ProductView product : productList) {
				supplierAll.createProduct(product);
			}
			for (ProductView product : appleProducts) {
				product.setPrice(product.getPrice() + 199);
				product.setQuantity(200);
				supplierApple.createProduct(product);
			}
		} catch (Exception e) {
			System.out.println("Error creating products in suppliers. Are suppliers running?");
			System.out.println("Message: " + e.getMessage());
		}
	}

	@After
	public void cleanup() {
		supplierApple.clear();
		supplierAll.clear();
	}


}