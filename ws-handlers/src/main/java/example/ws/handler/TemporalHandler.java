package example.ws.handler;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import static example.ws.handler.HeaderHandler.CONTEXT_PROPERTY;

public class TemporalHandler implements SOAPHandler<SOAPMessageContext> {

  /** Date formatter used for outputting timestamps in ISO 8601 format */
  private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
  private Date outboundTime = null;

  /**
   * Gets the header blocks that can be processed by this Handler instance. If
   * null, processes all.
   */
  @Override
  public Set<QName> getHeaders() {
    return null;
  }

  @Override
  public boolean handleMessage(SOAPMessageContext smc) {
    System.out.println("AddTimerToHeaderHandler: Handling message.");

    Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

    try {
      // get SOAP envelope
      SOAPMessage msg = smc.getMessage();
      SOAPPart sp = msg.getSOAPPart();
      SOAPEnvelope se = sp.getEnvelope();
      SOAPHeader sh = se.getHeader();

      if (outboundElement) {
        System.out.println("Writing header in outbound SOAP message...");

        // add header
        if (sh == null) sh = se.addHeader();

        // add header timestamp element
        Name name = se.createName("time", "t", "http://time");
        SOAPHeaderElement element = sh.addHeaderElement(name);

        outboundTime = new Date();
        element.addTextNode(dateFormatter.format(outboundTime));

      } else {
        System.out.println("Reading header in inbound SOAP message...");

        // check header
        if (sh == null) {
          System.out.println("Header not found.");
          return true;
        }

        // get first header element
        Name name = se.createName("time", "t", "http://time");
        Iterator it = sh.getChildElements(name);
        // check header element
        if (!it.hasNext()) {
          System.out.println("Header element not found.");
          return true;
        }
        SOAPElement element = (SOAPElement) it.next();

        // get header element value
        String valueString = element.getValue();
        Date inboundTime = dateFormatter.parse(valueString);

        // print received header
        if (outboundTime != null && inboundTime != null ) {
          long diff = inboundTime.getTime() - outboundTime.getTime();
          if (diff > 3*1000){
            System.out.println("Time interval superior to 3 seconds - rejecting.");
            // TODO: Handle reject differently? This breaks the other part
            // Ex: Expected PingResponse, Get Ping (returns message)
            return true;
          }
        }

        // put header in a property context
        smc.put(CONTEXT_PROPERTY, valueString);
        // set property scope to application client/server class can
        // access it
        smc.setScope(CONTEXT_PROPERTY, Scope.APPLICATION);
      }

    } catch (SOAPException e) {
      System.out.print("Caught exception in handleMessage: ");
      System.out.println(e);
      System.out.println("Continue normal processing...");
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return true;
  }

  /** The handleFault method is invoked for fault message processing. */
  @Override
  public boolean handleFault(SOAPMessageContext smc) {
   // logToSystemOut(smc);
    return true;
  }

  /**
   * Called at the conclusion of a message exchange pattern just prior to the
   * JAX-WS runtime dispatching a message, fault or exception.
   */
  @Override
  public void close(MessageContext messageContext) {
    // nothing to clean up
  }
}
