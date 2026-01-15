package org.example.service;

import org.example.model.Inscriere;
import org.example.model.Participant;
import org.example.model.Proba;
import org.example.model.User;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ISwimmingServices {

    CompletableFuture<User> login(User user, ISwimmingObserver client);

    CompletableFuture<Void> logout(User user, ISwimmingObserver client);

    CompletableFuture<List<Proba>> getAllProbes();

    CompletableFuture<Void> addInscriere(Inscriere inscriere, ISwimmingObserver client);

    CompletableFuture<List<Participant>> findParticipantsByProba(int distanta, String stil);

    CompletableFuture<Participant> saveParticipant(Participant participant, ISwimmingObserver observer);

    CompletableFuture<Optional<Participant>> findNameAndPrenume(String nume, String prenume);
}