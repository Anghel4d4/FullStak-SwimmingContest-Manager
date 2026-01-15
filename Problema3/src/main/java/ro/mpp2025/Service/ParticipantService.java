package ro.mpp2025.Service;

import ro.mpp2025.Domain.Participant;
import ro.mpp2025.Domain.Proba;
import ro.mpp2025.Repository.Interface.IParticipantRepository;

import java.util.List;

public class ParticipantService {
    private final IParticipantRepository participantRepository;

    public ParticipantService(IParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }


    public Participant saveParticipant(Participant participant) {
        participantRepository.save(participant);
        return participant;
    }

    public List<Participant> findByProba(Proba proba, String columnName) {
        return participantRepository.findByProba(proba.getId(), columnName);
    }

    public int getEventCount(Long participantId) {
        return participantRepository.getEventCount(participantId);
    }
}