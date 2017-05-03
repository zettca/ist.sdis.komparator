package org.komparator.supplier.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.handler.MessageContext;


import example.ws.handler.SignHandler;
import org.komparator.supplier.domain.Product;
import org.komparator.supplier.domain.Purchase;
import org.komparator.supplier.domain.QuantityException;
import org.komparator.supplier.domain.Supplier;

import javax.xml.ws.WebServiceContext;


@WebService(
		endpointInterface = "org.komparator.supplier.ws.SupplierPortType", 
		wsdlLocation = "supplier.1_0.wsdl", 
		name = "SupplierWebService", 
		portName = "SupplierPort", 
		targetNamespace = "http://ws.supplier.komparator.org/", 
		serviceName = "SupplierService"
)
@HandlerChain(file = "/supplier-ws_handler-chain.xml")
public class SupplierPortImpl implements SupplierPortType {

	// end point manager
	private SupplierEndpointManager endpointManager;

	@Resource
	private WebServiceContext webServiceContext;


	public String keyAlias;

	public String keyStoreFile;
	public String dataToSend;
	
	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps;
	
	public SupplierPortImpl(SupplierEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
		
		testProps = new Properties();
		try {
			testProps.load(SupplierPortImpl.class.getResourceAsStream(TEST_PROP_FILE));
			System.out.println("Loaded test properties:");
			System.out.println(testProps);
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
			try {
				throw e;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		String uddiEnabled = testProps.getProperty("uddi.enabled");
		String uddiURL = testProps.getProperty("uddi.url");
		keyAlias = testProps.getProperty("ws.name");
		String wsURL = testProps.getProperty("ws.url");
				
		System.out.println("\n\n\n\n\n URLLLLL \n\n\n"+keyAlias+"\n\n\n\n\n\n");
		/*0String[] slash_split = url.split("/");
		String[] dots_split = slash_split[2].split(":");
		String port = dots_split[1];
		int server_id = Integer.parseInt(port) % 10;

		//keyAlias = "T50_Supplier" + server_id;*/

		keyStoreFile = "../supplier-ws/src/main/resources/" + keyAlias + ".jks";

		dataToSend = keyAlias + "#" + keyStoreFile;
	}

	// Main operations -------------------------------------------------------
	
	
	public ProductView getProduct(String productId) throws BadProductId_Exception {
		sendDataToSign();

		// check product id
		if (productId == null)
			throwBadProductId("Product identifier cannot be null!");
		productId = productId.trim();
		if (productId.length() == 0)
			throwBadProductId("Product identifier cannot be empty or whitespace!");

		// retrieve product
		Supplier supplier = Supplier.getInstance();
		Product p = supplier.getProduct(productId);
		if (p != null) {
			ProductView pv = newProductView(p);
			// product found!
			return pv;
		}
		// product not found
		return null;
	}

	public List<ProductView> searchProducts(String descText) throws BadText_Exception {
		sendDataToSign();

		//check argument not empty nor has spaces
		if(descText == null)
			throwBadText("Product description cannot be null!");
		descText = descText.trim();
		if (descText.length() == 0)
			throwBadText("Product description cannot be empty or whitespace!");


		//Get products in list of ProductView
		Supplier supplier = Supplier.getInstance();
		List<ProductView> pvs = new ArrayList<ProductView>();

		//Get products id by description or get products by desc

		for (String pid : supplier.getProductsIDs()) {
			Product p = supplier.getProduct(pid);
			if(p.getDescription().contains(descText)) {
				ProductView pv = newProductView(p);
				pvs.add(pv);
			}
		}
		return pvs;
	}

	public String buyProduct(String productId, int quantity)
			throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		sendDataToSign();

		// check product id
		if (productId == null)
			throwBadProductId("Product identifier cannot be null!");
		else if (productId.trim().length() == 0)
			throwBadProductId("Product identifier cannot be empty or whitespace!");
		else if (quantity <=0)
			throwBadQuantity("Product quantity must be positive!");


		Supplier supplier = Supplier.getInstance();
		if (supplier.getProduct(productId) == null)
			throwBadProductId("Product does not exist!");

		String purchaseID = null;
		try {
			purchaseID = supplier.buyProduct(productId, quantity);
		} catch (QuantityException e) {
			throwInsufficientQuantity("Not enough quantity in stock!");
		}
		return purchaseID;
	}

	// Auxiliary operations --------------------------------------------------

	public String ping(String name) {
		sendDataToSign();

	    if (name == null || name.trim().length() == 0)
			name = "friend";

		String wsName = endpointManager.getWsName();

		StringBuilder builder = new StringBuilder();
		builder.append("Hello ").append(name);
		builder.append(" from ").append(wsName);
		System.out.println("Ping request. Pinging back with: " + builder.toString());
		return builder.toString();
	}

	public void clear() {
		sendDataToSign();
		Supplier.getInstance().reset();
	}

	public void createProduct(ProductView productToCreate) throws BadProductId_Exception, BadProduct_Exception {
		sendDataToSign();
		// check null
		if (productToCreate == null)
			throwBadProduct("Product view cannot be null!");
		// check id
		String productId = productToCreate.getId();
		if (productId == null)
			throwBadProductId("Product identifier cannot be null!");
		productId = productId.trim();
		if (productId.length() == 0)
			throwBadProductId("Product identifier cannot be empty or whitespace!");
		// check description
		String productDesc = productToCreate.getDesc();
		if (productDesc == null)
			productDesc = "";
		// check quantity
		int quantity = productToCreate.getQuantity();
		if (quantity <= 0)
			throwBadProduct("Quantity must be a positive number!");
		// check price
		int price = productToCreate.getPrice();
		if (price <= 0)
			throwBadProduct("Price must be a positive number!");

		// create new product
		Supplier s = Supplier.getInstance();
		s.registerProduct(productId, productDesc, quantity, price);
	}

	public List<ProductView> listProducts() {
		sendDataToSign();
		Supplier supplier = Supplier.getInstance();
		List<ProductView> pvs = new ArrayList<ProductView>();
		for (String pid : supplier.getProductsIDs()) {
			Product p = supplier.getProduct(pid);
			ProductView pv = newProductView(p);
			pvs.add(pv);
		}
		return pvs;
	}

	public List<PurchaseView> listPurchases() {
		sendDataToSign();
		Supplier supplier = Supplier.getInstance();
		List<PurchaseView> pvs = new ArrayList<PurchaseView>();
		for (String pid : supplier.getPurchasesIDs()) {
			Purchase p = supplier.getPurchase(pid);
			PurchaseView pv = newPurchaseView(p);
			pvs.add(pv);
		}
		return pvs;
	}

	// View helpers ----------------------------------------------------------

	private ProductView newProductView(Product product) {
		ProductView view = new ProductView();
		view.setId(product.getId());
		view.setDesc(product.getDescription());
		view.setQuantity(product.getQuantity());
		view.setPrice(product.getPrice());
		return view;
	}

	private PurchaseView newPurchaseView(Purchase purchase) {
		PurchaseView view = new PurchaseView();
		view.setId(purchase.getPurchaseId());
		view.setProductId(purchase.getProductId());
		view.setQuantity(purchase.getQuantity());
		view.setUnitPrice(purchase.getUnitPrice());
		return view;
	}

	// Exception helpers -----------------------------------------------------

	/** Helper method to throw new BadProductId exception */
	private void throwBadProductId(final String message) throws BadProductId_Exception {
		BadProductId faultInfo = new BadProductId();
		faultInfo.message = message;
		throw new BadProductId_Exception(message, faultInfo);
	}

	/** Helper method to throw new BadProduct exception */
	private void throwBadProduct(final String message) throws BadProduct_Exception {
		BadProduct faultInfo = new BadProduct();
		faultInfo.message = message;
		throw new BadProduct_Exception(message, faultInfo);
	}

	/** Helper method to throw new BadText exception */
	private void throwBadText(final String message) throws BadText_Exception {
		BadText faultInfo = new BadText();
		faultInfo.message = message;
		throw new BadText_Exception(message, faultInfo);
	}

	/** Helper method to throw new BadQuantity exception */
	private void throwBadQuantity(final String message) throws BadQuantity_Exception {
		BadQuantity faultInfo = new BadQuantity();
		faultInfo.message = message;
		throw new BadQuantity_Exception(message, faultInfo);
	}

	/** Helper method to throw new InsufficientQuantity exception */
	private void throwInsufficientQuantity(final String message) throws InsufficientQuantity_Exception {
		InsufficientQuantity faultInfo = new InsufficientQuantity();
		faultInfo.message = message;
		throw new InsufficientQuantity_Exception(message, faultInfo);
	}

	public void sendDataToSign() {
		MessageContext mc = webServiceContext.getMessageContext();

		mc.put(SignHandler.REQUEST_PROPERTY, dataToSend);
	}

}
