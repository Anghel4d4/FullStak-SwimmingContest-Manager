package org.example.service;


import org.example.model.Inscriere;

public interface ISwimmingObserver {
    void notifyRegister(Inscriere inscriere) throws Exception;
}
