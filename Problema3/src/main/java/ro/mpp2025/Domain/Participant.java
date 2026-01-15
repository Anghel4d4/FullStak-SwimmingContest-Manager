package ro.mpp2025.Domain;

public class Participant extends Entity<Long> {
    private final String nume;
    private final String prenume;
    private final String varsta;
    private int eventCount;

    public Participant(Long id, String nume, String prenume, String varsta) {
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

    public String getVarsta(){
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


