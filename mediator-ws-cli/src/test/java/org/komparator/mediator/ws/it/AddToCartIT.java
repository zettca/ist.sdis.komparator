package org.komparator.mediator.ws.it;

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

        cartView.setCartId("Carrinho1");
        List<CartItemView> cartItems = cartView.getItems();


    }

    /* ===== Test Arguments ===== */

    @Test(expected = InvalidCartId_Exception.class)
    public void addToCartNullCartIdTest() throws InvalidCartId_Exception, InvalidItemId_Exception,
            InvalidQuantity_Exception, NotEnoughItems_Exception {
        mediatorClient.addToCart(null, itemIdView, 1);
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

    /* ===== Test any result ===== */

    // TODO: ohgodno killmepls

}
