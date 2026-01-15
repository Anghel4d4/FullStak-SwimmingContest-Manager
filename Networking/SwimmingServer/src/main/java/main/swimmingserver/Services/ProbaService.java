package main.swimmingserver.Services;

import main.swimmingmodel.Proba;
import main.swimmingmodel.dto.ProbaDTO;
import main.swimmingpersistance.Interface.IProbaRepository;

import java.util.List;


public class ProbaService {
    private final IProbaRepository probaRepository;

    public ProbaService(IProbaRepository probaRepository) {
        this.probaRepository = probaRepository;
    }

    public List<ProbaDTO> getAllProbes() {
        return probaRepository.findAllWithParticipantCount();
    }


    public Proba findByDistanceAndStyle(String distance, String style) {
        return probaRepository.findByDistanceAndStyle(distance, style);
    }

    public int getParticipantCount(Integer probaId) {
        return probaRepository.getParticipantCount(probaId);
    }

//    public void updateProba(Proba proba) {
//        // opdate the inmemory participant count before doing further actions
//        proba.setParticipantCount(getParticipantCount(proba.getId()));
//    }
}