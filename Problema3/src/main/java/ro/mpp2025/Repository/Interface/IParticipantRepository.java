package ro.mpp2025.Repository.Interface;

import ro.mpp2025.Domain.Participant;

import java.util.List;

public interface IParticipantRepository extends Repository<Long, Participant> {
    int getEventCount(Long participantId);
    List<Participant> findByProba(Long probaId, String columnName);}