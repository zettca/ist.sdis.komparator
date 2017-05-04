package example.ws.handler;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.Iterator;
import java.util.Set;

public class CypherCCHandler implements SOAPHandler<SOAPMessageContext> {

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
            Name name = env.createName("buyCart", "ns2", "http://ws.mediator.komparator.org/"); // not sure
            Iterator it = sb.getChildElements(name);
            if (!it.hasNext()) {
                System.out.println("No BuyCart? Unexpected...");
            }

            SOAPElement el = (SOAPElement) it.next();
            String textCC = el.getValue();

            String cypheredCC = "";
            // TODO: Do Cypher CC

        } catch (SOAPException e) {
            e.printStackTrace();
        }

        return true;
    }

    private boolean handleInbound(SOAPMessageContext smc) {
        try {
            SOAPEnvelope env = smc.getMessage().getSOAPPart().getEnvelope();
            SOAPBody sb = env.getBody();
            Name name = env.createName("buyCartResponse", "ns2", "http://ws.mediator.komparator.org/"); // not sure
            Iterator it = sb.getChildElements(name);
            if (!it.hasNext()) {
                System.out.println("No BuyCart? Unexpected...");
            }

            SOAPElement el = (SOAPElement) it.next();
            String cypheredCC = el.getValue();

            // TODO: Do DeCypher CC
            String textCC = "";


        } catch (SOAPException e) {
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

}