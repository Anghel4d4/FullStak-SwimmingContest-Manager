package rest.Model;

import java.io.Serializable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;


@jakarta.persistence.Entity
@Table(name = "Proba")
public class Proba extends Entity<Integer> implements Serializable {


    @NotBlank
    @Column(name = "distanta", nullable = false)
    private String distanta;

    @NotBlank
    @Column(name = "stil", nullable = false)
    private String stil;

    public Proba() {
    }

    public Proba(Integer id, String distanta, String stil) {
        this.setId(id);
        this.distanta = distanta;
        this.stil = stil;
    }

    public String getDistanta() {
        return distanta;
    }

    public void setDistanta(String distanta) {
        this.distanta = distanta;
    }

    public String getStil() {
        return stil;
    }

    public void setStil(String stil) {
        this.stil = stil;
    }


    @Override
    public String toString() {
        return "Proba{id=" + this.getId() + ", distanta=" + this.distanta + ", stil=" + this.stil + "}";
    }
}
