package org.example.model;

import java.io.Serializable;

public class ProbaSearchDTO implements Serializable {
    private final int distanta;
    private final String stil;

    public ProbaSearchDTO(int distanta, String stil){
        this.distanta = distanta;
        this.stil = stil;
    }

    public int getDistanta(){
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
