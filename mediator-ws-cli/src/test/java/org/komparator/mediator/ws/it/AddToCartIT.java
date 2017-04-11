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
        List<CartItemView> cartItems = cartView.getItems();
    }

    /* ===== Test Arguments ===== */

    @Test(expected = InvalidCartId_Exception.class)
    public void addToCartNullCartIdTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        mediatorClient.addToCart(null, itemIdView, 1);
    }

    @Test(expected = InvalidCartId_Exception.class)
    public void addToCartEmptyCartIdTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        mediatorClient.addToCart(" \n\n\t\t \t  \n \n", itemIdView, 1);
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void addToCartNullItemIdTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        mediatorClient.addToCart(cartView.getCartId(), null, 1);
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void addToCartNullSupplierIdTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        itemIdView.setSupplierId(null);
        mediatorClient.addToCart(cartView.getCartId(), itemIdView, 1);
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void addToCartNullProductIdTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        itemIdView.setProductId(null);
        mediatorClient.addToCart(cartView.getCartId(), itemIdView, 1);
    }

    @Test(expected = InvalidQuantity_Exception.class)
    public void addToCartNegativeQuantityTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        mediatorClient.addToCart(cartView.getCartId(), itemIdView, -2);
    }

    @Test(expected = InvalidQuantity_Exception.class)
    public void addToCartZeroQuantityTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        mediatorClient.addToCart(cartView.getCartId(), itemIdView, 0);
    }

    @Test(expected = NotEnoughItems_Exception.class)
    public void addToCartNotEnoughItemsTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        mediatorClient.addToCart(cartView.getCartId(), itemIdView, 999999999);
    }

    /* ===== Test Results ===== */

    @Test
    public void addToCartNewProductTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        itemIdView.setProductId("PixelXL");

        Assert.assertNull(getCart(cartView.getCartId()));
        mediatorClient.addToCart(cartView.getCartId(), itemIdView, 1);
        Assert.assertNotNull(getCart(cartView.getCartId()));

        Assert.assertEquals(1, getCartItem(cartView.getCartId(), itemIdView).getQuantity());
    }

    @Test
    public void addToCartNewCartTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        String cartId = "NON-EXISTING-CART";

        Assert.assertNull(getCart(cartId));
        mediatorClient.addToCart(cartId, itemIdView, 1);
        Assert.assertNotNull(getCart(cartId));

        Assert.assertEquals(1, getCartItem(cartId, itemIdView).getQuantity());
    }

    @Test
    public void addToCartMoreProductsTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        String cartId = "NON-EXISTING-CART";
        itemIdView.setSupplierId("T50_Supplier2");
        itemIdView.setProductId("iPhone7+");

        Assert.assertNull(getCart(cartId));
        mediatorClient.addToCart(cartId, itemIdView, 10);
        Assert.assertNotNull(getCart(cartId));
        mediatorClient.addToCart(cartId, itemIdView, 10);

        Assert.assertEquals(20, getCartItem(cartId, itemIdView).getQuantity());

    }

    // TODO: morestuffs ohgodno killmepls


    /* ===== Auxiliary methods ===== */

    // TODO: move this elsewhere? to Mediator?

    private CartView getCart(String cartId) {
        List<CartView> cartList = mediatorClient.listCarts();
        for (CartView cart : cartList) {
            if (cart.getCartId().equals(cartId)) {
                return cart;
            }
        }
        return null;
    }

    private CartItemView getCartItem(String cartId, ItemIdView itemId) {
        for (CartItemView cartItemView : getCart(cartId).getItems()) {
            if (cartItemView.getItem().getItemId().getProductId().equals(itemId.getProductId()) &&
                    cartItemView.getItem().getItemId().getSupplierId().equals(itemId.getSupplierId())) {
                return cartItemView;
            }
        }
        return null;
    }

}
