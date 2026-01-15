package main.swimmingnetworking;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;
public abstract class AbstractConcurrentServer extends AbstractServer {
    private static final Logger logger= LogManager.getLogger(AbstractConcurrentServer.class);

    public AbstractConcurrentServer(int port) {
        super(port);
        logger.debug("Concurrent AbstractServer initialized on port {}", port);
    }


    @Override
    protected void processRequest(Socket client){
        logger.info("Preparing to handle client at {}", client.getRemoteSocketAddress());
        try {
            Thread workerThread = createWorker(client);
            workerThread.start();
            logger.info("Started worker thread {} for client {}", workerThread.getName(), client.getRemoteSocketAddress());
        } catch (Exception e) {
            logger.error("Error starting worker thread for client {}: {}", client.getRemoteSocketAddress(), e.getMessage(), e);
        }
    }

    protected abstract Thread createWorker(Socket client);
}
