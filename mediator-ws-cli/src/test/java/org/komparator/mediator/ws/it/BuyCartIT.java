package org.komparator.mediator.ws.it;


import org.junit.Test;
import org.komparator.mediator.ws.EmptyCart_Exception;
import org.komparator.mediator.ws.InvalidCartId_Exception;
import org.komparator.mediator.ws.InvalidCreditCard_Exception;

public class BuyCartIT extends BaseIT {
    private final String creditCardNr = "4556648855991861";

    /* ===== Test Arguments ===== */

    @Test(expected = InvalidCartId_Exception.class)
    public void buyCartNullCartIdTest() throws InvalidCartId_Exception, EmptyCart_Exception, InvalidCreditCard_Exception {
        mediatorClient.buyCart(null, creditCardNr);
    }

    @Test(expected = InvalidCartId_Exception.class)
    public void buyCartEmptyCartIdTest() throws InvalidCartId_Exception, EmptyCart_Exception, InvalidCreditCard_Exception {
        mediatorClient.buyCart("  \n\n  \n\t\t ", creditCardNr);
    }

    @Test(expected = InvalidCartId_Exception.class)
    public void buyCartInvalidCartIdTest() throws InvalidCartId_Exception, EmptyCart_Exception, InvalidCreditCard_Exception {
        mediatorClient.buyCart("NON-EXISTING-CART", creditCardNr);
    }

    // TODO: something about this EmptyCart_Exception

    // TODO: create Cart1 with items or somethin
    @Test(expected = InvalidCreditCard_Exception.class)
    public void buyCartNullCreditCardNrTest() throws InvalidCartId_Exception, EmptyCart_Exception, InvalidCreditCard_Exception {
        mediatorClient.buyCart("Cart1", null);
    }

    @Test(expected = InvalidCreditCard_Exception.class)
    public void buyCartEmptyCreditCardNrTest() throws InvalidCartId_Exception, EmptyCart_Exception, InvalidCreditCard_Exception {
        mediatorClient.buyCart("Cart1", "  \n\n  \n\t\t ");
    }

    /* ===== Test Results ===== */

    // TODO: soooo boringggg

    /*@Test(expected = InvalidCreditCard_Exception.class)
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
        ItemIdView itemId = new ItemIdView();
        itemId.setProductId("Batata");
        itemId.setSupplierId("T50_Supplier1");

        try {
            mediatorClient.addToCart("0", itemId, 5);
        } catch (InvalidCartId_Exception | InvalidItemId_Exception | InvalidQuantity_Exception | NotEnoughItems_Exception e) {
            assertTrue(false);
        }
    }*/

}
