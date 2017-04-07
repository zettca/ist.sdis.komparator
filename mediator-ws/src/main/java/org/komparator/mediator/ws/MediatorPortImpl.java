package org.komparator.mediator.ws;

import org.komparator.supplier.ws.ProductView;
import org.komparator.supplier.ws.cli.SupplierClient;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
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

	public MediatorPortImpl() {
	}

	public MediatorPortImpl(MediatorEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
		this.suppliers = new HashMap<>();
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
		List<ItemView> items = new ArrayList<>();

		try {
			for (String clientName : suppliers.keySet()) {
				SupplierClient client = suppliers.get(clientName);
				ProductView product = client.getProduct(productId);
				ItemView item = new ItemView();
				item.setDesc(product.getDesc());
				item.setPrice(product.getPrice());
				ItemIdView itemId = new ItemIdView();
				itemId.setProductId(product.getId());
				itemId.setSupplierId(clientName);
				item.setItemId(itemId);
				items.add(item);
			}
		} catch (Exception e){
			System.out.println("Error connecting to suppliers...");
		}

		return items;
	}
	
	private List<ProductView> getItemByDesc(SupplierClient client, String descText){
		List<ProductView> products = new ArrayList<ProductView>();
		for(ProductView p: client.listProducts()){
			if(p.getDesc().equals(descText))
				products.add(p);
		}
		return products;
	}
	
	@Override
	public List<ItemView> searchItems(String descText) throws InvalidText_Exception {
		List<ItemView> items = new ArrayList<>();

		try {// TODO EXCEPTION PRODUCT NOT FOUND
			for (String clientName : suppliers.keySet()) {
				SupplierClient client = suppliers.get(clientName);
				for(ProductView product : getItemByDesc(client, descText)){
					ItemView item = new ItemView();
					item.setDesc(product.getDesc());
					item.setPrice(product.getPrice());
					ItemIdView itemId = new ItemIdView();
					itemId.setProductId(product.getId());
					itemId.setSupplierId(clientName);
					item.setItemId(itemId);
					items.add(item);
				}
			}
		} catch (Exception e){
			System.out.println("Error connecting to suppliers...");
		}
		if (items.size() > 0) {
			  Collections.sort(items, new Comparator<ItemView>() {
			      @Override
			      public int compare(final ItemView object1, final ItemView object2) {
			          return object1.getItemId().toString().compareTo(object2.getItemId().toString());
			      }
			  });
			}
		return items;
	}
	
	@Override
	public void addToCart(String cartId, ItemIdView itemId, int itemQty) throws InvalidCartId_Exception,
			InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public ShoppingResultView buyCart(String cartId, String creditCardNr)
			throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
    
	// Auxiliary operations --------------------------------------------------	
	
    // TODO
	
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
			System.out.println("Error connecting to suppliers...");
			return "Error pinging";
		}

		return builder.toString();
	}
	
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public List<CartView> listCarts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ShoppingResultView> shopHistory() {
		// TODO Auto-generated method stub
		return null;
	}

	
	// View helpers -----------------------------------------------------
	
    // TODO

    
	// Exception helpers -----------------------------------------------------

    // TODO

}
