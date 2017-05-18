package org.komparator.mediator.ws.cli;

import java.util.List;

import org.komparator.mediator.ws.EmptyCart_Exception;
import org.komparator.mediator.ws.InvalidCartId_Exception;
import org.komparator.mediator.ws.InvalidCreditCard_Exception;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.InvalidQuantity_Exception;
import org.komparator.mediator.ws.InvalidText_Exception;
import org.komparator.mediator.ws.ItemIdView;
import org.komparator.mediator.ws.ItemView;
import org.komparator.mediator.ws.NotEnoughItems_Exception;
import org.komparator.mediator.ws.ShoppingResultView;

public class FrontEnd {
	
	MediatorClient client;
	static final long WAIT_INTERVAL = 14 * 1000;
	
	FrontEnd(MediatorClient client) {
        this.client = client;
    }
	
	  public String ping(String arg0) {
		  this.client.port.ping(arg0);

	        String response;

	        try {
	            response = this.client.port.ping(arg0);
	        } catch (Exception e) {
	            try {
	                Thread.sleep(WAIT_INTERVAL);
	            } catch (InterruptedException e1) {
	                System.out.println("Thread not put to sleep:" + e1.getMessage());
	            }
	            this.client.handleThings();
	            response = this.client.port.ping(arg0);
	        }
	        return response;
	    }

	    public List<ItemView> searchItems(String descText) throws InvalidText_Exception {
	        List<ItemView> response;

	        try {
	            response = this.client.port.searchItems(descText);
	        } catch (Exception e) {
	            if (e instanceof InvalidText_Exception) {
	                throw e;
	            }
	            try {
	                Thread.sleep(WAIT_INTERVAL);
	            } catch (InterruptedException e1) {
	                System.out.println("Thread not put to sleep:" + e1.getMessage());
	            }
	            this.client.handleThings();
	            response = this.client.port.searchItems(descText);
	        }
	        return response;
	    }

	    public List<ItemView> getItems(String productId) throws InvalidItemId_Exception {
	        List<ItemView> response;

	        try {
	            response = this.client.port.getItems(productId);
	        } catch (Exception e) {
	            if (e instanceof InvalidItemId_Exception) {
	                throw e;
	            }
	            try {
	                Thread.sleep(WAIT_INTERVAL);
	            } catch (InterruptedException e1) {
	                System.out.println("Thread not put to sleep:" + e1.getMessage());
	            }
	            this.client.handleThings();
	            response = this.client.port.getItems(productId);
	        }
	        return response;
	    }

	    public ShoppingResultView buyCart(String cartId, String creditCardNr)
	            throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
	        ShoppingResultView response;

	        try {
	            response = this.client.port.buyCart(cartId, creditCardNr);
	        } catch (Exception e) {
	            if (e instanceof EmptyCart_Exception || e instanceof InvalidCartId_Exception
	                || e instanceof InvalidCreditCard_Exception) {
	                throw e;
	            }
	            try {
	                Thread.sleep(WAIT_INTERVAL);
	            } catch (InterruptedException e1) {
	                System.out.println("Thread not put to sleep:" + e1.getMessage());
	            }
	            this.client.handleThings();
	            response = this.client.port.buyCart(cartId, creditCardNr);
	        }
	        return response;
	    }

	    public void addToCart(String cartId, ItemIdView itemId, int itemQty) throws InvalidCartId_Exception,
	            InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {

	        try {
	        	this.client.port.addToCart(cartId, itemId, itemQty);
	        } catch (Exception e) {
	            if (e instanceof InvalidCartId_Exception || e instanceof InvalidItemId_Exception
	                || e instanceof InvalidQuantity_Exception || e instanceof NotEnoughItems_Exception) {
	                throw e;
	            }
	            try {
	                Thread.sleep(WAIT_INTERVAL);
	            } catch (InterruptedException e1) {
	                System.out.println("Thread not put to sleep:" + e1.getMessage());
	            }
	            this.client.handleThings();
	            this.client.port.addToCart(cartId, itemId, itemQty);
	        }

	    }

}
