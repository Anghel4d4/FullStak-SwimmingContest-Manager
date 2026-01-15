package rest.Model;

import java.io.Serializable;
import jakarta.persistence.*;

@jakarta.persistence.Entity
@Table(name = "Participant")
public class Participant extends Entity<Integer> implements Serializable {

    @Column(name = "Nume", nullable = false)
    private String nume;

    @Column(name = "Prenume", nullable = false)
    private String prenume;

    @Column(name = "Varsta", nullable = false)
    private String varsta;



    public Participant() {
    }

    public Participant(Integer id, String nume, String prenume, String varsta) {
        this.setId(id);
        this.nume = nume;
        this.prenume = prenume;
        this.varsta = varsta;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public String getVarsta() {
        return varsta;
    }

    public void setVarsta(String varsta) {
        this.varsta = varsta;
    }


    @Override
    public String toString() {
        return "Participant{id=" + this.getId() + ", nume=" + this.nume + ", prenume=" + this.prenume + ", varsta=" + this.varsta + "}";
    }
}
