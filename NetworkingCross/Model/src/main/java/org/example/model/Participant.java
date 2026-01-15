package org.example.model;

import java.io.Serializable;

public class Participant extends Entity<Long> implements Serializable {

    private final String nume;
    private final String prenume;
    private final int varsta;
    private int eventCount;

    public Participant(Long id, String nume, String prenume, int varsta) {
        this.setId(id);
        this.nume= nume;
        this.prenume = prenume;
        this.varsta = varsta;
    }

    public String getNume(){
        return nume;
    }

    public String getPrenume(){
        return prenume;
    }

    public int getVarsta(){
        return varsta;
    }


    public int getEventCount() {
        return eventCount;
    }

    public void setEventCount(int eventCount) {
        this.eventCount = eventCount;
    }
    public String toString(){
        return "Participant{id=" + this.getId() + ", nume=" + this.nume + ", prenume=" + this.prenume + ", varsta=" + this.varsta + "}";
    }
}


