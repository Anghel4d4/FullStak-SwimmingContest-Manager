package org.example.Controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.ClientObserver;
import org.example.model.Participant;
import org.example.model.Proba;
import org.example.model.User;
import org.example.service.ISwimmingServices;

import java.util.List;

public class MainController {
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private Button logoutButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button registerButton;

    @FXML
    private TableView<Proba> probeTableView;
    @FXML
    private TableColumn<Proba, String> distanceColumn;
    @FXML
    private TableColumn<Proba, String> styleColumn;
    @FXML
    private TableColumn<Proba, Integer> participantsColumn;

    @FXML
    private ComboBox<Integer> distanceComboBox;
    @FXML
    private ComboBox<String> styleComboBox;

    @FXML
    private TableView<Participant> participantsTableView;
    @FXML
    private TableColumn<Participant, String> nameColumn;
    @FXML
    private TableColumn<Participant, Integer> ageColumn;
    @FXML
    private TableColumn<Participant, Integer> probeCountColumn;

    private final User user;
    private final ISwimmingServices services;
    private final ClientObserver observer;

    public MainController(User user, ISwimmingServices services, ClientObserver observer) {
        this.user = user;
        this.services = services;
        this.observer = observer;
        logger.info("MainController created for user: {}", user.getUsername());
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadProbes();
        loadDistancesAndStyles();

        searchButton.setOnAction(event -> handleSearch());
        registerButton.setOnAction(event -> handleRegisterParticipant());
        logoutButton.setOnAction(event -> handleLogout());
        refreshButton.setOnAction(event -> loadProbes());

    }


    private void setupTableColumns() {
        // Probe table setup with bindings
        distanceColumn.setCellValueFactory(cellData -> {
            Proba proba = cellData.getValue();
            return javafx.beans.binding.Bindings.createStringBinding(() -> String.valueOf(proba.getDistanta()));
        });

        styleColumn.setCellValueFactory(cellData -> {
            Proba proba = cellData.getValue();
            return javafx.beans.binding.Bindings.createStringBinding(proba::getStil);
        });

        participantsColumn.setCellValueFactory(cellData -> {
            Proba proba = cellData.getValue();
            return javafx.beans.binding.Bindings.createObjectBinding(proba::getParticipantCount);
        });

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nume"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("varsta"));
        probeCountColumn.setCellValueFactory(new PropertyValueFactory<>("eventCount"));
    }


    public void loadProbes() {
        services.getAllProbes()
                .thenAccept(probes -> Platform.runLater(() -> {
                    ObservableList<Proba> probeData = FXCollections.observableArrayList(probes);
                    probeTableView.setItems(probeData);
                    probeTableView.refresh();
                    logger.info("Probe table loaded successfully");
                }))
                .exceptionally(ex -> {
                    showAlert(Alert.AlertType.ERROR, "Error", "Error loading probe table: " + ex.getMessage());
                    return null;
                });
    }

    public void loadDistancesAndStyles() {
        services.getAllProbes()
                .thenAccept(probes -> Platform.runLater(() -> {
                    ObservableList<Integer> distances = FXCollections.observableArrayList(probes.stream().map(Proba::getDistanta).distinct().toList());
                    ObservableList<String> styles = FXCollections.observableArrayList(probes.stream().map(Proba::getStil).distinct().toList());
                    distanceComboBox.setItems(distances);
                    styleComboBox.setItems(styles);
                    logger.info("Distances and styles loaded successfully");
                }))
                .exceptionally(ex -> {
                    showAlert(Alert.AlertType.ERROR, "Error", "Error loading filters: " + ex.getMessage());
                    return null;
                });
    }

    @FXML
    public void handleSearch() {
        Integer distanta = distanceComboBox.getValue();
        String stil = styleComboBox.getValue();

        if (distanta == null || stil == null) {
            showAlert(Alert.AlertType.WARNING, "Input Required", "Please select both distance and style");
            return;
        }

        services.findParticipantsByProba(distanta, stil)
                .thenAccept(participants -> Platform.runLater(() -> {
                    ObservableList<Participant> participantsData = FXCollections.observableArrayList(participants);
                    participantsTableView.setItems(participantsData);
                    logger.info("Search completed, {} participants found", participants.size());
                }))
                .exceptionally(ex -> {
                    showAlert(Alert.AlertType.ERROR, "Search Error", "Failed to search for participants: " + ex.getMessage());
                    return null;
                });
    }


    private void handleRegisterParticipant() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/register-view.fxml"));
            RegisterController controller = new RegisterController(services, observer);
            loader.setController(controller);

            Stage registerStage = new Stage();
            registerStage.setTitle("Register Participant");
            registerStage.setScene(new Scene(loader.load()));

            controller.setDialogStage(registerStage);
            registerStage.show();

        } catch (Exception e) {
            logger.error("Error opening register window", e);
            showAlert(Alert.AlertType.ERROR, "Error","Error opening register window: " + e.getMessage());
        }
    }

    private void handleLogout() {
        services.logout(user, observer)
                .thenRun(() -> Platform.runLater(() -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
                        LoginController controller = new LoginController(services);
                        loader.setController(controller);
                        Stage loginStage = new Stage();
                        loginStage.setTitle("Login");
                        loginStage.setScene(new Scene(loader.load()));

                        Stage currentStage = (Stage) logoutButton.getScene().getWindow();
                        loginStage.show();
                        currentStage.close();

                        logger.info("Logged out successfully, login window opened");
                    } catch (Exception e) {
                        logger.error("Error during logout redirect", e);
                        showAlert(Alert.AlertType.ERROR, "Error", "Error during logout: " + e.getMessage());
                    }
                }))
                .exceptionally(ex -> {
                    showAlert(Alert.AlertType.ERROR, "Error", "Error during logout: " + ex.getMessage());
                    return null;
                });
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}