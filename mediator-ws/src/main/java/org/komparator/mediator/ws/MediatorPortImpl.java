package org.komparator.mediator.ws;

import org.komparator.supplier.ws.ProductView;
import org.komparator.supplier.ws.cli.SupplierClient;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
	private List<SupplierClient> suppliers;

	public MediatorPortImpl() {
	}

	public MediatorPortImpl(MediatorEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
		this.suppliers = new ArrayList<SupplierClient>();
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
			Collection<String> supplierURLs = uddiNaming.list("T50_Supplier%");
			suppliers.clear();
			for (String supplierURL : supplierURLs) {
				SupplierClient client = new SupplierClient(supplierURL);
				suppliers.add(client);
			}
			System.out.println("Found Suppliers:");
			System.out.println(suppliers);
		} catch (Exception e) {
			suppliers.clear();
			System.out.println("Error updating suppliers");
		}
	}

	// Main operations -------------------------------------------------------

    // TODO
	@Override
	public List<ItemView> getItems(String productId) throws InvalidItemId_Exception {
		List<ItemView> items = new ArrayList<>();

		try {
			for (SupplierClient client : this.suppliers) {
				ProductView product = client.getProduct(productId);
				ItemView item = new ItemView();
				item.setDesc(product.getDesc());
				item.setPrice(product.getPrice());
				ItemIdView itemId = new ItemIdView();
				itemId.setProductId(product.getId());
				itemId.setSupplierId(""); // TODO what is this?
				item.setItemId(itemId);
				items.add(item);
			}
		} catch (Exception e){
			System.out.println("Error connecting to suppliers...");
		}

		return items;
	}
	
	@Override
	public List<ItemView> searchItems(String descText) throws InvalidText_Exception {
		// TODO Auto-generated method stub
		return null;
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
			for (SupplierClient client : this.suppliers) {
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
