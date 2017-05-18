package org.komparator.mediator.ws;

import org.komparator.mediator.ws.cli.MediatorClient;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;

import java.util.Date;
import java.util.TimerTask;

public class LifeProof extends TimerTask {
    static final String BACKUP_URL = "http://localhost:8072/mediator-ws/endpoint";
    static final long PING_INTERVAL = 5 * 1000;
    static final long PING_OFFSET = 2 * 1000;
    private MediatorEndpointManager endpoint;

    LifeProof(MediatorEndpointManager endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void run() {
        if (endpoint.isPrimary) {
            System.out.println("Primary: Sending imAlive signal to BackupMediator...");
            new MediatorClient(BACKUP_URL).imAlive();

        } else {
            long timeDiff = new Date().getTime() - this.endpoint.lastAliveTime;
            if (timeDiff > PING_INTERVAL + PING_OFFSET) {
                System.out.println("Mediator did not send imAlive. Taking over...");
                handleBackupTakeover();
            }
        }
    }

    private void handleBackupTakeover() {
        try {
            endpoint.getUddiNaming().rebind(endpoint.getWsName(), BACKUP_URL);
            endpoint.isPrimary = true;
            MediatorApp.timer.cancel();
            MediatorApp.timer.purge();
            this.cancel();
        } catch (UDDINamingException e) {
            System.out.println("Error rebinding UDDI for " + endpoint.getWsName());
        }
    }
}
