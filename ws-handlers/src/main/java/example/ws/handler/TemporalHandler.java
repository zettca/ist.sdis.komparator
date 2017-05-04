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

public class TemporalHandler implements SOAPHandler<SOAPMessageContext> {

  /** Date formatter used for outputting timestamps in ISO 8601 format */
  private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

  @Override
  public Set<QName> getHeaders() {
    return null;
  }

  @Override
  public boolean handleMessage(SOAPMessageContext smc) {
    Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
    return (outbound) ? handleOutbound(smc) : handleInbound(smc);
  }

  private boolean handleInbound(SOAPMessageContext smc) {
    try {
      SOAPEnvelope env = smc.getMessage().getSOAPPart().getEnvelope();
      SOAPHeader sh = getHeader(env);
      Name name = env.createName("time", "t", "http://supplier.komparator.org/");
      Iterator it = sh.getChildElements(name);
      // check header element
      if (!it.hasNext()) {
        System.out.println("Temporal: Header element not found.");
        return false;
      }
      SOAPElement el = (SOAPElement) it.next();
      String value = el.getValue();
      Date dateValue = dateFormatter.parse(value);
      Date dateNow = new Date();

      long diff = dateNow.getTime() - dateValue.getTime();
      if (diff > 3 * 1000) {
        System.out.println("Time interval superior to 3 seconds - rejecting.");
        return false;
      }

      String CONTEXT_PROPERTY = "timer.property";
      smc.put(CONTEXT_PROPERTY, value);
      smc.setScope(CONTEXT_PROPERTY, Scope.APPLICATION);

    } catch (SOAPException | ParseException e) {
      e.printStackTrace();
    }

    return true;
  }

  private boolean handleOutbound(SOAPMessageContext smc) {
    try {
      SOAPEnvelope env = smc.getMessage().getSOAPPart().getEnvelope();
      SOAPHeader sh = getHeader(env);
      Name name = env.createName("time", "t", "http://supplier.komparator.org/");
      SOAPHeaderElement el = sh.addHeaderElement(name);
      el.addTextNode(dateFormatter.format(new Date()));
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

  @Override
  public boolean handleFault(SOAPMessageContext smc) {
    return true;
  }

  @Override
  public void close(MessageContext messageContext) {
  }
}
