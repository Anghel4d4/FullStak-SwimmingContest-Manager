package ro.mpp2025.Repository.Interface;

import ro.mpp2025.Domain.Proba;

import java.util.List;

public interface IProbaRepository extends Repository<Long, Proba> {
    List<String> findDistinctDistances();
    List<String> findDistinctStyles();
    Proba findByDistanceAndStyle(String distance, String style);
    int getParticipantCount(Long probaId);
}