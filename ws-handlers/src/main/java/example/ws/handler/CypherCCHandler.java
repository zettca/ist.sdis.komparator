package example.ws.handler;

import org.komparator.security.CryptoUtil;
import pt.ulisboa.tecnico.sdis.ws.cli.CAClient;
import pt.ulisboa.tecnico.sdis.ws.cli.CAClientException;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Iterator;
import java.util.Set;

import static javax.xml.bind.DatatypeConverter.printBase64Binary;

public class CypherCCHandler implements SOAPHandler<SOAPMessageContext> {
    private CAClient ca = setUp("http://sec.sd.rnl.tecnico.ulisboa.pt:8081/ca");

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext smc) {
        Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        return (outbound) ? handleOutbound(smc) : handleInbound(smc);
    }

    private boolean handleOutbound(SOAPMessageContext smc) {
        try {
            SOAPEnvelope env = smc.getMessage().getSOAPPart().getEnvelope();
            SOAPBody sb = env.getBody();
            Name name = env.createName("buyCart"/*, "ns2", "http://ws.mediator.komparator.org/"*/); // not sure
            Iterator it = sb.getChildElements(name);
            if (!it.hasNext()) {
                System.out.println("No BuyCart? Should be response...");
                return true;
            }

            SOAPElement el = (SOAPElement) it.next();
            name = env.createName("creditCardNr");
            it = el.getChildElements(name);
            if (!it.hasNext()) {
                System.out.println("No creditCardNr entry? Totally unexpected...");
                return false;
            }

            /* Mediator-cli sending to Mediator */

            el = (SOAPElement) it.next();
            String textCC = el.getValue();

            byte[] caBytes = ca.getPublicKey("T50_Mediator");
            PublicKey key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(caBytes));

            String cypheredCC = CryptoUtil.asymCipher(textCC.getBytes(), key);
            el.setValue(cypheredCC);

        } catch (SOAPException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    private boolean handleInbound(SOAPMessageContext smc) {
        try {
            SOAPEnvelope env = smc.getMessage().getSOAPPart().getEnvelope();
            SOAPBody sb = env.getBody();
            Name name = env.createName("buyCart"/*, "ns2", "http://ws.mediator.komparator.org/"*/); // not sure
            Iterator it = sb.getChildElements(name);
            if (!it.hasNext()) {
                System.out.println("No BuyCart? Should be response...");
                return true;
            }

            SOAPElement el = (SOAPElement) it.next();
            name = env.createName("creditCardNr");
            it = el.getChildElements(name);
            if (!it.hasNext()) {
                System.out.println("No creditCardNr entry? Totally unexpected...");
            }

            el = (SOAPElement) it.next();
            String cypheredCC = el.getValue();

            /* Mediator-cli sending to Mediator */


            // TODO: Do DeCypher CC
            String keyPath = "";
            char[] pass = "WkyodoJT".toCharArray();
            PrivateKey key = CryptoUtil.getPrivateKeyFromKeystore(keyPath, pass, "T50_Mediator", pass);
            byte[] bytesCC = CryptoUtil.asymDecipher(el.getValue(), key);
            String textCC = printBase64Binary(bytesCC);
            el.setValue(textCC);


        } catch (SOAPException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext smc) {
        return true;
    }

    @Override
    public void close(MessageContext messageContext) {
        // nothing to clean up
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

}