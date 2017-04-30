package org.komparator.mediator.ws.it;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.komparator.mediator.ws.*;

public class BuyCartIT extends BaseIT {
    private final String creditCardNr = "4556648855991861";
    private final ItemIdView itemIdView = new ItemIdView();

    @Before
    public void makeCart() {
        itemIdView.setProductId("PixelXL");
        itemIdView.setSupplierId("T50_Supplier1");
    }

    /* ===== Test Arguments ===== */

    @Test(expected = InvalidCartId_Exception.class)
    public void nullCartIdTest() throws InvalidCartId_Exception, EmptyCart_Exception, InvalidCreditCard_Exception {
        mediatorClient.buyCart(null, creditCardNr);
    }

    @Test(expected = InvalidCartId_Exception.class)
    public void emptyCartIdTest() throws InvalidCartId_Exception, EmptyCart_Exception, InvalidCreditCard_Exception {
        mediatorClient.buyCart("  \n\n  \n\t\t ", creditCardNr);
    }

    @Test(expected = InvalidCartId_Exception.class)
    public void invalidCartIdTest() throws InvalidCartId_Exception, EmptyCart_Exception, InvalidCreditCard_Exception {
        mediatorClient.buyCart("NON-EXISTING-CART", creditCardNr);
    }

    @Test(expected = InvalidCreditCard_Exception.class)
    public void nullCardTest() throws InvalidCartId_Exception, EmptyCart_Exception, InvalidCreditCard_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception, InvalidItemId_Exception {
        String cartId = "MyCart";
        mediatorClient.addToCart(cartId, itemIdView, 1);
        mediatorClient.buyCart(cartId, null);
    }

    @Test(expected = InvalidCreditCard_Exception.class)
    public void emptyCardTest() throws InvalidCartId_Exception, EmptyCart_Exception, InvalidCreditCard_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception, InvalidItemId_Exception {
        String cartId = "MyCart";
        mediatorClient.addToCart(cartId, itemIdView, 1);
        mediatorClient.buyCart(cartId, "");
    }

    @Test(expected = InvalidCreditCard_Exception.class)
    public void wrongCardTest() throws InvalidCartId_Exception, EmptyCart_Exception, InvalidCreditCard_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception, InvalidItemId_Exception {
        String cartId = "MyCart";
        mediatorClient.addToCart(cartId, itemIdView, 1);
        mediatorClient.buyCart(cartId, creditCardNr.substring(0, 6));
    }

    @Test(expected = InvalidCreditCard_Exception.class)
    public void appendedTrashCardTest() throws InvalidCartId_Exception, EmptyCart_Exception, InvalidCreditCard_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception, InvalidItemId_Exception {
        String cartId = "MyCart";
        mediatorClient.addToCart(cartId, itemIdView, 1);
        mediatorClient.buyCart(cartId, creditCardNr + "123456");
    }

    /* ===== Test Results ===== */

    @Test
    public void emptyBuyTest() throws InvalidCartId_Exception, EmptyCart_Exception, InvalidCreditCard_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception, InvalidItemId_Exception {
        String cartId1 = "MyCartSneakyThief";
        String cartId2 = "MyCartSlowGuy";
        itemIdView.setProductId("Pixel");
        mediatorClient.addToCart(cartId1, itemIdView, 1);
        mediatorClient.addToCart(cartId2, itemIdView, 10);
        itemIdView.setProductId("PixelXL");
        mediatorClient.addToCart(cartId1, itemIdView, 20);
        mediatorClient.addToCart(cartId2, itemIdView, 1);

        ShoppingResultView res;
        res = mediatorClient.buyCart(cartId1, creditCardNr);
        Assert.assertEquals(Result.COMPLETE, res.getResult());
        res = mediatorClient.buyCart(cartId2, creditCardNr);
        Assert.assertEquals(Result.EMPTY, res.getResult());
        Assert.assertEquals(2, res.getDroppedItems().size());
        Assert.assertEquals(0, res.getPurchasedItems().size());
    }

    @Test
    public void partialBuyTest() throws InvalidCartId_Exception, EmptyCart_Exception, InvalidCreditCard_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception, InvalidItemId_Exception {
        String cartId1 = "MyCartDude1";
        String cartId2 = "MyCartDude2";
        itemIdView.setProductId("Pixel");
        mediatorClient.addToCart(cartId1, itemIdView, 5);
        mediatorClient.addToCart(cartId2, itemIdView, 5);
        itemIdView.setProductId("PixelXL");
        mediatorClient.addToCart(cartId1, itemIdView, 11);
        mediatorClient.addToCart(cartId2, itemIdView, 10);

        ShoppingResultView res;
        res = mediatorClient.buyCart(cartId1, creditCardNr);
        Assert.assertEquals(Result.COMPLETE, res.getResult());
        res = mediatorClient.buyCart(cartId2, creditCardNr);
        Assert.assertEquals(Result.PARTIAL, res.getResult());
        Assert.assertEquals(1, res.getDroppedItems().size());
        Assert.assertEquals(1, res.getPurchasedItems().size());
    }

    @Test
    public void fullBuyTest() throws InvalidCartId_Exception, EmptyCart_Exception, InvalidCreditCard_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception, InvalidItemId_Exception {
        String cartId = "MyCart3";
        itemIdView.setProductId("Pixel");
        mediatorClient.addToCart(cartId, itemIdView, 5);
        itemIdView.setProductId("PixelXL");
        mediatorClient.addToCart(cartId, itemIdView, 5);
        ShoppingResultView result = mediatorClient.buyCart(cartId, creditCardNr);
        Assert.assertEquals(Result.COMPLETE, result.getResult());
        Assert.assertEquals(2, result.getPurchasedItems().size());
    }

}
