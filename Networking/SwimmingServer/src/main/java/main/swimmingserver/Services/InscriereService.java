package main.swimmingserver.Services;


import main.swimmingmodel.Inscriere;
import main.swimmingpersistance.Interface.IInscriereRepository;

public class InscriereService {
    private final IInscriereRepository inscriereRepository;

    public InscriereService(IInscriereRepository inscriereRepository) {
        this.inscriereRepository = inscriereRepository;
    }

    public void saveInscriere(Inscriere inscriere) {
        inscriereRepository.save(inscriere);
    }
}