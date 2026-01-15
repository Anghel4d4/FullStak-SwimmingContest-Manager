package main.swimmingnetworking;
import main.swimmingservices.ISwimmingServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;
public class ConcurrentServer extends AbstractConcurrentServer {
    private final ISwimmingServices server;
    private static final Logger logger = LogManager.getLogger(ConcurrentServer.class);

    public ConcurrentServer(int port, ISwimmingServices server) {
        super(port);
        this.server = server;
        logger.info("Swimming ConcurrentServer initialized on port {}", port);
    }

    @Override
    protected Thread createWorker(Socket client) {
        logger.info("Creating worker thread for client {}", client.getRemoteSocketAddress());

        SwimmingClientRpcWorker worker = new SwimmingClientRpcWorker(server, client);
        Thread thread = new Thread(worker);
        thread.setName("Worker-" + client.getPort());

        logger.debug("Worker thread [{}] created for {}", thread.getName(), client.getRemoteSocketAddress());
        return thread;
    }

    @Override
    public void stop() {
        logger.info("Swimming server stopping...");
    }
}
