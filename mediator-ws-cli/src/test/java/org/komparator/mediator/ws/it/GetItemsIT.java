package org.komparator.mediator.ws.it;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;


import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.ItemView;

public class GetItemsIT extends BaseIT{

  @Test
  public void getItemTest() throws InvalidItemId_Exception {
    assertNotNull(mediatorClient.getItems("Banana"));
  }

  @Test
  public void getTwoItemsTest() throws InvalidItemId_Exception {
    List<ItemView> list = mediatorClient.getItems("Batata");
    Assert.assertEquals(2, list.size());
  }

  @Test
  public void getAllItemsTest() throws InvalidItemId_Exception {
    List<ItemView> res = mediatorClient.getItems("Batata");
    Assert.assertEquals(2, res.size());
  }

  @Test(expected = InvalidItemId_Exception.class)
  public void nullTextArgumentTest() throws InvalidItemId_Exception {
    mediatorClient.getItems(null);
  }

  @Test(expected = InvalidItemId_Exception.class)
  public void emptyTextArgumentTest() throws InvalidItemId_Exception {
    mediatorClient.getItems("  \t\t \n\n ");
  }

}
