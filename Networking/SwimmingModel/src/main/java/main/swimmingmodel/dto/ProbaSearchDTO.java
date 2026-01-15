package main.swimmingmodel.dto;

import java.io.Serializable;

public class ProbaSearchDTO implements Serializable {
    private final String distanta;
    private final String stil;

    public ProbaSearchDTO(String distanta, String stil){
        this.distanta = distanta;
        this.stil = stil;
    }

    public String getDistanta(){
        return distanta;
    }

    public String getStil(){
        return stil;
    }

    @Override
    public String toString() {
        return String.format("ProbaSearchDTO{distance='%s', style='%s'}", distanta, stil);
    }

}
