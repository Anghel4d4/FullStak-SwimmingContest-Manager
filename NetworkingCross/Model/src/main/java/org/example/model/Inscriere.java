package org.example.model;


import java.io.Serializable;

public class Inscriere extends Entity<Long> implements Serializable {

    private Participant participant;
    private Proba proba;

    public Inscriere(Long id, Participant participant, Proba proba){
        this.setId(id);
        this.participant = participant;
        this.proba=proba;
    }

    public Participant getParticipant(){
        return participant;
    }

    public void setParticipant(Participant participant){
        this.participant = participant;
    }

    public Proba getProba()
    {
        return proba;
    }

    public void setProba(Proba proba){
        this.proba = proba;
    }

    public String toString(){
        return "Inscriere{id=" + this.getId() + ", Participant=" + this.participant + ", Proba=" + this.proba + "}";
    }

}
