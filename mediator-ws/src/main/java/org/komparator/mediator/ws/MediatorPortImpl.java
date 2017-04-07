package org.komparator.mediator.ws;

import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadQuantity_Exception;
import org.komparator.supplier.ws.InsufficientQuantity_Exception;
import org.komparator.supplier.ws.ProductView;
import org.komparator.supplier.ws.cli.SupplierClient;

import pt.ulisboa.tecnico.sdis.ws.cli.CreditCardClient;
import pt.ulisboa.tecnico.sdis.ws.cli.CreditCardClientException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;


import java.util.*;

import javax.jws.WebService;

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

	private void updateSuppliers() {
		try {
			UDDINaming uddiNaming = endpointManager.getUddiNaming();
			Collection<UDDIRecord> supplierRecords = uddiNaming.listRecords("T50_Supplier%");
			suppliers.clear();
			for (UDDIRecord supplierRecord : supplierRecords) {
				SupplierClient client = new SupplierClient(supplierRecord.getUrl());
				suppliers.put(supplierRecord.getOrgName(), client);
			}
			System.out.println("Found Suppliers:");
			System.out.println(suppliers.keySet());
		} catch (Exception e) {
			suppliers.clear();
			System.out.println("Error updating suppliers");
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
				SupplierClient client = suppliers.get(clientName);
				ProductView product = client.getProduct(productId);
				ItemView item = productViewToItemView(clientName,product);
				items.add(item);
			}
		} catch (Exception e){
			System.out.println("Error connecting to suppliers...");
		}

		if (items.size() > 0) {
			Collections.sort(items, new Comparator<ItemView>() {
				@Override
				public int compare(final ItemView item1, final ItemView item2) {
					return item2.getPrice() - item1.getPrice();
				}
			});
		}

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
				SupplierClient client = suppliers.get(clientName);
				for(ProductView product : client.searchProducts(descText)){
					ItemView item = productViewToItemView(clientName,product);
					items.add(item);
				}
			}
		} catch (Exception e){
			System.out.println("Error connecting to suppliers...");
		}

		if (items.size() > 1) {
			Collections.sort(items, new Comparator<ItemView>() {
				@Override
				public int compare(final ItemView item1, final ItemView item2) {
					int res = item1.getItemId().toString().compareTo(item2.getItemId().toString());
					return (res == 0) ? item2.getPrice() - item1.getPrice() : res;
				}
			});
		}

		return items;
	}
	
	@Override
	public synchronized void addToCart(String cartId, ItemIdView itemId, int itemQty) throws InvalidCartId_Exception,
			InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {

		if (cartId == null || cartId.trim().length() == 0) {
			throwInvalidCartId_Exception("Cart ID cannot be null or empty");
		} else if (itemId == null || cartId.trim().length() == 0) {
			throwInvalidItemId_Exception("Item ID cannot be null or empty");
		} else if (itemQty <= 0) {
			throwInvalidQuantity_Exception("Quantity must be positive");
		}
		
		updateSuppliers();
		CartItemView cartItem = itemViewToCartItemView(itemId, itemQty);
		// Procura o card, ou cria um novo se não existir
		CartView cart = null;
		if(carts.containsKey(cartId)){	//verifica se existe o cart
			cart = carts.get(cartId);
			
			for(CartItemView item : cart.getItems()){	//verifica se ja ha este item
				if(item.getItem().getItemId().equals(itemId)){ // item existe
					for(CartItemView it : cart.getItems()){
						if(cartItem.equals(it)){ // encontrou o item
							int gtyTotal = it.getQuantity() + itemQty;
							
							try {
								if(suppliers.get(itemId.getSupplierId()).getProduct(itemId.getProductId()).getQuantity()>=gtyTotal) {
									it.setQuantity(gtyTotal);
								} else {
									throwNotEnoughItems_Exception("Not enough items in stock");
								}
							} catch (BadProductId_Exception e) {
								throwInvalidItemId_Exception("Item does not exist");
							}
						}
					}
				}
			}
			// quando o item ainda nao ha adiciona-o
			try {
				if(suppliers.get(itemId.getSupplierId()).getProduct(itemId.getProductId()).getQuantity() >= itemQty)
					cart.items.add(cartItem);

				else{	//quantidades erradas
					throwNotEnoughItems_Exception("Not enough items in stock");
				}
			} catch (BadProductId_Exception e) {
				throwInvalidItemId_Exception("Item does not exist");
			}

		} else{	// criar cart caso não exista
			cart = new CartView();
			try {
				ProductView product = suppliers.get(itemId.getSupplierId()).getProduct(itemId.getProductId());
				if (product.getQuantity() >= itemQty) {
					cartItem.setQuantity(itemQty);
					cart.setCartId(cartId);
					cart.items.add(cartItem);
					carts.put(cartId, cart);
				} else {
					throwNotEnoughItems_Exception("Not enough items in stock");
				}
			} catch (BadProductId_Exception e) {
				throwInvalidItemId_Exception("Item does not exist");
			}
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
		
		updateSuppliers();
		CartView cart = carts.get(cartId);
		String supplierName, productId;
		ShoppingResultView shopRes = new ShoppingResultView();
		int price = 0;

		if(cart.getItems().isEmpty()) {
			throwEmptyCart_Exception("Cart is empty");
		}

		try {
			CreditCardClient cardClient = new CreditCardClient(endpointManager.getWsName());
			if(!cardClient.validateNumber(creditCardNr)) {
				throwInvalidCreditCard_Exception("Invalid card Exception");
			}
		} catch (CreditCardClientException e) {
			System.out.println("Error connecting to CreditCard.");
		}

		for(CartItemView cartItem : cart.getItems()){
			supplierName = cartItem.getItem().getItemId().getSupplierId();
			productId = cartItem.getItem().getItemId().getProductId();
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
        
        if(shopRes.droppedItems.isEmpty()) res = Result.COMPLETE;
        
        else if (shopRes.purchasedItems.isEmpty()) res = Result.EMPTY;
        
        else res = Result.PARTIAL;
        
        shopRes.setResult(res);
        shopRes.setTotalPrice(price);
        shopRes.setId("ShoppingResultView"+shopId);
        shopId++;
        shopHistory.add(shopRes);
        
		return shopRes;
	}

	// Auxiliary operations --------------------------------------------------
	
	@Override
	public String ping(String arg0) {
		StringBuilder builder = new StringBuilder();
		updateSuppliers();

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
	}
	
	@Override
	public List<CartView> listCarts() {
		return (List<CartView>) carts.values();
	}

	@Override
	public List<ShoppingResultView> shopHistory() {
		return shopHistory;
	}

	
	// View helpers -----------------------------------------------------
	

	public ItemView productViewToItemView(String clientName, ProductView product){
		ItemView item = new ItemView();
		item.setDesc(product.getDesc());
		item.setPrice(product.getPrice());
		ItemIdView itemId = new ItemIdView();
		itemId.setProductId(product.getId());
		itemId.setSupplierId(clientName);
		item.setItemId(itemId);
		return item;
	}
	
	public CartItemView itemViewToCartItemView(ItemIdView itemId, int quantidade){
		CartItemView cartItem = new CartItemView();
		SupplierClient client = suppliers.get(itemId.getSupplierId());
		ProductView product = new ProductView();
		try {
			product = client.getProduct(itemId.getProductId());
		} catch (BadProductId_Exception e) {
			e.printStackTrace();
		}
		
		ItemView item = productViewToItemView(itemId.getSupplierId(), product);
		cartItem.setItem(item);
		cartItem.setQuantity(quantidade);
		return cartItem;
	}

    
	// Exception helpers -----------------------------------------------------

	 
    /** Helper method to throw new EmptyCart exception */
    private void throwEmptyCart_Exception(final String message) throws EmptyCart_Exception {
        EmptyCart faultInfo = new EmptyCart();
        faultInfo.message = message;
        throw new EmptyCart_Exception(message, faultInfo);
    }
 
    /** Helper method to throw new InvalidCreditCard exception */
    private void throwInvalidCartId_Exception(final String message) throws InvalidCartId_Exception {
        InvalidCartId faultInfo = new InvalidCartId();
        faultInfo.message = message;
        throw new InvalidCartId_Exception(message, faultInfo);
    }
    
    /** Helper method to throw new InvalidCreditCard exception */
    private void throwInvalidCreditCard_Exception(final String message) throws InvalidCreditCard_Exception {
    	InvalidCreditCard faultInfo = new InvalidCreditCard();
        faultInfo.message = message;
        throw new InvalidCreditCard_Exception(message, faultInfo);
    }
 
    /** Helper method to throw new InvalidItemId exception */
    private void throwInvalidItemId_Exception(final String message) throws InvalidItemId_Exception {
        InvalidItemId faultInfo = new InvalidItemId();
        faultInfo.message = message;
        throw new InvalidItemId_Exception(message, faultInfo);
    }
 
    /** Helper method to throw new InvalidQuantity exception */
    private void throwInvalidQuantity_Exception(final String message) throws InvalidQuantity_Exception {
        InvalidQuantity faultInfo = new InvalidQuantity();
        faultInfo.message = message;
        throw new InvalidQuantity_Exception(message, faultInfo);
    }
 
    /** Helper method to throw new NotEnoughItems exception */
    private void throwNotEnoughItems_Exception(final String message) throws NotEnoughItems_Exception {
        NotEnoughItems faultInfo = new NotEnoughItems();
        faultInfo.message = message;
        throw new NotEnoughItems_Exception(message, faultInfo);
    }
 
    /** Helper method to throw new InvalidText exception */
    private void throwInvalidText_Exception(final String message) throws InvalidText_Exception {
        InvalidText faultInfo = new InvalidText();
        faultInfo.message = message;
        throw new InvalidText_Exception(message, faultInfo);
    }

}
