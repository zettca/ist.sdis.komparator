package org.komparator.mediator.ws.it;

import org.junit.Assert;
import org.junit.Test;
import org.komparator.mediator.ws.InvalidText_Exception;
import org.komparator.mediator.ws.ItemView;

import java.util.ArrayList;
import java.util.Collections;
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
        List<ItemView> sortedRes = new ArrayList<>(res);
        Collections.sort(sortedRes, (it1, it2) -> {
            int cmp = it1.getItemId().getProductId().compareTo(it2.getItemId().getProductId());
            return (cmp == 0) ? it2.getPrice() - it1.getPrice() : cmp;
        });
        Assert.assertArrayEquals(sortedRes.toArray(), res.toArray());
    }

    @Test
    public void searchItemsAllNamesSortedTest() throws InvalidText_Exception {
        List<ItemView> res = mediatorClient.searchItems("SmartPhone");
        List<ItemView> sortedRes = new ArrayList<>(res);
        Collections.sort(sortedRes, (it1, it2) -> {
            int cmp = it1.getItemId().getProductId().compareTo(it2.getItemId().getProductId());
            return (cmp == 0) ? it2.getPrice() - it1.getPrice() : cmp;
        });
        Assert.assertArrayEquals(sortedRes.toArray(), res.toArray());
    }


}
