package org.example.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.ClientObserver;
import org.example.model.Inscriere;
import org.example.model.Participant;
import org.example.model.Proba;
import org.example.service.ISwimmingServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class RegisterController {
    private static final Logger logger = LogManager.getLogger();


    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField nameField;
    @FXML
    private TextField prenumeField;
    @FXML
    private TextField ageField;
    @FXML
    private TableView<Proba> probeSelectionTableView;
    @FXML
    private TableColumn<Proba, String> distanceSelectColumn;
    @FXML
    private TableColumn<Proba, String> styleSelectColumn;
    @FXML
    private Label errorMessageLabel;

    private final ISwimmingServices services;
    private final ClientObserver observer;
    private Stage dialogStage;

    public RegisterController(ISwimmingServices services, ClientObserver observer) {
        this.services = services;
        this.observer = observer;
        logger.info("RegisterController created");
    }

    @FXML
    public void initialize() {
        setupTable();
        loadProbes();
        saveButton.setOnAction(event -> handleSaveRegistration());
        cancelButton.setOnAction(event -> handleCancel());
        logger.info("RegisterController initialized");
    }

    private void setupTable() {
        distanceSelectColumn.setCellValueFactory(new PropertyValueFactory<>("distanta"));
        styleSelectColumn.setCellValueFactory(new PropertyValueFactory<>("stil"));
        probeSelectionTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void loadProbes() {
        services.getAllProbes()
                .thenAccept(probes -> Platform.runLater(() -> {
                    probeSelectionTableView.getItems().setAll(probes);
                    logger.info("Loaded {} probe for selection", probes.size());
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> showError("Error loading probe: " + ex.getMessage()));
                    return null;
                });
    }

    // Java
    @FXML
    private void handleSaveRegistration() {
        if (!validateInputs()) {
            return;
        }

        String name = nameField.getText().trim();
        String prenume = prenumeField.getText().trim();
        int age = Integer.parseInt(ageField.getText().trim());
        List<Proba> selectedProbe = new ArrayList<>(probeSelectionTableView.getSelectionModel().getSelectedItems());

        services.findNameAndPrenume(name, prenume)
                .thenCompose(optionalParticipant -> {
                    if (optionalParticipant.isPresent()) {
                        return CompletableFuture.completedFuture(optionalParticipant.get());
                    } else {
                        Participant newP = new Participant(null, name, prenume, age);
                        return services.saveParticipant(newP, observer);
                    }
                })
                .thenAccept(savedParticipant -> {
                    // NOW savedParticipant has the correct ID from the server
                    List<CompletableFuture<Void>> registrations = selectedProbe.stream()
                            .map(proba -> {
                                Inscriere i = new Inscriere(null, savedParticipant, proba);
                                return services.addInscriere(i, observer);
                            }).toList();

                    CompletableFuture.allOf(registrations.toArray(new CompletableFuture[0]))
                            .thenRun(() -> Platform.runLater(() -> {
                                showSuccess();
                                dialogStage.close();
                            }))
                            .exceptionally(ex -> {
                                Platform.runLater(() -> showError("Registration failed: " + ex.getMessage()));
                                return null;
                            });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> showError("Error: " + ex.getMessage()));
                    return null;
                });
    }

    private boolean validateInputs() {
        String name = nameField.getText().trim();
        String prenume = prenumeField.getText().trim();
        String ageText = ageField.getText().trim();

        if (name.isEmpty() || prenume.isEmpty() || ageText.isEmpty()) {
            showError("All the boxes should be completed!");
            return false;
        }

        try {
            int age = Integer.parseInt(ageText);
            if (age <= 0 || age > 120) {
                showError("The age should be a valid one (1-120)!");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("The age should be a number!");
            return false;
        }

        List<Proba> selectedProbe = probeSelectionTableView.getSelectionModel().getSelectedItems();
        if (selectedProbe.isEmpty()) {
            showError("Select at least one Proba!");
            return false;
        }

        return true;
    }

    @FXML
    private void handleCancel() {
        logger.info("Registration cancelled");
        dialogStage.close();
    }

    private void showError(String message) {
        errorMessageLabel.setText(message);
        errorMessageLabel.setVisible(true);
        logger.warn("Validation error: {}", message);
    }

    private void showSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration Completed");
        alert.setHeaderText(null);
        alert.setContentText("Participant registered with success!");
        alert.showAndWait();
        logger.info("Registration completed successfully");
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
}