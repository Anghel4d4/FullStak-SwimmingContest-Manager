package main.swimmingnetworking;

import main.swimmingmodel.*;
import main.swimmingmodel.dto.ParticipantDTO;
import main.swimmingmodel.dto.ProbaDTO;
import main.swimmingmodel.dto.ProbaSearchDTO;
import main.swimmingservices.ISwimmingObserver;
import main.swimmingservices.ISwimmingServices;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.io.IOException;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("BusyWait")
public class SwimmingClientRpcWorker implements Runnable, ISwimmingObserver {
    private static final Logger logger = LogManager.getLogger(SwimmingClientRpcWorker.class);

    private final ISwimmingServices server;
    private final Socket connection;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    public SwimmingClientRpcWorker(ISwimmingServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
            logger.info("Worker initialized for client {}", connection.getRemoteSocketAddress());
        } catch (IOException e) {
            logger.error("Error creating worker {}",e.getMessage(), e);
        }
    }

    @Override
    public void run() {
        logger.info("Worker started for {}", connection.getRemoteSocketAddress());
        while (connected) {
            try {
                Object req = input.readObject();
                if (req instanceof Request request) {
                    logger.info("Received request: {}", request.type());
                    Response response = handleRequest(request);
                    sendResponse(response);
                }
            } catch (java.io.EOFException eof) {
                logger.warn("Client disconnected: {}", connection.getRemoteSocketAddress());
                connected = false;
                closeConnection();
            } catch (Exception e) {
                logger.error("Error processing request: {}", e.getMessage(), e);
                connected = false;
                closeConnection();
            }
        }
        logger.info("Worker stopped for {}", connection.getRemoteSocketAddress());
    }

    protected void sendResponse(Response response) throws IOException {
        synchronized (output) {
            logger.debug("Sent response: {}", response.type());
            output.writeObject(response);
            output.flush();
        }
    }

    private Response handleRequest(Request request) {
        try{
            switch(request.type()){
                case LOGIN -> {
                    User user = (User) request.data();
                    User result = server.login(user, this);
                    logger.info("User {} logged in successfully", user.getUsername());
                    return new Response(ResponseType.OK, result);
                }
                case LOGOUT -> {
                    User user = (User) request.data();
                    server.logout(user, this);
                    connected = false;
                    logger.info("User {} logged out successfully", user.getUsername());
                    return new Response(ResponseType.OK, null);
                }
                case GET_PROBES -> {
                    List<ProbaDTO> probes = server.getAllProbes();
                    logger.info("Retrieved all probes");
                    return new Response(ResponseType.OK, probes);
                }
                case REGISTER_PARTICIPANT -> {
                    Inscriere inscriere = (Inscriere) request.data();
                    server.addInscriere(inscriere, this);
                    logger.info("Participant {} registered successfully", inscriere.getParticipant().getNume());
                    return new Response(ResponseType.OK, null);
                }
                case FIND_PARTICIPANTS_BY_PROBA -> {
                    ProbaSearchDTO searchDTO = (ProbaSearchDTO) request.data();
                    List<ParticipantDTO> participants = server.findParticipantsByProba(
                            searchDTO.getDistanta(),
                            searchDTO.getStil()
                    );
                    return new Response(ResponseType.OK, participants);
                }
                case SAVE_PARTICIPANT -> {
                    Participant participant = (Participant) request.data();
                    Participant savedParticipant = server.saveParticipant(participant, this);
                    return new Response(ResponseType.OK, savedParticipant);
                }
                case FIND_NAME_AND_PRENUME -> {
                    try {
                        String[] data = (String[]) request.data();
                        String nume = data[0];
                        String prenume = data[1];
                        Optional<Participant> participantOpt = server.findNameAndPrenume(nume, prenume);
                        Participant participant = participantOpt.orElse(null);
                        return new Response(ResponseType.OK, participant);
                    } catch (Exception e) {
                        logger.error("Error processing findNameAndPrenume: {}", e.getMessage(), e);
                        return new Response(ResponseType.ERROR, "Error processing findNameAndPrenume: " + e.getMessage());
                    }
                }
                default -> {
                    logger.warn("Unknown request type received: {}", request.type());
                    return new Response(ResponseType.ERROR, "Unknown request type: " + request.type());
                }
            }

        }catch (Exception e){
            logger.error("Error handling request {}: {}",request.type(), e.getMessage(), e);
            return new Response(ResponseType.ERROR, e.getMessage());
        }
    }

    public void closeConnection(){
        try{
            if(input != null) input.close();
            if(output != null) output.close();
            if(connection != null && !connection.isClosed()) connection.close();
            logger.info("Connection closed for {}", connection.getRemoteSocketAddress());

        }catch(IOException e){
            logger.error("Error closing connection: {}", e.getMessage(), e);
        }
    }

    @Override
    public void notifyRegister(Inscriere inscriere) throws Exception {
        logger.info("Broadcasting registration notification to all clients {}", inscriere.getParticipant().getNume());
        Response resp = new Response(ResponseType.REGISTER_NOTIFICATION, inscriere);
        sendResponse(resp);
    }
}