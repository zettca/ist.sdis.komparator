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

    @Test(expected = InvalidItemId_Exception.class)
    public void addToCartEmptySupplierIdTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        itemIdView.setSupplierId(" \n\t \t\t\n  \n");
        mediatorClient.addToCart(cartView.getCartId(), itemIdView, 1);
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void addToCartEmptyProductIdTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        itemIdView.setProductId(" \n\t \t\t\n  \n");
        mediatorClient.addToCart(cartView.getCartId(), itemIdView, 1);
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void addToCartInvalidSupplierIdTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        itemIdView.setSupplierId("NON-EXISTING-SUPPLIER");
        mediatorClient.addToCart(cartView.getCartId(), itemIdView, 1);
    }

    @Test(expected = InvalidItemId_Exception.class)
    public void addToCartInvalidProductIdTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        itemIdView.setProductId("NON-EXISTING-PRODUCT");
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

    /* ===== Test Results ===== */

    @Test(expected = NotEnoughItems_Exception.class)
    public void addToCartNotEnoughItemsTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        System.out.println(itemIdView.getSupplierId());
        mediatorClient.addToCart(cartView.getCartId(), itemIdView, 999999999);
    }

    @Test
    public void addToCartNewProductTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        itemIdView.setProductId("PixelXL");
        int itemQty = 2;

        mediatorClient.addToCart(cartView.getCartId(), itemIdView, itemQty);
        Assert.assertNotNull(getCart(cartView.getCartId()));

        Assert.assertEquals(itemQty, getCartItem(cartView, itemIdView).getQuantity());
    }

    @Test
    public void addToCartGlobalTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        String cartId = "NON-EXISTING-CART-X";
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
        Assert.assertEquals(2, cart.getItems().size());
        Assert.assertNotNull(cart.getItems());
        cartItem = getCartItem(cart, itemIdView);
        Assert.assertNotNull(cartItem);
        Assert.assertEquals(2 * itemQty, cartItem.getQuantity());
    }

    @Test
    public void addToCartMoreProductsTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        String cartId = "NON-EXISTING-CART2";
        itemIdView.setSupplierId("T50_Supplier2");
        itemIdView.setProductId("iPhone7+");

        Assert.assertNull(getCart(cartId));
        mediatorClient.addToCart(cartId, itemIdView, 10);
        Assert.assertNotNull(getCart(cartId));
        mediatorClient.addToCart(cartId, itemIdView, 10);

        CartItemView cartItem = getCartItem(getCart(cartId), itemIdView);
        Assert.assertNotNull(cartItem);
        Assert.assertEquals(20, cartItem.getQuantity());
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

    private boolean sameItemIds(ItemIdView it1, ItemIdView it2) {
        System.out.println(it1.getSupplierId() + " " + it1.getProductId());
        System.out.println(it2.getSupplierId() + " " + it2.getProductId());
        return it1.getProductId().equals(it2.getProductId()) && it1.getSupplierId().equals(it2.getSupplierId());
    }

    private CartItemView getCartItem(CartView cart, ItemIdView itemId) {
        System.out.println("GetCartItem: " + cart.getCartId() + " " + itemId.getProductId() + " " + itemId.getSupplierId());
        if (cart.getItems() == null) return null;
        for (CartItemView cartItem : cart.getItems()) {
            System.out.println(cartItem.getItem().getItemId());
            if (sameItemIds(itemId, cartItem.getItem().getItemId())) {
                return cartItem;
            }
        }
        return null;
    }

}
