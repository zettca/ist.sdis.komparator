package org.komparator.mediator.ws.it;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;


import java.util.List;
import org.junit.Test;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.ItemView;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;

public class GetItemsIT extends BaseIT{

  @Test
  public void getItemsTest() throws InvalidItemId_Exception{
    assertNotNull("mediator not getting items", mediatorClient.getItems("Banana"));

  }

  @Test
  public void getTwoItemsTest() throws InvalidItemId_Exception{
    List<ItemView> list = mediatorClient.getItems("Batata");

    assertEquals(list.get(0).getItemId().getProductId(), "Batata");
    assertEquals(list.get(0).getPrice(), 1);
    assertEquals(list.get(0).getItemId().getSupplierId(), "SupplierTest1");

    assertEquals(list.get(1).getItemId().getProductId(), "Batata");
    assertEquals(list.get(0).getPrice(), 2);
    assertEquals(list.size(),2);
    assertEquals(list.get(0).getItemId().getSupplierId(), "SupplierTest2");
  }




}
