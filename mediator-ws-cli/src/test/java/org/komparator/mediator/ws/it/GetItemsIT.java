package org.komparator.mediator.ws.it;

import org.junit.Assert;
import org.junit.Test;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.ItemView;

import java.util.List;

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
  public void getItemsExactSortedByPriceTest() throws InvalidItemId_Exception {
    List<ItemView> res = mediatorClient.getItems("iPhone7+");
    Assert.assertEquals(2, res.size());

    Assert.assertEquals("iPhone7+", res.get(0).getItemId().getProductId());
    Assert.assertEquals("T50_Supplier1", res.get(0).getItemId().getSupplierId());

    Assert.assertEquals("iPhone7+", res.get(1).getItemId().getProductId());
    Assert.assertEquals("T50_Supplier2", res.get(1).getItemId().getSupplierId());
  }

  @Test
  public void getItemsAllSortedByPriceTest() throws InvalidItemId_Exception {
    List<ItemView> res = mediatorClient.getItems("iPhone6S");
    Assert.assertEquals(2, res.size());

    for (int i = 0; i < res.size() - 1; i++) {
      Assert.assertTrue(res.get(i).getPrice() <= res.get(i + 1).getPrice());
    }
  }

}
