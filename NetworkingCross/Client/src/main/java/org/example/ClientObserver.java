package org.example;

import javafx.application.Platform;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Controllers.MainController;
import org.example.model.Inscriere;
import org.example.service.ISwimmingObserver;

public class ClientObserver implements ISwimmingObserver {
    private static final Logger logger = LogManager.getLogger(ClientObserver.class);
    private MainController mainController;

    public ClientObserver() {
        logger.info("ClientObserver created");
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        logger.info("MainController set in ClientObserver");
    }

    @Override
    public void notifyRegister(Inscriere inscriere) throws Exception {
        logger.info("Received registration notification for participant: {}",
                inscriere.getParticipant().getNume());
        if (mainController != null) {
            Platform.runLater(() -> {
                try {
                    mainController.loadProbes();
                    mainController.loadDistancesAndStyles();
                } catch (Exception e) {
                    logger.error("Error during refresh: {}", e.getMessage(), e);
                }
            });
        }
    }
}