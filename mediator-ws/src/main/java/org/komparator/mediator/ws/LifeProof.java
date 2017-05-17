package org.komparator.mediator.ws;

import org.komparator.mediator.ws.cli.MediatorClient;
import org.komparator.mediator.ws.cli.MediatorClientException;

import java.util.TimerTask;

public class LifeProof extends TimerTask {

    private MediatorEndpointManager endpoint;

    public LifeProof(MediatorEndpointManager endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void run() {
        if (this.endpoint.isPrimary) {
            System.out.println("Sending imAlive signal to backup Mediator...");
            handlePrimary();
        } else {
            System.out.println("Backup mediator stuffs...");
            handleBackup();
            // TODO: handle backup
        }

    }

    private void handlePrimary() {
        try {
            MediatorClient mediatorClient = new MediatorClient("http://localhost:8072/mediator-ws/endpoint");
            mediatorClient.imAlive();
        } catch (MediatorClientException e) {
            System.out.println("Error connecting to backup Mediator...");
        }
    }

    private void handleBackup() {
        // TODO: implement this
    }

}
