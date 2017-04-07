package org.komparator.mediator.ws.it;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;

public class GetItemsIT extends BaseIT{

  @Test
  public void getItemsTest() throws InvalidItemId_Exception{
    assertNotNull("mediator not getting items", mediatorClient.getItems("potatoes"));

  }

}
