package org.komparator.security.handler;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.*;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import static javax.xml.bind.DatatypeConverter.printBase64Binary;

public class FreshnessHandler implements SOAPHandler<SOAPMessageContext> {

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext smc) {
        Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        System.out.println("\n\n\tFreshness Handler: Handling " + ((outbound) ? "OUT" : "IN") + "bound message.");
        return (outbound) ? handleOutbound(smc) : handleInbound(smc);
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
            final byte array[] = new byte[16];
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.nextBytes(array);

            SOAPEnvelope env = smc.getMessage().getSOAPPart().getEnvelope();
            SOAPHeader sh = getHeader(env);
            Name name = env.createName("token", "id", "http://supplier.komparator.org/");
            SOAPHeaderElement el = sh.addHeaderElement(name);
            el.addTextNode(printBase64Binary(array));

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
            Name name = env.createName("token", "id", "http://supplier.komparator.org/");
            Iterator it = sh.getChildElements(name);
            if (!it.hasNext()) {
                System.out.println("Freshness: Header element not found.");
                return true;
            }
            SOAPElement el = (SOAPElement) it.next();
            String token = el.getValue();

            String path = "tokens.tsv";
            if (!validToken(path, token)) { // Rejects for existing token/errors
                System.out.println("Freshness: Invalid token. Rejecting message!");
                return false;
            }

            addToken(path, token);

            // TODO: Maybe reload after 10 seconds? No need tho
            String CONTEXT_PROPERTY = "token.property";
            smc.put(CONTEXT_PROPERTY, token);
            smc.setScope(CONTEXT_PROPERTY, MessageContext.Scope.APPLICATION);

        } catch (SOAPException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean validToken(String path, String token) {
        try {
            FileInputStream is = new FileInputStream(path);

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.defaultCharset())); // charset?
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equals(token)) {
                    System.out.println("Token found! Rejecting...");
                    return false;
                }
            }
            reader.close();
            is.close();
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("File does not exist!");
        } catch (IOException e) {
            System.out.println("Error reading file!");
        }
        return true; // default case
    }

    private void addToken(String path, String token) {
        try {
            BufferedWriter output = new BufferedWriter(new FileWriter(path, true));
            output.append(token);
            output.newLine();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SOAPHeader getHeader(SOAPEnvelope env) throws SOAPException {
        SOAPHeader header = env.getHeader();
        if (header == null) header = env.addHeader();
        return header;
    }

}
