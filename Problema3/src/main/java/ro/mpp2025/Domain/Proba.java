package ro.mpp2025.Domain;

public class Proba extends Entity<Long>{
    private final String distanta;
    private final String stil;
    private int participantCount;

    public Proba(Long id, String distanta, String stil){
        this.setId(id);
        this.distanta = distanta;
        this.stil = stil;
    }

    public String getDistanta(){
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
