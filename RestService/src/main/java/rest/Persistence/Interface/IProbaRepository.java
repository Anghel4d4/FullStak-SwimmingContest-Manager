package rest.Persistence.Interface;

import org.springframework.data.jpa.repository.JpaRepository;
import rest.Model.Proba;

public interface IProbaRepository extends JpaRepository<Proba, Integer> {
    Proba findByDistantaAndStil(String distanta, String stil);
}