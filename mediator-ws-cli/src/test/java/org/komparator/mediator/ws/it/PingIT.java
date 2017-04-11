package org.komparator.mediator.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;


/**
 * Test suite
 */
public class PingIT extends BaseIT {

    @Test
    public void pingEmptyTest() {
        assertNotNull(mediatorClient.ping("test"));
    }

    @Test
    public void pingLinesTest() {
        String res = mediatorClient.ping("testing");
        String[] lines = res.split("\n");
        assertEquals(3, lines.length);
    }

}
