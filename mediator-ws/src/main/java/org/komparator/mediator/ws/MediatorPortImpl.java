package org.komparator.mediator.ws;

import org.komparator.supplier.ws.*;
import org.komparator.supplier.ws.cli.SupplierClient;
import pt.ulisboa.tecnico.sdis.ws.cli.CreditCardClient;
import pt.ulisboa.tecnico.sdis.ws.cli.CreditCardClientException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

import javax.jws.WebService;
import java.util.*;

@WebService(
		endpointInterface = "org.komparator.mediator.ws.MediatorPortType", 
		wsdlLocation = "mediator.1_0.wsdl", 
		name = "MediatorWebService", 
		portName = "MediatorPort", 
		targetNamespace = "http://ws.mediator.komparator.org/", 
		serviceName = "MediatorService"
)

public class MediatorPortImpl implements MediatorPortType {

	// end point manager
	private MediatorEndpointManager endpointManager;
	private Map<String, SupplierClient> suppliers;
	private Map<String, CartView> carts;
	private List<ShoppingResultView> shopHistory;

	private int shopId=0;

	public MediatorPortImpl() {
	}

	public MediatorPortImpl(MediatorEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
		this.suppliers = new HashMap<String, SupplierClient>();
		this.carts = new HashMap<String, CartView>();
		this.shopHistory = new ArrayList<ShoppingResultView>();
	}

	public MediatorEndpointManager getEndpointManager() {
		return endpointManager;
	}

	public void setEndpointManager(MediatorEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
	}

	void updateSuppliers() {
		try {
			UDDINaming uddiNaming = endpointManager.getUddiNaming();
			Collection<UDDIRecord> supplierRecords = uddiNaming.listRecords("T50_Supplier%");
			suppliers.clear();
			for (UDDIRecord supplierRecord : supplierRecords) {
				SupplierClient client = new SupplierClient(supplierRecord.getUrl());
				suppliers.put(supplierRecord.getOrgName(), client);
			}
			System.out.println("Found Suppliers: " + suppliers.keySet().toString());
		} catch (Exception e) {
			//suppliers.clear();
			System.out.println("Error updating suppliers: " + e.getMessage());
		}
	}

	
	// Main operations -------------------------------------------------------
	
	
	@Override
	public List<ItemView> getItems(String productId) throws InvalidItemId_Exception {
		if (productId == null || productId.trim().length() == 0){
			throwInvalidItemId_Exception("Item ID cannot be null or empty");
		}

		List<ItemView> items = new ArrayList<>();
		try {
			for (String clientName : suppliers.keySet()) {
				ProductView product = suppliers.get(clientName).getProduct(productId);
				if (product != null) items.add(productToItemView(product, clientName));
			}
		} catch (BadProductId_Exception e) {
			throwInvalidItemId_Exception(e.getMessage());
		}

		items.sort(Comparator.comparing(ItemView::getPrice));
		return items;
	}

	@Override
	public List<ItemView> searchItems(String descText) throws InvalidText_Exception {
		if (descText == null || descText.trim().length() == 0) {
			throwInvalidText_Exception("Search query cannot be null or empty");
		}
		List<ItemView> items = new ArrayList<>();
		try {
			for (String clientName : suppliers.keySet()) {
				for (ProductView product : suppliers.get(clientName).searchProducts(descText)) {
					items.add(productToItemView(product, clientName));
				}
			}
		} catch (BadText_Exception e) {
			throwInvalidText_Exception(e.getMessage());
		}

		items.sort((item1, item2) -> {
			int res = item1.getItemId().getProductId().compareTo(item2.getItemId().getProductId());
			return (res == 0) ? Integer.compare(item1.getPrice(), item2.getPrice()) : res;
		});

		return items;
	}
	
	@Override
	public synchronized void addToCart(String cartId, ItemIdView itemId, int itemQty) throws InvalidCartId_Exception,
			InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {

		if (cartId == null || cartId.trim().length() == 0) {
			throwInvalidCartId_Exception("CartID cannot be null or empty");
		} else if (itemId == null || itemId.getSupplierId() == null || itemId.getProductId() == null ||
				itemId.getSupplierId().trim().length() == 0 || itemId.getProductId().trim().length() == 0) {
			throwInvalidItemId_Exception("ItemID cannot be null or empty (nor its Product/Supplier IDs)");
		} else if (itemQty <= 0) {
			throwInvalidQuantity_Exception("Quantity must be positive");
		}

		SupplierClient supplierClient = suppliers.get(itemId.getSupplierId());
		if (supplierClient == null) throwInvalidItemId_Exception("Supplier does not exist");
		ProductView product = getSupplierProduct(supplierClient, itemId.getProductId());

		CartView cart = carts.get(cartId);
		if (cart == null) { // create new cart
			cart = new CartView();
			cart.setCartId(cartId);
			carts.put(cartId, cart);
		}

		CartItemView cartItem = findCartItem(cart.getItems(), itemId);
		if (cartItem == null) { // create new cart item & fill info
			cartItem = newCartItem(itemId, product);
			cart.getItems().add(cartItem);
		}

		int newItemQty = cartItem.getQuantity() + itemQty;
		if (product.getQuantity() >= newItemQty) {
			cartItem.setQuantity(newItemQty);
		} else {
			throwNotEnoughItems_Exception("Supplier does not have enough items in stock");
		}
	}

	@Override
	public synchronized ShoppingResultView buyCart(String cartId, String creditCardNr)
			throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {

		if (cartId == null || cartId.trim().length() == 0) {
			throwInvalidCartId_Exception("Cart cannot be null or empty");
		} else if (creditCardNr == null || creditCardNr.trim().length() == 0) {
			throwInvalidCreditCard_Exception("Credit Card cannot be null or empty");
		}

		CartView cart = carts.get(cartId);
		System.out.println("Cart:" + cart);
		if (cart == null) {
			throwInvalidCartId_Exception("CartId does not match any existing cart");
		} else if (cart.getItems() == null || cart.getItems().isEmpty()) {
			throwEmptyCart_Exception("Cart has no items");
		}

		ShoppingResultView shopRes = new ShoppingResultView();
		int price = 0;

		try {
			CreditCardClient cardClient = new CreditCardClient(endpointManager.getWsName());
			if(!cardClient.validateNumber(creditCardNr)) {
				throwInvalidCreditCard_Exception("Invalid card Exception");
			}
		} catch (CreditCardClientException e) {
			System.out.println("Error connecting to CreditCard.");
		}

		for(CartItemView cartItem : cart.getItems()){
			String supplierName = cartItem.getItem().getItemId().getSupplierId();
			String productId = cartItem.getItem().getItemId().getProductId();
			int qty = cartItem.getQuantity();

			SupplierClient client = suppliers.get(supplierName);
			
			try {
				client.buyProduct(productId, qty);
			} catch (BadProductId_Exception | BadQuantity_Exception | InsufficientQuantity_Exception e) {
				shopRes.droppedItems.add(cartItem);
				continue;
			}
			price += cartItem.getItem().getPrice() * qty;
			shopRes.purchasedItems.add(cartItem);
		}

		Result res;
		if (shopRes.droppedItems.isEmpty()) {
			res = Result.COMPLETE;
		} else if (shopRes.purchasedItems.isEmpty()) {
			res = Result.EMPTY;
		} else {
			res = Result.PARTIAL;
		}

		shopRes.setResult(res);
		shopRes.setTotalPrice(price);
		shopRes.setId("ShoppingResultView" + shopId);
		shopId++;
		shopHistory.add(shopRes);
		carts.remove(cartId);

		return shopRes;
	}

	// Auxiliary operations --------------------------------------------------

	@Override
	public String ping(String arg0) {
		StringBuilder builder = new StringBuilder();

		builder.append("Hello from Mediator!\n"); // Ping self back

		try { // Ping all Suppliers
			for (String clientName : suppliers.keySet()) {
				SupplierClient client = suppliers.get(clientName);
				String res = client.ping(arg0);
				builder.append(res + "\n");
			}
		} catch (Exception e){
			e.getMessage();
		}

		return builder.toString();
	}

	@Override
	public void clear() {
		try {
			for (String clientName : suppliers.keySet()) {
				SupplierClient client = suppliers.get(clientName);
				client.clear();
			}
		} catch (Exception e){
			System.out.println("Error clearing suppliers...");
		}
		suppliers.clear();
		carts.clear();
		shopHistory.clear();
		updateSuppliers();
	}

	@Override
	public List<CartView> listCarts() {
		return new ArrayList<>(carts.values());
	}

	@Override
	public List<ShoppingResultView> shopHistory() {
		return shopHistory;
	}

	// View helpers -----------------------------------------------------

	private CartItemView findCartItem(List<CartItemView> cartItems, ItemIdView itemId) {
		for (CartItemView cartItem : cartItems) { // search for cart item
			if (cartItem.getItem() != null && equalsItem(itemId, cartItem.getItem().getItemId())) {
				return cartItem;
			}
		}
		return null;
	}

	private boolean equalsItem(ItemIdView it1, ItemIdView it2) {
		return it1.getProductId().equals(it2.getProductId()) && it1.getSupplierId().equals(it2.getSupplierId());
	}

	private ItemView productToItemView(ProductView product, String clientName) {
		ItemView item = new ItemView();
		item.setDesc(product.getDesc());
		item.setPrice(product.getPrice());

		ItemIdView itemId = new ItemIdView();
		itemId.setProductId(product.getId());
		itemId.setSupplierId(clientName);
		item.setItemId(itemId);

		return item;
	}

	private CartItemView newCartItem(ItemIdView itemId, ProductView product) {
		CartItemView cartItem = new CartItemView();
		ItemView item = new ItemView();

		item.setItemId(itemId);
		item.setPrice(product.getPrice());
		item.setDesc(product.getDesc());
		cartItem.setItem(item);
		cartItem.setQuantity(0);
		return cartItem;
	}

	private ProductView getSupplierProduct(SupplierClient supplierClient, String productId) throws InvalidItemId_Exception {
		ProductView product = null;
		try {
			product = supplierClient.getProduct(productId);
		} catch (BadProductId_Exception e) {
			throwInvalidItemId_Exception("Item does not exist on supplier!");
		}
		if (product == null) throwInvalidItemId_Exception("Item does not exist on supplier!");
		return product;
	}

	// Exception helpers -----------------------------------------------------


	/**
	 * Helper method to throw new EmptyCart exception
	 */
	private void throwEmptyCart_Exception(final String message) throws EmptyCart_Exception {
		EmptyCart faultInfo = new EmptyCart();
		faultInfo.message = message;
		throw new EmptyCart_Exception(message, faultInfo);
	}

	/**
	 * Helper method to throw new InvalidCreditCard exception
	 */
	private void throwInvalidCartId_Exception(final String message) throws InvalidCartId_Exception {
		InvalidCartId faultInfo = new InvalidCartId();
		faultInfo.message = message;
		throw new InvalidCartId_Exception(message, faultInfo);
	}

	/**
	 * Helper method to throw new InvalidCreditCard exception
	 */
	private void throwInvalidCreditCard_Exception(final String message) throws InvalidCreditCard_Exception {
		InvalidCreditCard faultInfo = new InvalidCreditCard();
		faultInfo.message = message;
		throw new InvalidCreditCard_Exception(message, faultInfo);
	}

	/**
	 * Helper method to throw new InvalidItemId exception
	 */
	private void throwInvalidItemId_Exception(final String message) throws InvalidItemId_Exception {
		InvalidItemId faultInfo = new InvalidItemId();
		faultInfo.message = message;
		throw new InvalidItemId_Exception(message, faultInfo);
	}

	/**
	 * Helper method to throw new InvalidQuantity exception
	 */
	private void throwInvalidQuantity_Exception(final String message) throws InvalidQuantity_Exception {
		InvalidQuantity faultInfo = new InvalidQuantity();
		faultInfo.message = message;
		throw new InvalidQuantity_Exception(message, faultInfo);
	}

	/**
	 * Helper method to throw new NotEnoughItems exception
	 */
	private void throwNotEnoughItems_Exception(final String message) throws NotEnoughItems_Exception {
		NotEnoughItems faultInfo = new NotEnoughItems();
		faultInfo.message = message;
		throw new NotEnoughItems_Exception(message, faultInfo);
	}

	/**
	 * Helper method to throw new InvalidText exception
	 */
	private void throwInvalidText_Exception(final String message) throws InvalidText_Exception {
		InvalidText faultInfo = new InvalidText();
		faultInfo.message = message;
		throw new InvalidText_Exception(message, faultInfo);
	}

}
