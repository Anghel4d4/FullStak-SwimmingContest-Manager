package main.swimmingmodel;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@jakarta.persistence.Entity
@Table(name = "Proba")
public class Proba extends Entity<Integer> implements Serializable {

    @Column(name = "distanta", nullable = false)
    private String distanta;

    @Column(name = "stil", nullable = false)
    private String stil;

    public Proba() {
    }

    public Proba(Integer id, String distanta, String stil) {
        this.setId(id);
        this.distanta = distanta;
        this.stil = stil;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Override
    public Integer getId() {
        return super.getId();
    }

    @Override
    public void setId(Integer id) {
        super.setId(id);
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
