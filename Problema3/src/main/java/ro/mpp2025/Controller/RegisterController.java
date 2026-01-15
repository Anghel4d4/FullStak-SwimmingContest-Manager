package ro.mpp2025.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ro.mpp2025.Domain.Participant;
import ro.mpp2025.Domain.Proba;
import ro.mpp2025.Domain.Inscriere;
import ro.mpp2025.Service.Service;


import java.util.ArrayList;
import java.util.List;

public class RegisterController {
    public TextField prenumeField;
    @FXML
    private TextField nameField;
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

    private Service service;
    private MainController mainController;
    private Stage primaryStage;

    public void initialize() {
        distanceSelectColumn.setCellValueFactory(new PropertyValueFactory<>("distanta"));
        styleSelectColumn.setCellValueFactory(new PropertyValueFactory<>("stil"));
    }

    public void setServices(Service service) {
        this.service = service;

        loadProbes();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void loadProbes() {
        List<Proba> probes = service.getProbaService().getAllProbes();
        probeSelectionTableView.getItems().setAll(probes);
    }

    @FXML
    private void handleCancel() {
        primaryStage.close();
    }

    @FXML
    private void handleSaveRegistration() {
        String name = nameField.getText();
        String prenume = prenumeField.getText();
        String ageText = ageField.getText();

        if (name.isEmpty() || ageText.isEmpty()) {
            showError("Toate campurile trebuie completate!");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageText);
            if (age <= 0 || age > 120) {
                showError("Varsta trebuie sa fie intre 1 și 120 ani!");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Varsta trebuie sa fie un numar!");
            return;
        }

        List<Proba> selectedProbes = new ArrayList<>(probeSelectionTableView.getSelectionModel().getSelectedItems());

        if (selectedProbes.isEmpty()) {
            showError("Trebuie sa selectati cel putin o proba!");
            return;
        }

        try {
            Participant participant = new Participant(null, name, prenume, String.valueOf(age));
            participant = service.getParticipantService().saveParticipant(participant);

            for (Proba proba : selectedProbes) {
                Inscriere inscriere = new Inscriere(null, participant, proba);
                service.getInscriereService().saveInscriere(inscriere);


            }

            showSuccess();


            // Actualizeaza lista de probe în fereastra principala
            if (mainController != null) {
                mainController.refreshProbeList();
            }

            primaryStage.close();
        } catch (Exception e) {
            showError("Eroare la inregistrare: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorMessageLabel.setText(message);
        errorMessageLabel.setVisible(true);
    }

    private void showSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Inregistrare reusita");
        alert.setHeaderText(null);
        alert.setContentText("Participant inregistrat cu succes!");
        alert.showAndWait();
    }
}