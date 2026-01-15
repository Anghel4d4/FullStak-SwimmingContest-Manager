package main.swimmingclient.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import main.swimmingclient.ClientObserver;
import main.swimmingmodel.Inscriere;
import main.swimmingmodel.Participant;
import main.swimmingmodel.Proba;
import main.swimmingmodel.dto.ProbaDTO;
import main.swimmingservices.ISwimmingServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private TableView<ProbaDTO> probeSelectionTableView;
    @FXML
    private TableColumn<ProbaDTO, String> distanceSelectColumn;
    @FXML
    private TableColumn<ProbaDTO, String> styleSelectColumn;
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
        try {
            List<ProbaDTO> probe = services.getAllProbes();
            probeSelectionTableView.getItems().setAll(probe);
            logger.info("Loaded {} probe for selection", probe.size());
        } catch (Exception e) {
            logger.error("Error loading probe", e);
            showError("Error loading probe: " + e.getMessage());
        }
    }

    // Java
    @FXML
    private void handleSaveRegistration() {
        if (!validateInputs()) {
            return;
        }
        try {
            String name = nameField.getText().trim();
            String prenume = prenumeField.getText().trim();
            String age = ageField.getText().trim();
            List<ProbaDTO> selectedProbe = new ArrayList<>(probeSelectionTableView.getSelectionModel().getSelectedItems());

            Participant participant;
            // this is a check for duplicates
            Optional<Participant> existingOpt = services.findNameAndPrenume(name, prenume);
            if (existingOpt.isPresent()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                        "Participant already exists. Press OK to add as a new participant or Cancel to only register the inscription and update the count.");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {

                    //it creates a new participant if ok
                    participant = new Participant(null, name, prenume, age);
                    services.saveParticipant(participant, observer);
                } else {

                    //else is using the existing one
                    participant = existingOpt.get();
                }
            } else {
                participant = new Participant(null, name, prenume, age);
                services.saveParticipant(participant, observer);
            }

            // create registrations
            // the participant count will be updated automatically
            for (ProbaDTO probaDTO : selectedProbe) {
                Inscriere inscriere = new Inscriere(null, participant, probaDTO.getProba());
                services.addInscriere(inscriere, observer);
                logger.info("Created registration for {} {} in probe {} {}",
                        name, prenume, probaDTO.getDistanta(), probaDTO.getStil());
            }
            showSuccess();
            dialogStage.close();

        } catch (Exception e) {
            logger.error("Error saving registration", e);
            showError("Registration error: " + e.getMessage());
        }
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

        List<ProbaDTO> selectedProbe = probeSelectionTableView.getSelectionModel().getSelectedItems();
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