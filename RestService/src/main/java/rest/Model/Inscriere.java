package rest.Model;

import jakarta.persistence.*;
import java.io.Serializable;

@jakarta.persistence.Entity
@Table(name = "Inscriere")
public class Inscriere extends Entity<Integer> implements Serializable {

    private Participant participant;
    private Proba proba;

    public Inscriere() {
    }

    public Inscriere(Integer id, Participant participant, Proba proba) {
        this.setId(id);
        this.participant = participant;
        this.proba = proba;
    }

    @ManyToOne
    @JoinColumn(name = "Participant", nullable = false)
    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    @ManyToOne
    @JoinColumn(name = "Proba", nullable = false)
    public Proba getProba() {
        return proba;
    }

    public void setProba(Proba proba) {
        this.proba = proba;
    }

    @Override
    public String toString() {
        return "Inscriere{id=" + this.getId() + ", Participant=" + this.participant + ", Proba=" + this.proba + "}";
    }
}
