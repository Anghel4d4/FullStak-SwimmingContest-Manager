package ro.mpp2025;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ro.mpp2025.Controller.LoginController;
import ro.mpp2025.Service.UserService;
import ro.mpp2025.Repository.UserRepository;
import Utils.JdbcUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Main extends Application {
    private static final Logger logger = LogManager.getLogger(Main.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        Properties props = new Properties();
        try {
            props.load(new FileReader("bd.config"));
        } catch (IOException e) {
            logger.error("Cannot find bd.config", e);
            return;
        }

        JdbcUtils jdbcUtils = new JdbcUtils(props);
        UserRepository userRepository = new UserRepository(jdbcUtils);
        UserService userService = new UserService(userRepository);


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
        Scene scene = new Scene(loader.load());

        LoginController controller = loader.getController();
        controller.setUserService(userService);
        controller.setPrimaryStage(primaryStage);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");
        primaryStage.show();
        logger.info("Application started successfully");
    }

    public static void main(String[] args) {
        launch(args);
        logger.info("Application launched");
    }
}