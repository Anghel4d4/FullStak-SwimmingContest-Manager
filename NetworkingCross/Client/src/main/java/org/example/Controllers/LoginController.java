package org.example.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.ClientObserver;
import org.example.model.User;
import org.example.service.ISwimmingServices;

public class LoginController {
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private Button loginButton;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private final ISwimmingServices services;
    public LoginController(ISwimmingServices services) {
        this.services = services;
        logger.info("LoginController created with services and observer initialized");
    }

    @FXML
    public void initialize() {
        loginButton.setOnAction(event -> handleLogin());
        logger.info("LoginController initialized");
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        logger.info("Trying to log in with username: {}", username);

        User user = new User(username, password);
        ClientObserver observer = new ClientObserver();

        services.login(user, observer)
                .thenAccept(loggedUser -> Platform.runLater(() -> openMainView(loggedUser, observer)))
                .exceptionally(ex -> {
                    logger.error("Login failed: {}", ex.getMessage());
                    showAlert(Alert.AlertType.ERROR, "Error", "Authentication failed: " + ex.getMessage());
                    return null;
                });
    }

    private void openMainView(User user, ClientObserver observer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main-view.fxml"));
            var controller = new MainController(user,this.services, observer);
            observer.setMainController(controller);
            loader.setController(controller);

            Parent root = loader.load();

            Scene scene = new Scene(root);

            Stage mainStage = new Stage();
            mainStage.setTitle("Main window - " + user.getUsername());
            mainStage.setScene(scene);
            mainStage.show();


            Stage loginStage = (Stage) loginButton.getScene().getWindow();
            loginStage.close();

            logger.info("Main window opened for user: {}", user.getUsername());
        } catch (Exception e) {
            logger.error("Error opening main window", e);
            showAlert(Alert.AlertType.ERROR,"Error","Eroare la open principal: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}