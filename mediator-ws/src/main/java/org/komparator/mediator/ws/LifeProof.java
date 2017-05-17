package org.komparator.mediator.ws;

import org.komparator.mediator.ws.cli.MediatorClient;
import org.komparator.mediator.ws.cli.MediatorClientException;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

public class LifeProof extends TimerTask {
    private static final long TIMEOUT_LIMIT = (5 + 2) * 1000;
    private static final String BACKUP_URL = "http://localhost:8072/mediator-ws/endpoint";
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
            long timeDiff = new Date().getTime() - this.endpoint.lastAliveTime;
            if (timeDiff > TIMEOUT_LIMIT) {
                handleBackupTakeover();
            }
        }
    }

    private void handlePrimary() {
        try {
            MediatorPortType port = endpoint.getPort();
            MediatorClient backupMediatorClient = new MediatorClient(BACKUP_URL);
            List<CartView> carts = port.listCarts();
            List<ShoppingResultView> shopHistory = port.shopHistory();
            backupMediatorClient.imAlive();
            backupMediatorClient.updateCart(carts);
            backupMediatorClient.updateShopHistory(shopHistory);
        } catch (MediatorClientException e) {
            System.out.println("Error connecting to backup Mediator...");
        }
    }

    private void handleBackupTakeover() {
        // TODO: register on UDDI and inform front-end
    }
}
