package ro.mpp2025.Service;

import ro.mpp2025.Repository.InscriereRepository;
import ro.mpp2025.Repository.ParticipantRepository;
import ro.mpp2025.Repository.ProbaRepository;
import ro.mpp2025.Repository.UserRepository;

public class Service {
    private final ParticipantService participantService;
    private final ProbaService probaService;
    private final InscriereService inscriereService;
    private final UserService userService;

    public Service(
            ParticipantRepository participantRepository,
            ProbaRepository probaRepository,
            InscriereRepository inscriereRepository,
            UserRepository userRepository
    ) {
        this.participantService = new ParticipantService(participantRepository);
        this.probaService = new ProbaService(probaRepository);
        this.inscriereService = new InscriereService(inscriereRepository);
        this.userService = new UserService(userRepository);
    }

    public ParticipantService getParticipantService() {
        return participantService;
    }

    public ProbaService getProbaService() {
        return probaService;
    }

    public InscriereService getInscriereService() {
        return inscriereService;
    }

    public UserService getUserService() {
        return userService;
    }
}