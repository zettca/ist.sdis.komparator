package org.komparator.supplier.ws.it;

import org.junit.*;
import org.komparator.supplier.ws.*;

/**
 * Test suite
 */
public class BuyProductIT extends BaseIT {

	// static members

	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() {
		client.clear();
	}

	@AfterClass
	public static void oneTimeTearDown() {
		client.clear();
	}

	// members

	// initialization and clean-up for each test
	@Before
	public void setUp() throws BadProduct_Exception, BadProductId_Exception {
		{
			ProductView product = new ProductView();
			product.setId("SK8");
			product.setDesc("Snickers");
			product.setPrice(1);
			product.setQuantity(4);
			client.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("MR2");
			product.setDesc("Mars");
			product.setPrice(2);
			product.setQuantity(10);
			client.createProduct(product);
		}
	}

	@After
	public void tearDown() {
		client.clear();
	}

	// tests
	// assertEquals(expected, actual);

	// public String buyProduct(String productId, int quantity)
	// throws BadProductId_Exception, BadQuantity_Exception,
	// InsufficientQuantity_Exception {

	// bad input tests

	@Test(expected = BadProductId_Exception.class)
	public void buyProductNullIdTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct(null, 1);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductNonExistingIdTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("SK9001", 1);
	}

	@Test(expected = BadQuantity_Exception.class)
	public void buyProductNegativeAmountTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("SK8", -10);
	}

	@Test(expected = BadQuantity_Exception.class)
	public void buyProductZeroAmountTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("SK8", 0);
	}

	@Test(expected = InsufficientQuantity_Exception.class)
	public void buyProductLudicrousAmountIdTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("SK8", 1000000);
	}

	@Test(expected = InsufficientQuantity_Exception.class)
	public void buyProductOverloadAmountTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("MR2", 12);
	}

	// main tests

	@Test
	public void buyProductValidAmountTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		String productId = "MR2";
		client.buyProduct(productId, 2);
		Assert.assertEquals(8, client.getProduct(productId).getQuantity());
	}

	@Test
	public void buyProductMaxAmountTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		String productId = "SK8";
		client.buyProduct(productId, 4);
		Assert.assertEquals(0, client.getProduct(productId).getQuantity());
	}

	@Test
	public void buyProductOverLimitUndoTest()
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		String productId = "SK8";
		int quantityBefore = client.getProduct(productId).getQuantity();
		try {
			client.buyProduct(productId, 8);
			Assert.fail();
		} catch (InsufficientQuantity_Exception e) {
			Assert.assertEquals(quantityBefore, client.getProduct(productId).getQuantity());
		} catch (Exception e) {
			Assert.fail();
		}
	}

}
