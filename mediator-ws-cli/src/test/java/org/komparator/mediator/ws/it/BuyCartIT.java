package org.komparator.mediator.ws.it;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.komparator.mediator.ws.EmptyCart_Exception;
import org.komparator.mediator.ws.InvalidCartId_Exception;
import org.komparator.mediator.ws.InvalidCreditCard_Exception;
import org.komparator.mediator.ws.InvalidItemId;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.InvalidQuantity_Exception;
import org.komparator.mediator.ws.InvalidText_Exception;
import org.komparator.mediator.ws.ItemIdView;
import org.komparator.mediator.ws.ItemView;
import org.komparator.mediator.ws.NotEnoughItems;
import org.komparator.mediator.ws.NotEnoughItems_Exception;

public class BuyCartIT extends BaseIT {


  @Test(expected = InvalidText_Exception.class)
  public void nullTextArgumentTest()
      throws InvalidCartId_Exception, EmptyCart_Exception, InvalidCreditCard_Exception {

    mediatorClient.buyCart(null, "4556648855991861");
  }

  @Test(expected = InvalidCreditCard_Exception.class)
  public void invalidTextArgumentBuyCartTest()
      throws InvalidCartId_Exception, EmptyCart_Exception, InvalidCreditCard_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception, InvalidItemId_Exception {

    ItemIdView itemId = new ItemIdView();
    itemId.setProductId("Batata");
    itemId.setSupplierId("T50_Supplier1");

    mediatorClient.addToCart("0", itemId, 5);

    mediatorClient.buyCart("0", null);
  }

  @Test(expected = InvalidCartId_Exception.class)
  public void emptyTextArgumentTest()
      throws InvalidText_Exception, InvalidCreditCard_Exception, EmptyCart_Exception, InvalidCartId_Exception {
    mediatorClient.buyCart(null, null);
  }

  @Test
  public void addCartsTest() {
    ItemIdView itemId = new ItemIdView(  );
    itemId.setProductId("Batata");
    itemId.setSupplierId("T50_Supplier1");

    try {
      mediatorClient.addToCart("0", itemId ,5 );
    } catch (InvalidCartId_Exception | InvalidItemId_Exception |InvalidQuantity_Exception | NotEnoughItems_Exception e) {
      assertTrue(false);
    }
  }

}
