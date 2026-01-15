package ro.mpp2025.Controller;

import Utils.JdbcUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ro.mpp2025.Domain.User;
import ro.mpp2025.Repository.ProbaRepository;
import ro.mpp2025.Repository.ParticipantRepository;
import ro.mpp2025.Repository.InscriereRepository;
import ro.mpp2025.Repository.UserRepository;
import ro.mpp2025.Service.Service;
import ro.mpp2025.Service.UserService;

import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    private UserService userService;
    private Stage primaryStage;

    public void initialize() {

    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Completati toate campurile!");
            return;
        }

        Optional<User> user = userService.authenticate(username, password);
        if (user.isPresent()) {
            openMainWindow();
        } else {
            showError("Credentiale incorecte!");
        }
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Eroare autentificare");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
    }

    private void openMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main-view.fxml"));
            Parent root = loader.load();

            MainController controller = loader.getController();

            Properties props = new Properties();
            try (FileReader reader = new FileReader("bd.config")) {
                props.load(reader);
            }

            JdbcUtils jdbcUtils = new JdbcUtils(props);
            ProbaRepository probaRepository = new ProbaRepository(jdbcUtils);
            ParticipantRepository participantRepository = new ParticipantRepository(jdbcUtils);
            InscriereRepository inscriereRepository = new InscriereRepository(jdbcUtils, participantRepository, probaRepository);
            UserRepository userRepository = new UserRepository(jdbcUtils);

            Service service = new Service(participantRepository, probaRepository, inscriereRepository, userRepository);

            controller.setServices(service);
            controller.setPrimaryStage(primaryStage);

            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Sistem concurs inot");
            primaryStage.setMaximized(true);
            primaryStage.show();

        } catch (IOException e) {
            showError("Eroare la deschiderea aplicatiei: " + e.getMessage());
        }
    }
}