package org.komparator.mediator.ws.it;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.ItemView;

public class GetItemsIT extends BaseIT{

  /* ===== Test Arguments ===== */

  @Test(expected = InvalidItemId_Exception.class)
  public void getItemsNullTextArgumentTest() throws InvalidItemId_Exception {
    mediatorClient.getItems(null);
  }

  @Test(expected = InvalidItemId_Exception.class)
  public void getItemsEmptyTextArgumentTest() throws InvalidItemId_Exception {
    mediatorClient.getItems("  \t\t \n\n ");
  }

  /* ===== Test any result ===== */

  @Test
  public void getItemsInvalidIdNotNullResultTest() throws InvalidItemId_Exception {
    List<ItemView> res = mediatorClient.getItems("TOTALLY-INVALID-ID");
    Assert.assertNotNull(res);
  }

  /* ===== Test quantities ===== */

  @Test
  public void getItemsInvalidIdZeroProductsTest() throws InvalidItemId_Exception {
    List<ItemView> res = mediatorClient.getItems("TOTALLY-INVALID-ID");
    Assert.assertEquals(0, res.size());
  }

  @Test
  public void getItemsNotExactSearchProductsTest() throws InvalidItemId_Exception {
    List<ItemView> res = mediatorClient.getItems("iPhone");
    Assert.assertEquals(0, res.size());
  }

  @Test
  public void getItemsOneResultTest() throws InvalidItemId_Exception {
    List<ItemView> res = mediatorClient.getItems("PixelXL");
    Assert.assertEquals(1, res.size());
  }

  @Test
  public void getItemsTwoResultsTest() throws InvalidItemId_Exception {
    List<ItemView> res = mediatorClient.getItems("iPhone6S");
    Assert.assertEquals(2, res.size());
  }

  /* ===== Test order ===== */

  @Test
  public void getItemsSortedByPriceTest() throws InvalidItemId_Exception {
    List<ItemView> res = mediatorClient.getItems("iPhone7+");
    Assert.assertEquals(2, res.size());
    Assert.assertTrue(res.get(0).getPrice() < res.get(1).getPrice());
  }

}
