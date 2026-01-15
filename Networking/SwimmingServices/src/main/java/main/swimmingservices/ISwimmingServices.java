package main.swimmingservices;

import main.swimmingmodel.Inscriere;
import main.swimmingmodel.Participant;
import main.swimmingmodel.Proba;
import main.swimmingmodel.User;
import main.swimmingmodel.dto.ParticipantDTO;
import main.swimmingmodel.dto.ProbaDTO;

import java.util.List;
import java.util.Optional;

public interface ISwimmingServices {

    User login(User user, ISwimmingObserver client) throws Exception;

    void logout(User user, ISwimmingObserver client) throws Exception;

    List<ProbaDTO> getAllProbes() throws Exception;

    void addInscriere(Inscriere inscriere, ISwimmingObserver client) throws Exception;

    List<ParticipantDTO> findParticipantsByProba(String distanta, String stil) throws Exception;

    Participant saveParticipant(Participant participant, ISwimmingObserver observer) throws Exception;

    Optional<Participant> findNameAndPrenume(String nume, String prenume) throws Exception;

}
