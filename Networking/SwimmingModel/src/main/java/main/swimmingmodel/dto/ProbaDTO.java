package main.swimmingmodel.dto;

import main.swimmingmodel.Proba;

import java.io.Serializable;

public class ProbaDTO implements Serializable {
    private final Proba proba;
    private final int participantCount;

    public ProbaDTO(Proba proba, int participantCount) {
        this.proba = proba;
        this.participantCount = participantCount;
    }

    public Integer getId() {
        return proba.getId();
    }

    public String getDistanta() {
        return proba.getDistanta();
    }

    public String getStil() {
        return proba.getStil();
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public Proba getProba() {
        return proba;
    }
}
