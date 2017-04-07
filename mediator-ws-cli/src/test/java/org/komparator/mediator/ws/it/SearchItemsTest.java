package org.komparator.mediator.ws.it;

import org.junit.Assert;
import org.junit.Test;
import org.komparator.mediator.ws.InvalidText_Exception;
import org.komparator.mediator.ws.ItemView;

import java.util.List;

public class SearchItemsTest extends BaseIT {

    /*@Test
    public void searchOneItemTest() throws InvalidText_Exception {
        List<ItemView> res = mediatorClient.searchItems("Batatas");
        Assert.assertEquals(4, res.size());
    }

    @Test
    public void searchAllItemsTest() throws InvalidText_Exception {
        List<ItemView> res = mediatorClient.searchItems("sabem bem");
        Assert.assertEquals(4, res.size());
    }*/

    @Test(expected = InvalidText_Exception.class)
    public void nullTextArgumentTest() throws InvalidText_Exception {
        mediatorClient.searchItems(null);
    }

    @Test(expected = InvalidText_Exception.class)
    public void emptyTextArgumentTest() throws InvalidText_Exception {
        mediatorClient.searchItems("  \t\t \n\n ");
    }

}
