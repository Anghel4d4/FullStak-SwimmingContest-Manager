package main.swimmingserver.Services;


import main.swimmingmodel.Participant;
import main.swimmingmodel.Proba;
import main.swimmingmodel.dto.ParticipantDTO;
import main.swimmingpersistance.Interface.IParticipantRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ParticipantService {
    private final IParticipantRepository participantRepository;

    public ParticipantService(IParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }


    public Participant saveParticipant(Participant participant) {
        participantRepository.save(participant);
        return participant;
    }

    public List<ParticipantDTO> findByProba(Proba proba, String columnName) {
        return participantRepository.findByProba(proba.getId(), columnName)
                .stream()
                .map(p -> new ParticipantDTO(p, participantRepository.getEventCount(p.getId())))
                .collect(Collectors.toList());
    }

    public Optional<Participant> findByNameAndPrenume(String nume, String prenume) {
        return participantRepository.findNameAndPrenume(nume, prenume);
    }

}