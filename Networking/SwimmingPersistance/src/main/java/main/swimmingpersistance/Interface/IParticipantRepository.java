package main.swimmingpersistance.Interface;


import main.swimmingmodel.Participant;
import main.swimmingmodel.dto.ParticipantDTO;
import main.swimmingmodel.dto.ProbaDTO;

import java.util.List;
import java.util.Optional;

public interface IParticipantRepository extends Repository<Integer, Participant> {
    int getEventCount(Integer participantId);
    List<Participant> findByProba(Integer probaId, String columnName);
    Optional<Participant> findNameAndPrenume(String nume, String prenume);
    List<ParticipantDTO> findAllWithEventCount();

}
