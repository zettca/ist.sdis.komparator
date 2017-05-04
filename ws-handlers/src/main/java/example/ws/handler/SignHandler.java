package example.ws.handler;

import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import pt.ulisboa.tecnico.sdis.ws.cli.CAClient;
import pt.ulisboa.tecnico.sdis.ws.cli.CAClientException;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

import org.komparator.security.CryptoUtil;

public class SignHandler implements SOAPHandler<SOAPMessageContext> {

	private CAClient ca;

	public static final String REQUEST_PROPERTY = "my.request.property";

	public static final String RESPONSE_PROPERTY = "my.response.property";

	public static final String REQUEST_HEADER = "myRequestHeader";
	public static final String REQUEST_NS = "urn:example";

	public static final String RESPONSE_HEADER = "myResponseHeader";
	public static final String RESPONSE_NS = REQUEST_NS;

	public static final String CONTEXT_PROPERTY = "my.property";

	public Set<QName> getHeaders() {
		return null;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		System.out.println("\n\n\n\n Sign Handler: Handling message.");

		ca = setUp("http://sec.sd.rnl.tecnico.ulisboa.pt:8081/ca");
		
		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		try {
			SOAPMessage msg = smc.getMessage();
			SOAPPart sp = msg.getSOAPPart();
			SOAPEnvelope se = sp.getEnvelope();
			SOAPHeader sh = se.getHeader();

			// check header
			if (sh == null) {
				System.out.println("Header not found.");
				return true;
			}
			SOAPHeaderElement headerElement = null;
			SOAPElement element = null;

			if (outboundElement) {
				System.out.println("OUTBOUND SIGNHANDLER");

				String dataReceived = (String) smc.get(REQUEST_PROPERTY);

				// Get data from entity
				String[] parseData = dataReceived.split("#");

				String keyAlias = parseData[0];
				String KeyStorePassword = "WkyodoJT";
				String keyPassword = "WkyodoJT";
				String keyStoreFile = parseData[1];
				// get timestamp from soap header
				
				Name name = se.createName("time", "t", "http://supplier.komparator.org/");
				Iterator it = sh.getChildElements(name);
				// check header element
				if (!it.hasNext()) {
					System.out.println("Header element not found.");
					return true;
				}
				element = (SOAPElement) it.next();

				// get header element value
				String timestamp = element.getValue();
				
				System.out.println("\n\n\n\n\n timestamp\n\n\n"+timestamp+"\n\n\n\n");

				
				
				ByteArrayOutputStream out = new ByteArrayOutputStream();

				msg.writeTo(out);

				String messageToSign = new String(out.toByteArray());
				
				final byte[] byteMessage = messageToSign.getBytes();
				String messageText = printBase64Binary(byteMessage);

				
				// make digital signature hashing
				byte[] digitalSignature = null;
				
				PrivateKey pk = CryptoUtil.getPrivateKeyFromKeystore(keyStoreFile, KeyStorePassword.toCharArray() 
							, keyAlias.toLowerCase(), keyPassword.toCharArray());
				
				digitalSignature = CryptoUtil.makeDigitalSignature(byteMessage,pk);
				
				String signedMessageText = printBase64Binary(digitalSignature);
				
				// add signature to element value
				String dataToSend = timestamp + "#" + keyAlias + "#" + signedMessageText + "#" + messageToSign;
				
				// add header element (name, namespace prefix, namespace)
				name = se.createName(REQUEST_HEADER, "e", REQUEST_NS);
				headerElement = sh.addHeaderElement(name);
				headerElement.addTextNode(dataToSend);
				

			} else {
				System.out.println("INBOUND SIGNHANDLER.");
				
				// get first header element
				Name name = se.createName(REQUEST_HEADER, "e", REQUEST_NS);
				Iterator it = sh.getChildElements(name);
				// check header element
				if (!it.hasNext()) {
					System.out.printf("Header element %s not found.%n", REQUEST_HEADER);
					return true; //FALSEEEEEE
				}
				element = (SOAPElement) it.next();
		
				// get header element value
				String dataReceived = element.getValue();
				
				String[] parseData = dataReceived.split("#");
				
				String timestamp = parseData[0];
				String keyAlias = parseData[1];
				String signedMessageText = parseData[2];
				String messageText = parseData[3];
								
				// Check if certificate was signed by CA
				String entityCertificateString = ca.getCertificate(keyAlias.toLowerCase());
				byte[] entityCertificateBytes = parseBase64Binary(entityCertificateString);
				byte[] caPublicKeyBytes = ca.getPublicKey("ca");
				PublicKey caPublicKey = KeyFactory.getInstance("RSA").
						generatePublic(new X509EncodedKeySpec(caPublicKeyBytes));
				
				CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
				
				InputStream in = new ByteArrayInputStream(entityCertificateBytes);
				X509Certificate cert = (X509Certificate)certFactory.generateCertificate(in);

				cert.verify(caPublicKey);
				System.out.println("Certificate was signed by ca");
				// End check
				
				byte[] digitalSignature = parseBase64Binary(signedMessageText);
				byte[] originalMessage = parseBase64Binary(messageText);
				
				byte[] publicKeyInBytes = ca.getPublicKey(keyAlias.toLowerCase());
				PublicKey publicKey = null;
				
				publicKey = KeyFactory.getInstance("RSA").
						generatePublic(new X509EncodedKeySpec(publicKeyInBytes));
				
				// verify signature
				boolean isValid = false;
				
				isValid = CryptoUtil.verifyDigitalSignature(digitalSignature, originalMessage, publicKey);
				
				if (isValid) {
					System.out.println("The digital signature is valid");
				} else {
					System.out.println("The digital signature is NOT valid");
					return false;
				}
				
			}
		} catch (Exception e) {
			System.out.print("Caught exception in handleMessage: ");
			System.out.println(e);
			System.out.println("Continue normal processing...");
		}

		return true;
	}

	public CAClient setUp(String url) {
		if (ca == null) {
			try {
				ca = new CAClient(url);
			} catch (CAClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ca;
	}


	@Override
	public void close(MessageContext context) {	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {return false;}
}