package main.swimmingnetworking;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class AbstractServer {
    private final int port;
    private ServerSocket server = null;
    private static final Logger logger = LogManager.getLogger(AbstractServer.class);

    public AbstractServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        try {
            server = new ServerSocket(port);
            logger.info("Server started on port {}", port);

            while (true) {
                logger.info("Waiting for clients...");
                Socket client = server.accept();
                logger.info("Client connected from {}", client.getRemoteSocketAddress());
                processRequest(client);
            }

        } catch (IOException e) {
            logger.error("Error starting server: {}", e.getMessage(), e);
            throw new Exception("Error starting server on port " + port, e);
        } finally {
            try {
                stop();
            } catch (Exception e) {
                logger.error("Error during server shutdown: {}", e.getMessage(), e);
            }
        }
    }

    public void stop() throws Exception {
        if (server != null && !server.isClosed()) {
            try {
                logger.info("Stopping server on port {}...", port);
                server.close();
                logger.info("Server successfully stopped.");
            } catch (IOException e) {
                logger.error("Error closing server socket: {}", e.getMessage(), e);
                throw new Exception("Error closing server", e);
            }
        }
    }

    protected abstract void processRequest(Socket client);
}