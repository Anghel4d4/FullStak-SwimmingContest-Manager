package ro.mpp2025.Service;

import ro.mpp2025.Domain.Inscriere;
import ro.mpp2025.Repository.Interface.IInscriereRepository;


public class InscriereService {
    private final IInscriereRepository inscriereRepository;

    public InscriereService(IInscriereRepository inscriereRepository) {
        this.inscriereRepository = inscriereRepository;
    }

    public void saveInscriere(Inscriere inscriere) {
        inscriereRepository.save(inscriere);
    }
}