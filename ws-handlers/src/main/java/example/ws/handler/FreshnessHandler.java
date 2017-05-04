package example.ws.handler;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

public class FreshnessHandler implements SOAPHandler<SOAPMessageContext> {

    private SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss.SSS");

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext smc) {
        Boolean isOutbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        return (isOutbound) ? handleOutbound(smc) : handleInbound(smc);
    }

    @Override
    public boolean handleFault(SOAPMessageContext smc) {
        return true;
    }

    @Override
    public void close(MessageContext messageContext) {
        // nothing to clean up
    }

    private boolean handleOutbound(SOAPMessageContext smc) {
        try {
            long timestamp = new Date().getTime();
            final byte array[] = new byte[8];
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.nextBytes(array);

            SOAPEnvelope env = smc.getMessage().getSOAPPart().getEnvelope();
            SOAPHeader sh = getHeader(env);
            Name name = env.createName("identifier", "id", "http://supplier.komparator.org/");
            SOAPHeaderElement el = sh.addHeaderElement(name);
            el.addTextNode(printHexBinary(array));

        } catch (NoSuchAlgorithmException e) {
            System.out.println("SecureRandom algorithm does not exist" + e.getMessage());
        } catch (SOAPException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean handleInbound(SOAPMessageContext smc) {
        try {
            SOAPEnvelope env = smc.getMessage().getSOAPPart().getEnvelope();
            SOAPHeader sh = getHeader(env);
            Iterator it = sh.getAllAttributes();
            if (!it.hasNext()) {
                System.out.println("Freshness: Header element not found.");
                return false;
            }
            SOAPElement el = (SOAPElement) it.next();
            String value = el.getValue();
            System.out.println("GOT" + value);

            String CONTEXT_PROPERTY = "identifier.property";
            smc.put(CONTEXT_PROPERTY, value);
            smc.setScope(CONTEXT_PROPERTY, MessageContext.Scope.APPLICATION);

        } catch (SOAPException e) {
            e.printStackTrace();
        }
        return true;
    }

    private SOAPHeader getHeader(SOAPEnvelope env) throws SOAPException {
        SOAPHeader header = env.getHeader();
        if (header == null) header = env.addHeader();
        return header;
    }

}
