package org.komparator.mediator.ws.it;

import org.junit.Assert;
import org.junit.Test;
import org.komparator.mediator.ws.InvalidText_Exception;
import org.komparator.mediator.ws.ItemView;

import java.util.List;

public class SearchItemsIT extends BaseIT {

    /* ===== Test Arguments ===== */

    @Test(expected = InvalidText_Exception.class)
    public void searchItemsNullTextArgumentTest() throws InvalidText_Exception {
        mediatorClient.searchItems(null);
    }

    @Test(expected = InvalidText_Exception.class)
    public void searchItemsEmptyTextArgumentTest() throws InvalidText_Exception {
        mediatorClient.searchItems("  \t\t \n\n ");
    }

    /* ===== Test any result ===== */

    @Test
    public void searchItemsNotNullResultTest() throws InvalidText_Exception {
        List<ItemView> res = mediatorClient.searchItems("NO-MATCHES?");
        Assert.assertNotNull(res);
    }

    @Test
    public void searchItemsAnyResultTest() throws InvalidText_Exception {
        List<ItemView> res = mediatorClient.searchItems("NO-MATCHES");
        Assert.assertEquals(0, res.size());
    }

    /* ===== Test quantities ===== */

    @Test
    public void searchItemsOneResultTest() throws InvalidText_Exception {
        List<ItemView> res = mediatorClient.searchItems("Google Pixel C");
        Assert.assertEquals(1, res.size());
    }

    @Test
    public void searchItemsAllPrefixedResultsTest() throws InvalidText_Exception {
        List<ItemView> res1 = mediatorClient.searchItems("Apple");
        List<ItemView> res2 = mediatorClient.searchItems("Google Pixel");
        Assert.assertEquals(6, res1.size());
        Assert.assertEquals(3, res2.size());
    }

    @Test
    public void searchItemsMatchSomeResultsTest() throws InvalidText_Exception {
        List<ItemView> res = mediatorClient.searchItems("iPhone 6");
        Assert.assertEquals(4, res.size());
    }

    /* ===== Test order ===== */

    @Test
    public void searchItemsPricesSortedTest() throws InvalidText_Exception {
        List<ItemView> res = mediatorClient.searchItems("iPhone 6S");

        Assert.assertEquals(2, res.size());

        Assert.assertEquals("iPhone6S", res.get(0).getItemId().getProductId());
        Assert.assertEquals("T50_Supplier1", res.get(0).getItemId().getSupplierId());

        Assert.assertEquals("iPhone6S", res.get(1).getItemId().getProductId());
        Assert.assertEquals("T50_Supplier2", res.get(1).getItemId().getSupplierId());
    }

    @Test
    public void searchItemsNamesAndPricesSortedTest() throws InvalidText_Exception {
        List<ItemView> res = mediatorClient.searchItems("iPhone 6");

        Assert.assertEquals(4, res.size());
        Assert.assertTrue(isOrdered(res));
    }

    @Test
    public void searchItemsAllNamesAndPricesSortedTest() throws InvalidText_Exception {
        List<ItemView> res = mediatorClient.searchItems("SmartPhone");

        Assert.assertEquals(9, res.size());
        Assert.assertTrue(isOrdered(res));
    }


    private boolean isOrdered(List<ItemView> items) {
        for (int i = 0; i < items.size() - 1; i++) {
            int cmp = items.get(i).getItemId().getProductId().compareTo(items.get(i + 1).getItemId().getProductId());
            if (cmp > 0) {
                return false;
            } else if (cmp == 0 && items.get(i).getPrice() > items.get(i + 1).getPrice()) {
                return false;
            }
        }
        return true;
    }


}
