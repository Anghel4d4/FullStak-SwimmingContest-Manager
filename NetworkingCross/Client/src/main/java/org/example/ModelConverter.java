package org.example;

import org.example.grpc.SwimmingProto;
import org.example.model.Inscriere;
import org.example.model.Participant;
import org.example.model.Proba;
import org.example.model.User;

public class ModelConverter {

    public static SwimmingProto.User toGrpcUser(User user){
        return SwimmingProto.User.newBuilder()
                .setId(user.getId() != null ? user.getId() : 0)
                .setUsername(user.getUsername())
                .setPassword(user.getPassword())
                .build();
    }

    public static User fromGrpcUser(SwimmingProto.User grpcUser){
        User user = new User(grpcUser.getUsername(), grpcUser.getPassword());
        user.setId(grpcUser.getId() != 0 ? grpcUser.getId() : null);
        return user;
    }

    public static SwimmingProto.Proba toGrpcProba(Proba proba) {
        return SwimmingProto.Proba.newBuilder()
                .setId(proba.getId() != null ? proba.getId() : 0)
                .setDistanta(proba.getDistanta())
                .setStil(proba.getStil())
                .setParticipantCount(proba.getParticipantCount())
                .build();
    }

    public static Proba fromGrpcProba(SwimmingProto.Proba grpcProba) {
        Proba proba = new Proba(grpcProba.getId() != 0 ? grpcProba.getId() : null,
                grpcProba.getDistanta(), grpcProba.getStil());
        proba.setParticipantCount(grpcProba.getParticipantCount());
        return proba;
    }

    public static SwimmingProto.Participant toGrpcParticipant(Participant participant){
        return SwimmingProto.Participant.newBuilder()
                .setId(participant.getId() != null ? participant.getId() : 0)
                .setNume(participant.getNume())
                .setPrenume(participant.getPrenume())
                .setVarsta(participant.getVarsta())
                .setEventCount(participant.getEventCount())
                .build();
    }

    public static Participant fromGrpcParticipant(SwimmingProto.Participant grpcParticipant)
    {
        Participant participant = new Participant(
                grpcParticipant.getId() != 0 ? grpcParticipant.getId() : null,
                grpcParticipant.getNume(),
                grpcParticipant.getPrenume(),
                grpcParticipant.getVarsta());
        participant.setEventCount(grpcParticipant.getEventCount());
        return participant;
    }

    public static SwimmingProto.Inscriere toGrpcInscriere(Inscriere inscriere) {
        return SwimmingProto.Inscriere.newBuilder()
                .setId(inscriere.getId() != null ? inscriere.getId() : 0)
                .setParticipant(toGrpcParticipant(inscriere.getParticipant()))
                .setProba(toGrpcProba(inscriere.getProba()))
                .build();
    }

    public static Inscriere fromGrpcInscriere(SwimmingProto.Inscriere grpcInscriere) {
        return new Inscriere(
                grpcInscriere.getId() != 0 ? grpcInscriere.getId() : null,
                fromGrpcParticipant(grpcInscriere.getParticipant()),
                fromGrpcProba(grpcInscriere.getProba()));
    }

    // ✅ FIXED CLIENT ERROR HANDLING FOR NOT FOUND PARTICIPANT
    public static java.util.concurrent.CompletableFuture<java.util.Optional<Participant>> handleFindParticipantResponse(SwimmingProto.ParticipantResponse response) {
        java.util.concurrent.CompletableFuture<java.util.Optional<Participant>> future = new java.util.concurrent.CompletableFuture<>();

        if (!response.getError().isEmpty() && response.getError().equalsIgnoreCase("Participant not found")) {
            future.complete(java.util.Optional.empty());
        } else if (!response.getError().isEmpty()) {
            future.completeExceptionally(new Exception(response.getError()));
        } else {
            future.complete(java.util.Optional.of(fromGrpcParticipant(response.getParticipant())));
        }

        return future;
    }
}
