package org.komparator.mediator.ws.it;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.komparator.mediator.ws.*;

import java.util.List;

public class AddToCartIT extends BaseIT {
    private final CartView cartView = new CartView();
    private final ItemIdView itemIdView = new ItemIdView();

    @Before
    public void populateViews() {
        itemIdView.setProductId("iPhone6");
        itemIdView.setSupplierId("T50_Supplier1");
        cartView.setCartId("MyCart1");
    }

    /* ===== Test Arguments ===== */

    @Test(expected = InvalidCartId_Exception.class)
    public void nullCartIdTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        mediatorClient.addToCart(null, itemIdView, 1);
    }

    @Test(expected = InvalidCartId_Exception.class)
    public void emptyCartIdTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        mediatorClient.addToCart(" \n\n\t\t \t  \n \n", itemIdView, 1);
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void nullItemIdTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        mediatorClient.addToCart(cartView.getCartId(), null, 1);
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void nullSupplierIdTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        itemIdView.setSupplierId(null);
        mediatorClient.addToCart(cartView.getCartId(), itemIdView, 1);
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void nullProductIdTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        itemIdView.setProductId(null);
        mediatorClient.addToCart(cartView.getCartId(), itemIdView, 1);
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void emptySupplierIdTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        itemIdView.setSupplierId(" \n\t \t\t\n  \n");
        mediatorClient.addToCart(cartView.getCartId(), itemIdView, 1);
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void emptyProductIdTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        itemIdView.setProductId(" \n\t \t\t\n  \n");
        mediatorClient.addToCart(cartView.getCartId(), itemIdView, 1);
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void invalidSupplierIdTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        itemIdView.setSupplierId("NON-EXISTING-SUPPLIER");
        mediatorClient.addToCart(cartView.getCartId(), itemIdView, 1);
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void invalidProductIdTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        itemIdView.setProductId("NON-EXISTING-PRODUCT");
        mediatorClient.addToCart(cartView.getCartId(), itemIdView, 1);
    }

    @Test(expected = InvalidQuantity_Exception.class)
    public void negativeQuantityTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        mediatorClient.addToCart(cartView.getCartId(), itemIdView, -2);
    }

    @Test(expected = InvalidQuantity_Exception.class)
    public void zeroQuantityTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        mediatorClient.addToCart(cartView.getCartId(), itemIdView, 0);
    }

    /* ===== Test Results ===== */

    @Test(expected = NotEnoughItems_Exception.class)
    public void notEnoughItemsTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        mediatorClient.addToCart(cartView.getCartId(), itemIdView, 999999999);
    }

    @Test(expected = NotEnoughItems_Exception.class)
    public void notEnoughItemsTest2() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        itemIdView.setProductId("Pixel");
        mediatorClient.addToCart(cartView.getCartId(), itemIdView, 10);
        mediatorClient.addToCart(cartView.getCartId(), itemIdView, 1);
    }

    @Test
    public void addAllItemsOkTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        itemIdView.setProductId("PixelXL");
        int itemQty = 20;

        mediatorClient.addToCart(cartView.getCartId(), itemIdView, itemQty);
    }

    @Test
    public void addAllMultipleCartsTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        itemIdView.setProductId("Pixel");

        mediatorClient.addToCart("BestCart1", itemIdView, 10);
        mediatorClient.addToCart("BestCart2", itemIdView, 10);
    }

    @Test
    public void oneItemAddAndCheckTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        String cartId = "MyNewCart";
        int itemQty = 2;

        Assert.assertNull(getCart(cartId));

        mediatorClient.addToCart(cartId, itemIdView, itemQty);

        CartView cart = getCart(cartId);
        Assert.assertNotNull(cart);
        Assert.assertEquals(cartId, cart.getCartId());
        Assert.assertEquals(1, cart.getItems().size());
        Assert.assertNotNull(cart.getItems());
        CartItemView cartItem = getCartItem(cart, itemIdView);
        Assert.assertNotNull(cartItem);
        Assert.assertEquals(itemQty, cartItem.getQuantity());
    }

    @Test
    public void multipleItemsAddAndCheckTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        String cartId = "MyNewCart2";
        int itemQty = 2;

        Assert.assertNull(getCart(cartId));

        mediatorClient.addToCart(cartId, itemIdView, itemQty);

        CartView cart = getCart(cartId);
        Assert.assertNotNull(cart);
        Assert.assertEquals(cartId, cart.getCartId());
        Assert.assertEquals(1, cart.getItems().size());
        Assert.assertNotNull(cart.getItems());
        CartItemView cartItem = getCartItem(cart, itemIdView);
        Assert.assertNotNull(cartItem);
        Assert.assertEquals(itemQty, cartItem.getQuantity());

        mediatorClient.addToCart(cartId, itemIdView, itemQty);
        cart = getCart(cartId);
        Assert.assertNotNull(cart);
        Assert.assertEquals(cartId, cart.getCartId());
        Assert.assertEquals(1, cart.getItems().size());
        Assert.assertNotNull(cart.getItems());
        cartItem = getCartItem(cart, itemIdView);
        Assert.assertNotNull(cartItem);
        Assert.assertEquals(2 * itemQty, cartItem.getQuantity());
    }

    @Test
    public void multipleItemsMultipleCartsAddCheckTest() throws InvalidCartId_Exception,
            InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
        String cartId1 = "ManyCartsWow1";
        String cartId2 = "ManyCartsWow2";
        int itemQty = 10;

        itemIdView.setProductId("Pixel");
        mediatorClient.addToCart(cartId1, itemIdView, itemQty);
        mediatorClient.addToCart(cartId2, itemIdView, itemQty);
        itemIdView.setProductId("iPhone6");
        mediatorClient.addToCart(cartId1, itemIdView, itemQty);
        mediatorClient.addToCart(cartId2, itemIdView, itemQty);

        CartView cart1 = getCart(cartId1);
        Assert.assertNotNull(cart1);
        Assert.assertEquals(cartId1, cart1.getCartId());
        Assert.assertEquals(2, cart1.getItems().size());
        Assert.assertNotNull(cart1.getItems());

        CartView cart2 = getCart(cartId1);
        Assert.assertNotNull(cart2);
        Assert.assertEquals(cartId1, cart2.getCartId());
        Assert.assertEquals(2, cart2.getItems().size());
        Assert.assertNotNull(cart2.getItems());

        CartItemView cartItem;
        itemIdView.setProductId("Pixel");
        cartItem = getCartItem(cart1, itemIdView);
        Assert.assertNotNull(cartItem);
        Assert.assertEquals(itemQty, cartItem.getQuantity());
        cartItem = getCartItem(cart2, itemIdView);
        Assert.assertNotNull(cartItem);
        Assert.assertEquals(itemQty, cartItem.getQuantity());

        itemIdView.setProductId("iPhone6");
        cartItem = getCartItem(cart1, itemIdView);
        Assert.assertNotNull(cartItem);
        Assert.assertEquals(itemQty, cartItem.getQuantity());
        cartItem = getCartItem(cart1, itemIdView);
        Assert.assertNotNull(cartItem);
        Assert.assertEquals(itemQty, cartItem.getQuantity());
    }


    /* ===== Auxiliary methods ===== */


    private CartView getCart(String cartId) {
        List<CartView> cartList = mediatorClient.listCarts();
        for (CartView cart : cartList)
            if (cart.getCartId().equals(cartId))
                return cart;
        return null;
    }

    private boolean sameItemIds(ItemIdView it1, ItemIdView it2) {
        return it1.getProductId().equals(it2.getProductId()) && it1.getSupplierId().equals(it2.getSupplierId());
    }

    private CartItemView getCartItem(CartView cart, ItemIdView itemId) {
        if (cart.getItems() == null) return null;
        for (CartItemView cartItem : cart.getItems())
            if (sameItemIds(itemId, cartItem.getItem().getItemId()))
                return cartItem;
        return null;
    }

}
