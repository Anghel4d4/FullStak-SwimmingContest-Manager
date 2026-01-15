package org.example.model;


import java.io.Serializable;

public class Proba extends Entity<Long> implements Serializable {

    private final int distanta;
    private final String stil;
    private int participantCount;

    public Proba(Long id, int distanta, String stil){
        this.setId(id);
        this.distanta = distanta;
        this.stil = stil;
    }

    public int getDistanta(){
        return distanta;
    }

    public String getStil(){
        return stil;
    }

    public int getParticipantCount(){
        return participantCount;
    }

    public void setParticipantCount(int participantCount){
        this.participantCount = participantCount;
    }

    public String toString(){
        return "Proba{id=" + this.getId() + ", distanta=" + this.distanta + ", stil=" + this.stil + "}";
    }
}
