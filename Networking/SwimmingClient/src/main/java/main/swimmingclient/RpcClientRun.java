package main.swimmingclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.swimmingclient.Controllers.LoginController;
import main.swimmingnetworking.SwimmingServerRpcProxy;
import main.swimmingservices.ISwimmingServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class RpcClientRun extends Application {
    private static final Logger logger = LogManager.getLogger(RpcClientRun.class);


    public static void main(String[] args) {
        logger.info("Starting Swimming Client UI ...");
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) {
        try {

            Properties properties = new Properties();
            properties.load(RpcClientRun.class.getClassLoader().getResourceAsStream("client.properties"));
            logger.info("Loaded client.properties");

            String host = properties.getProperty("swimming.server.host");
            int port = Integer.parseInt(properties.getProperty("swimming.server.port"));

            logger.info("Connecting to server at {}:{}", host, port);
            ISwimmingServices service = new SwimmingServerRpcProxy(host, port);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
            LoginController controller = new LoginController(service);
            loader.setController(controller);

            primaryStage.setTitle("Swimming System");
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.show();
            logger.info("Login window loaded successfully");
        } catch (IOException e) {
            logger.error("Error starting client application", e);
            System.err.println("Error starting application: " + e.getMessage());
        }
    }

}