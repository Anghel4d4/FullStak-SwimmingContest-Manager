package main.swimmingpersistance.Interface;


import main.swimmingmodel.Proba;
import main.swimmingmodel.dto.ProbaDTO;

import java.util.List;

public interface IProbaRepository extends Repository<Integer, Proba> {
    Proba findByDistanceAndStyle(String distance, String style);
    int getParticipantCount(Integer probaId);
    List<ProbaDTO> findAllWithParticipantCount();

}