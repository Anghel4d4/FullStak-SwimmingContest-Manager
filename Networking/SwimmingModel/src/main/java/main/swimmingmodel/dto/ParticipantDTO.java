// ParticipantDTO.java
package main.swimmingmodel.dto;

import main.swimmingmodel.Participant;

import java.io.Serializable;

public class ParticipantDTO implements Serializable {
    private final Integer id;
    private final String nume;
    private final String prenume;
    private final String varsta;
    private final int eventCount;

    public ParticipantDTO(Participant participant, int eventCount) {
        this.id = participant.getId();
        this.nume = participant.getNume();
        this.prenume = participant.getPrenume();
        this.varsta = participant.getVarsta();
        this.eventCount = eventCount;
    }

    public Integer getId() {
        return id;
    }

    public String getNume() {
        return nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public String getVarsta() {
        return varsta;
    }

    public int getEventCount() {
        return eventCount;
    }
}
