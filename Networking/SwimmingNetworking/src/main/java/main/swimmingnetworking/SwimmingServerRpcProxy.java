package main.swimmingnetworking;

import main.swimmingmodel.*;
import main.swimmingmodel.dto.ParticipantDTO;
import main.swimmingmodel.dto.ProbaDTO;
import main.swimmingmodel.dto.ProbaSearchDTO;
import main.swimmingservices.ISwimmingObserver;
import main.swimmingservices.ISwimmingServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@SuppressWarnings("unchecked")
public class SwimmingServerRpcProxy implements ISwimmingServices {

    private static final Logger logger = LogManager.getLogger(SwimmingServerRpcProxy.class);

    private final String host;
    private final int port;
    private ISwimmingObserver client;

    private Socket connection;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private final BlockingQueue<Response> qResponse = new LinkedBlockingQueue<>();
    private volatile boolean finished;

    public SwimmingServerRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private void initializeConnection() {
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            logger.info("Connected to server {}:{}", host, port);
            startReader();
        } catch (Exception e) {
            throw new RuntimeException("Error initializing connection", e);
        }
    }

    private void startReader() {
        Thread readerThread = new Thread(new ReaderThread());
        readerThread.setName("Proxy-ReaderThread");
        readerThread.start();
        logger.info("Reader thread started: {}", readerThread.getName());
    }


    private void sendRequest(Request request) throws Exception {
        if (request != null) {
            output.writeObject(request);
            output.flush();
            logger.debug("Request sent: {}", request.type());
        }
    }


    private Response readResponse() throws Exception {
        logger.debug("Waiting for response...");
        Response response = qResponse.take();
        logger.debug("Response received: {}", response.type());
        return response;
    }


    private boolean isUpdate(Response response) {
        return response.type() == ResponseType.REGISTER_NOTIFICATION;
    }


    private void handleUpdate(Response response) {
        try {
            if (Objects.requireNonNull(response.type()) == ResponseType.REGISTER_NOTIFICATION) {
                Inscriere inscriere = (Inscriere) response.data();
                logger.info("There is an update: inscriere registered for participant {}",
                        inscriere.getParticipant().getId());
                client.notifyRegister(inscriere);
            } else {
                logger.warn("Unknown update received: {}", response.type());
            }
        } catch (Exception e) {
            logger.error("Error handling update: {}", e.getMessage(), e);
        }
    }




    @Override
    public User login(User user, ISwimmingObserver  client) throws Exception {
        this.client = client;
        initializeConnection();

        sendRequest(new Request(RequestType.LOGIN, user));
        Response response = readResponse();

        if (response.type() == ResponseType.OK) {
            logger.info("Login successful for {}", user.getUsername());
            return (User) response.data();
        } else {
            closeConnection();
            throw new Exception((String) response.data());
        }
    }



    @Override
    public void logout(User user, ISwimmingObserver client) throws Exception {
        sendRequest(new Request(RequestType.LOGOUT, user));
        Response response = readResponse();
        closeConnection();

        if (response.type() != ResponseType.OK) {
            throw new Exception((String) response.data());
        }

    }

    @Override
    public List<ProbaDTO> getAllProbes() throws Exception {
        sendRequest(new Request(RequestType.GET_PROBES, null));
        Response response = readResponse();

        if (response.type() == ResponseType.OK) {
            return (List<ProbaDTO>) response.data();
        } else {
            throw new Exception((String) response.data());
        }
    }

    @Override
    public void addInscriere(Inscriere inscriere, ISwimmingObserver client) throws Exception {
        sendRequest(new Request(RequestType.REGISTER_PARTICIPANT, inscriere));
        Response response = readResponse();
        if (response.type() != ResponseType.OK) {
            throw new Exception((String) response.data());
        }
    }

    @Override
    public Participant saveParticipant(Participant participant, ISwimmingObserver observer) throws Exception {
        sendRequest(new Request(RequestType.SAVE_PARTICIPANT, participant));
        Response response = readResponse();

        if (response.type() == ResponseType.OK) {
            return (Participant) response.data();
        } else {
            throw new Exception((String) response.data());
        }
    }


    @Override
    public List<ParticipantDTO> findParticipantsByProba(String distanta, String stil) throws Exception {
        logger.info("Searching for participants in proba with distance: {} and style: {}", distanta, stil);

        try {
            ProbaSearchDTO searchDTO = new ProbaSearchDTO(distanta, stil);
            Request request = new Request(RequestType.FIND_PARTICIPANTS_BY_PROBA, searchDTO);

            sendRequest(request);

            Response response = readResponse();
            if (response.type() == ResponseType.OK) {
                return (List<ParticipantDTO>) response.data();
            } else {
                throw new Exception(response.data().toString());
            }
        } catch (Exception e) {
            logger.error("Error searching for participants", e);
            throw new Exception("Search failed: " + e.getMessage());
        }
    }


    @Override
    public Optional<Participant> findNameAndPrenume(String nume, String prenume) throws Exception {
        logger.info("Searching for participant with nume: {} and prenume: {}", nume, prenume);


        String[] data = new String[]{nume, prenume};
        Request request = new Request(RequestType.FIND_NAME_AND_PRENUME, data);
        sendRequest(request);
        Response response = readResponse();
        if(response.type() == ResponseType.OK) {
            if(response.data() == null) {
                return Optional.empty();
            }
            return Optional.of((Participant) response.data());
        } else {
            throw new Exception((String) response.data());
        }
    }






    public void closeConnection() {
        finished = true;
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (connection != null && !connection.isClosed()) connection.close();
            logger.info("The connection was closed");
        } catch (Exception e) {
            logger.error("An error occurred while closing the connection: {}", e.getMessage(), e);
        }
    }



    private class ReaderThread implements Runnable {
        @Override
        public void run() {
            while (!finished) {
                try {
                    Object obj = input.readObject();
                    if (obj instanceof Response response) {
                        if (isUpdate(response)) {
                            handleUpdate(response);
                        } else {
                            qResponse.put(response);
                        }
                    }
                } catch (Exception e) {
                    logger.error("ReaderThread error: {}", e.getMessage());
                    finished = true;
                    closeConnection();
                }
            }
        }
    }
}