package org.komparator.mediator.ws.it;

import org.junit.Assert;
import org.junit.Test;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.ItemView;

import java.util.List;

public class GetItemsIT extends BaseIT {

  /* ===== Test Arguments ===== */

  @Test(expected = InvalidItemId_Exception.class)
  public void nullTextArgumentTest() throws InvalidItemId_Exception {
    mediatorClient.getItems(null);
  }

  @Test(expected = InvalidItemId_Exception.class)
  public void emptyTextArgumentTest() throws InvalidItemId_Exception {
    mediatorClient.getItems("  \t\t \n\n ");
  }

  /* ===== Test any result ===== */

  @Test
  public void invalidIdTest() throws InvalidItemId_Exception {
    List<ItemView> res = mediatorClient.getItems("TOTALLY-INVALID-ID");
    Assert.assertNotNull(res);
  }

  @Test
  public void invalidIdValidDescTest() throws InvalidItemId_Exception {
    List<ItemView> res = mediatorClient.getItems("Apple");
    Assert.assertNotNull(res);
  }

  /* ===== Test quantities ===== */

  @Test
  public void invalidIdZeroProductsTest() throws InvalidItemId_Exception {
    List<ItemView> res = mediatorClient.getItems("TOTALLY-INVALID-ID");
    Assert.assertEquals(0, res.size());
  }

  @Test
  public void notExactSearchProductsTest() throws InvalidItemId_Exception {
    List<ItemView> res = mediatorClient.getItems("iPhone");
    Assert.assertEquals(0, res.size());
  }

  @Test
  public void oneResultTest() throws InvalidItemId_Exception {
    List<ItemView> res = mediatorClient.getItems("PixelXL");
    Assert.assertEquals(1, res.size());
  }

  @Test
  public void twoResultsTest() throws InvalidItemId_Exception {
    List<ItemView> res = mediatorClient.getItems("iPhone6S");
    Assert.assertEquals(2, res.size());
  }

  /* ===== Test order ===== */

  @Test
  public void exactSortedByPriceTest() throws InvalidItemId_Exception {
    List<ItemView> res = mediatorClient.getItems("iPhone7+");
    Assert.assertEquals(2, res.size());

    Assert.assertEquals("iPhone7+", res.get(0).getItemId().getProductId());
    Assert.assertEquals("T50_Supplier1", res.get(0).getItemId().getSupplierId());

    Assert.assertEquals("iPhone7+", res.get(1).getItemId().getProductId());
    Assert.assertEquals("T50_Supplier2", res.get(1).getItemId().getSupplierId());
  }

  @Test
  public void allSortedByPriceTest() throws InvalidItemId_Exception {
    List<ItemView> res = mediatorClient.getItems("iPhone6");
    Assert.assertEquals(2, res.size());
    Assert.assertTrue(res.get(0).getPrice() <= res.get(1).getPrice());
  }

  @Test
  public void allSortedByPriceTest2() throws InvalidItemId_Exception {
    List<ItemView> res = mediatorClient.getItems("iPhone6S");
    Assert.assertEquals(2, res.size());
    Assert.assertTrue(res.get(0).getPrice() <= res.get(1).getPrice());
  }

}
