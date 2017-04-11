package org.komparator.mediator.ws.it;

import org.junit.Assert;
import org.junit.Test;
import org.komparator.mediator.ws.InvalidText_Exception;
import org.komparator.mediator.ws.ItemIdView;
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
        Assert.assertEquals(2, res.size());
    }

    /* ===== Test order ===== */

    @Test
    public void searchItemsNamesSortedTest() throws InvalidText_Exception {
        List<ItemView> res = mediatorClient.searchItems("iPhone 6");

        Assert.assertEquals("iPhone6", res.get(0).getItemId().getProductId());
        Assert.assertEquals("iPhone6S", res.get(1).getItemId().getProductId());
    }

    @Test
    public void searchItemsAllNamesSortedTest() throws InvalidText_Exception {
        List<ItemView> res = mediatorClient.searchItems("iPhone");
        ItemIdView itemIdView = null;

        Assert.assertEquals(6, res.size());

        itemIdView = res.get(0).getItemId();
        Assert.assertEquals("iPhone6", itemIdView.getProductId());
        Assert.assertEquals("T50_Supplier1", itemIdView.getSupplierId());

        itemIdView = res.get(1).getItemId();
        Assert.assertEquals("iPhone6", itemIdView.getProductId());
        Assert.assertEquals("T50_Supplier2", itemIdView.getSupplierId());

        itemIdView = res.get(2).getItemId();
        Assert.assertEquals("iPhone6S", itemIdView.getProductId());
        Assert.assertEquals("T50_Supplier1", itemIdView.getSupplierId());

        itemIdView = res.get(3).getItemId();
        Assert.assertEquals("iPhone6S", itemIdView.getProductId());
        Assert.assertEquals("T50_Supplier2", itemIdView.getSupplierId());

        itemIdView = res.get(4).getItemId();
        Assert.assertEquals("iPhone7+", itemIdView.getProductId());
        Assert.assertEquals("T50_Supplier1", itemIdView.getSupplierId());

        itemIdView = res.get(5).getItemId();
        Assert.assertEquals("iPhone7+", itemIdView.getProductId());
        Assert.assertEquals("T50_Supplier2", itemIdView.getSupplierId());

    }


}
