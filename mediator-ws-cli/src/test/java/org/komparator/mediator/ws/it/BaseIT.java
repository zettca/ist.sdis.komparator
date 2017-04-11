package org.komparator.mediator.ws.it;

import org.junit.AfterClass;
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
	protected static Properties testProps;

	protected static MediatorClient mediatorClient;
	protected static SupplierClient supplierAll;
	protected static SupplierClient supplierApple;

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
		supplierAll = new SupplierClient(uddiURL, "T50_Supplier1");
		supplierApple = new SupplierClient(uddiURL, "T50_Supplier2");

		List<ProductView> products = new ArrayList<>();
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
		products.add(product1);
		appleProducts.add(product1);

		product2.setId("iPhone6S");
		product2.setDesc("SmartPhone Apple iPhone 6S");
		product2.setPrice(600);
		product2.setQuantity(20);
		products.add(product2);
		appleProducts.add(product2);

		product3.setId("iPhone7+");
		product3.setDesc("SmartPhone Apple iPhone 7 Plus");
		product3.setPrice(700);
		product3.setQuantity(20);
		products.add(product3);
		appleProducts.add(product3);

		/* === Brand Separator? === */

		product4.setId("Pixel");
		product4.setDesc("SmartPhone Google Pixel");
		product4.setPrice(650);
		product4.setQuantity(1);
		products.add(product4);

		product5.setId("PixelXL");
		product5.setDesc("SmartPhone Google Pixel XL");
		product5.setPrice(750);
		product5.setQuantity(2);
		products.add(product5);

		product6.setId("PixelC");
		product6.setDesc("SmartPhone Google Pixel C");
		product6.setPrice(1200);
		product6.setQuantity(4);
		products.add(product6);

		try {
			for (ProductView product : products) {
				supplierAll.createProduct(product);
			}

			for (ProductView product : appleProducts) {
				product.setPrice(product.getPrice() + 100);
				product.setQuantity(100);
				supplierApple.createProduct(product);
			}

		} catch (Exception e) {
			System.out.println("Error creating products in suppliers. Are suppliers running?");
			System.out.println("Message: " + e.getMessage());
		}

	}

}