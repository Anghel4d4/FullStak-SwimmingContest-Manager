package ro.mpp2025.Service;

import ro.mpp2025.Domain.Proba;
import ro.mpp2025.Repository.Interface.IProbaRepository;

import java.util.List;

public class ProbaService {
    private final IProbaRepository probaRepository;

    public ProbaService(IProbaRepository probaRepository) {
        this.probaRepository = probaRepository;
    }

    public List<Proba> getAllProbes() {
        return (List<Proba>) probaRepository.findAll();
    }

    public List<String> getDistinctDistances() {
        return probaRepository.findDistinctDistances();
    }

    public List<String> getDistinctStyles() {
        return probaRepository.findDistinctStyles();
    }

    public Proba findByDistanceAndStyle(String distance, String style) {
        return probaRepository.findByDistanceAndStyle(distance, style);
    }

    public int getParticipantCount(Long probaId) {
        return probaRepository.getParticipantCount(probaId);
    }

    public void updateProba(Proba proba) {
        probaRepository.update(proba);
    }
}