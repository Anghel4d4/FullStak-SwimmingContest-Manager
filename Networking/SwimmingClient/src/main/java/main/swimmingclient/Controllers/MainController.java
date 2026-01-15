package main.swimmingclient.Controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.swimmingmodel.dto.ParticipantDTO;
import main.swimmingmodel.dto.ProbaDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import main.swimmingmodel.Participant;
import main.swimmingmodel.Proba;
import main.swimmingmodel.User;
import main.swimmingservices.ISwimmingServices;
import main.swimmingclient.ClientObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private TableView<ProbaDTO> probeTableView;
    @FXML
    private TableColumn<ProbaDTO, String> distanceColumn;
    @FXML
    private TableColumn<ProbaDTO, String> styleColumn;
    @FXML
    private TableColumn<ProbaDTO, Integer> participantsColumn;

    @FXML
    private ComboBox<String> distanceComboBox;
    @FXML
    private ComboBox<String> styleComboBox;

    @FXML
    private TableView<ParticipantDTO> participantsTableView;
    @FXML
    private TableColumn<ParticipantDTO, String> nameColumn;
    @FXML
    private TableColumn<ParticipantDTO, String> ageColumn;
    @FXML
    private TableColumn<ParticipantDTO, Integer> probeCountColumn;

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
            ProbaDTO proba = cellData.getValue();
            return javafx.beans.binding.Bindings.createStringBinding(proba::getDistanta);
        });

        styleColumn.setCellValueFactory(cellData -> {
            ProbaDTO proba = cellData.getValue();
            return javafx.beans.binding.Bindings.createStringBinding(proba::getStil);
        });

        participantsColumn.setCellValueFactory(cellData -> {
            ProbaDTO proba = cellData.getValue();
            return javafx.beans.binding.Bindings.createObjectBinding(proba::getParticipantCount);
        });

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nume"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("varsta"));
        probeCountColumn.setCellValueFactory(new PropertyValueFactory<>("eventCount"));
    }


    public void loadProbes() {
        try {
            List<ProbaDTO> probes = services.getAllProbes();

            ObservableList<ProbaDTO> probeData = FXCollections.observableArrayList(probes);
            probeTableView.setItems(probeData);
            probeTableView.refresh();

            logger.info("Probe table loaded successfully with participant counts");
        } catch (Exception e) {
            logger.error("Error loading probe table", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading probe table: " + e.getMessage());
        }
    }

    public void loadDistancesAndStyles() {
        try {
            List<ProbaDTO> probes = services.getAllProbes();
            distanceComboBox.getItems().clear();
            styleComboBox.getItems().clear();

            ObservableList<String> distances = FXCollections.observableArrayList(
                    probes.stream()
                            .map(ProbaDTO::getDistanta)
                            .distinct()
                            .toList()
            );
            ObservableList<String> styles = FXCollections.observableArrayList(
                    probes.stream()
                            .map(ProbaDTO::getStil)
                            .distinct()
                            .toList()
            );

            distanceComboBox.setItems(distances);
            styleComboBox.setItems(styles);
            logger.info("Distances and styles loaded successfully");
        } catch (Exception e) {
            logger.error("Error loading distances and styles", e);
            showAlert(Alert.AlertType.ERROR, "Error","Error loading filters: " + e.getMessage());
        }
    }

    @FXML
    public void handleSearch() {
        String distanta = distanceComboBox.getValue();
        String stil = styleComboBox.getValue();

        if (distanta == null || stil == null) {
            showAlert(Alert.AlertType.WARNING, "Input Required",
                    "Please select both distance and style");
            return;
        }
        try {
            // i create a list where i store the found participants to use it to display them
            List<ParticipantDTO> participants = services.findParticipantsByProba(distanta, stil);
            ObservableList<ParticipantDTO> participantsData = FXCollections.observableArrayList(participants);
            participantsTableView.setItems(participantsData);
            logger.info("Search completed, {} participants found", participants.size());
        } catch (Exception e) {
            logger.error("Failed to search for participants", e);
            showAlert(Alert.AlertType.ERROR, "Search Error",
                    "Failed to search for participants: " + e.getMessage());
        }
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
        try {
            services.logout(user, observer);
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
            logger.error("Error during logout", e);
            showAlert(Alert.AlertType.ERROR, "Error","Error during logout: " + e.getMessage());
        }
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