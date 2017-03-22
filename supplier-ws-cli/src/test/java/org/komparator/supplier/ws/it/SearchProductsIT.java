package org.komparator.supplier.ws.it;

import org.junit.*;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.BadText_Exception;
import org.komparator.supplier.ws.ProductView;

import java.util.List;

/**
 * Test suite
 */
public class SearchProductsIT extends BaseIT {

    // static members

    // one-time initialization and clean-up
    @BeforeClass
    public static void oneTimeSetUp() throws BadProduct_Exception, BadProductId_Exception {
        client.clear();

        {
            ProductView product = new ProductView();
            product.setId("IP7");
            product.setDesc("iPhone7");
            product.setPrice(1000);
            product.setQuantity(10);
            client.createProduct(product);
        }
        {
            ProductView product = new ProductView();
            product.setId("IP6");
            product.setDesc("iPhone6");
            product.setPrice(900);
            product.setQuantity(20);
            client.createProduct(product);
        }
        {
            ProductView product = new ProductView();
            product.setId("IP5");
            product.setDesc("iPhone5");
            product.setPrice(800);
            product.setQuantity(30);
            client.createProduct(product);
        }

    }

    @AfterClass
    public static void oneTimeTearDown() {
        client.clear();
    }

    // members

    // initialization and clean-up for each test
    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // tests
    // assertEquals(expected, actual);

    // public List<ProductView> searchProducts(String descText) throws
    // BadText_Exception

    // bad input tests

    @Test(expected = BadText_Exception.class)
    public void searchProductsNullTest() throws BadText_Exception {
        client.searchProducts(null);
    }

    @Test(expected = BadText_Exception.class)
    public void searchProductsEmptyTest() throws BadText_Exception {
        client.searchProducts("");
    }

    @Test(expected = BadText_Exception.class)
    public void searchProductsWhiteTextTest() throws BadText_Exception {
        client.searchProducts("  \t\n\t\n  ");
    }

    // main tests

    @Test
    public void searchProductsExistsSingleTest() throws BadText_Exception {
        List<ProductView> res = client.searchProducts("iPhone7");
        Assert.assertEquals(1, res.size());
    }

    @Test
    public void searchProductsExistsMultipleTest() throws BadText_Exception {
        List<ProductView> res = client.searchProducts("iPhone");
        Assert.assertEquals(3, res.size());
    }

    @Test
    public void searchProductsExistsNoneTest() throws BadText_Exception {
        List<ProductView> res = client.searchProducts("Nexus 6P");
        Assert.assertEquals(0, res.size());
    }

}
