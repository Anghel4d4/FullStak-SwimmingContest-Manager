package ro.mpp2025.Controller;

import Utils.JdbcUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ro.mpp2025.Domain.Participant;
import ro.mpp2025.Domain.Proba;
import ro.mpp2025.Repository.UserRepository;
import ro.mpp2025.Service.Service;
import ro.mpp2025.Service.UserService;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class MainController {
    @FXML
    private TableView<Proba> probeTableView;
    @FXML
    private TableColumn<Proba, String> distanceColumn;
    @FXML
    private TableColumn<Proba, String> styleColumn;
    @FXML
    private TableColumn<Proba, Integer> participantsColumn;

    @FXML
    private ComboBox<String> distanceComboBox;
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

    private Service service;
    private Stage primaryStage;

    public void initialize() {
        // Configurare coloane tabel probe
        distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distanta"));
        styleColumn.setCellValueFactory(new PropertyValueFactory<>("stil"));
        participantsColumn.setCellValueFactory(new PropertyValueFactory<>("participantCount"));

        // Configurare coloane tabel participanți
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nume"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("varsta"));
        probeCountColumn.setCellValueFactory(new PropertyValueFactory<>("eventCount"));
    }

    public void setServices(Service service) {
        this.service = service;
        loadProbes();
        loadDistancesAndStyles();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void loadProbes() {
        List<Proba> probes = service.getProbaService().getAllProbes();
        for (Proba proba : probes) {
            int participantCount = service.getProbaService().getParticipantCount(proba.getId());
            proba.setParticipantCount(participantCount);
        }
        probeTableView.getItems().setAll(probes);
    }

    private void loadDistancesAndStyles() {
        List<String> distances = service.getProbaService().getDistinctDistances();
        distanceComboBox.getItems().setAll(distances);

        List<String> styles = service.getProbaService().getDistinctStyles();
        styleComboBox.getItems().setAll(styles);
    }

    public void refreshProbeList() {
        loadProbes();
    }

    @FXML
    private void handleSearch() {
        String distance = distanceComboBox.getValue();
        String style = styleComboBox.getValue();

        if (distance == null || style == null) {
            showAlert(Alert.AlertType.WARNING, "Atentie", "Selectati atat distanta cat si stilul!");
            return;
        }

        Proba selectedProba = service.getProbaService().findByDistanceAndStyle(distance, style);
        if (selectedProba == null) {
            showAlert(Alert.AlertType.INFORMATION, "Informatie", "Nu s-a gasit nicio proba cu criteriile selectate.");
            return;
        }

        List<Participant> participants = service.getParticipantService().findByProba(selectedProba, "id");
        for (Participant participant : participants) {
            int eventCount = service.getParticipantService().getEventCount(participant.getId());
            participant.setEventCount(eventCount);
        }
        participantsTableView.getItems().setAll(participants);
    }

    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/register-view.fxml"));
            Parent root = loader.load();

            RegisterController controller = loader.getController();
            controller.setServices(service);
            controller.setMainController(this);

            Stage stage = new Stage();
            stage.setTitle("Inregistrare participant nou");
            stage.setScene(new Scene(root));
            controller.setPrimaryStage(stage);

            stage.showAndWait();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Eroare", "Eroare la deschiderea ferestrei de inregistrare: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
            Parent root = loader.load();

            LoginController controller = loader.getController();

            // Initialize the UserService
            Properties props = new Properties();
            try (FileReader reader = new FileReader("bd.config")) {
                props.load(reader);
            }

            JdbcUtils jdbcUtils = new JdbcUtils(props);
            UserService userService = new UserService(new UserRepository(jdbcUtils));
            controller.setUserService(userService);

            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            controller.setPrimaryStage(stage);

            primaryStage.close();

            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error opening the login window: " + e.getMessage());
        }
    }



    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}