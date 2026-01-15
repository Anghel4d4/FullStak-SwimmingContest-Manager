package main.swimmingserver.Services;


import main.swimmingmodel.*;
import main.swimmingmodel.dto.ParticipantDTO;
import main.swimmingmodel.dto.ProbaDTO;
import main.swimmingservices.ISwimmingObserver;
import main.swimmingservices.ISwimmingServices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Service implements ISwimmingServices {

    private static final Logger logger = LogManager.getLogger(Service.class);
    private final ParticipantService participantService;
    private final ProbaService probaService;
    private final InscriereService inscriereService;
    private final UserService userService;

    private final Map<String, ISwimmingObserver> loggedClients = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public Service(UserService userService, ProbaService probaService,
                   ParticipantService participantService, InscriereService inscriereService) {
        this.userService = userService;
        this.probaService = probaService;
        this.participantService = participantService;
        this.inscriereService = inscriereService;
    }

    @Override
    public synchronized User login(User user, ISwimmingObserver client) throws Exception {
        if (user == null || client == null) {
            logger.warn("Null parameter in login method.");
            throw new IllegalArgumentException("User and client must not be null.");
        }
        logger.info("Attempting login for user: {}", user.getUsername());
        Optional<User> authUser = userService.authenticate(user.getUsername(), user.getPassword());
        if (authUser.isPresent()) {
            loggedClients.put(authUser.get().getUsername(), client);
            logger.info("User {} logged in successfully.", authUser.get().getUsername());
            return authUser.get();
        } else {
            logger.warn("Authentication failed for username: {}", user.getUsername());
            throw new Exception("Authentication failed.");
        }
    }

    @Override
    public synchronized void logout(User user, ISwimmingObserver client) throws Exception {
        if (user == null || client == null) {
            logger.warn("Null parameter in logout method.");
            throw new IllegalArgumentException("User and client must not be null.");
        }
        if (loggedClients.remove(user.getUsername(), client)) {
            logger.info("User {} logged out successfully.", user.getUsername());
        } else {
            logger.warn("User {} was not logged in.", user.getUsername());
            throw new Exception("User not logged in.");
        }
    }

    @Override
    public synchronized List<ProbaDTO> getAllProbes() throws Exception {
        List<ProbaDTO> probes = probaService.getAllProbes();
        logger.info("Retrieved {} probes.", probes.size());
        return probes;
    }


    @Override
    public synchronized void addInscriere(Inscriere inscriere, ISwimmingObserver client) throws Exception {
        inscriereService.saveInscriere(inscriere);

        //i add this because i need to update the participant count when added
        Proba proba = inscriere.getProba();
        notifyRegister(inscriere);
    }


    @Override
    public synchronized List<ParticipantDTO> findParticipantsByProba(String distanta, String stil) throws Exception {
        logger.info("Finding participants for proba with distance: {} and style: {}", distanta, stil);

        try {
            Proba proba = probaService.findByDistanceAndStyle(distanta, stil);
            if (proba == null) {
                logger.warn("No proba found with distance: {} and style: {}", distanta, stil);
                return new ArrayList<>();
            }

            return participantService.findByProba(proba, "Id");
        } catch (Exception e) {
            logger.error("Error finding participants for proba", e);
            throw new Exception("Failed to find participants: " + e.getMessage());
        }
    }

    private void notifyRegister(Inscriere inscriere) {
        logger.info("Notifying clients about new registration: {}", inscriere.getParticipant().getNume());
        for (ISwimmingObserver client : loggedClients.values()) {
            executor.submit(() -> {
                try {
                    client.notifyRegister(inscriere);
                } catch (Exception e) {
                    logger.error("Error notifying client about registration: {}", e.getMessage());
                }
            });
        }
    }


    public void shutdown() {
        logger.info("Shutting down executor service...");
        executor.shutdownNow();
    }

    @Override
    public synchronized Participant saveParticipant(Participant participant, ISwimmingObserver observer) throws Exception {
        logger.info("Saving participant: {}", participant.getNume());
        return participantService.saveParticipant(participant);
    }

    @Override
    public synchronized Optional<Participant> findNameAndPrenume(String nume, String prenume) throws Exception {
        return participantService.findByNameAndPrenume(nume, prenume);
    }

}