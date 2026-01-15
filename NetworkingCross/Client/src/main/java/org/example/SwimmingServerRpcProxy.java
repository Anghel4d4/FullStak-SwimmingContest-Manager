package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.grpc.*;
import org.example.model.*;
import org.example.service.ISwimmingObserver;
import org.example.service.ISwimmingServices;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class SwimmingServerRpcProxy implements ISwimmingServices {

    private static final Logger logger = LogManager.getLogger(SwimmingServerRpcProxy.class);

    private final String host;
    private final int port;
    private final ManagedChannel channel;
    private final SwimmingServiceGrpc.SwimmingServiceStub asyncStub;
    private ISwimmingObserver clientObserver;
    private volatile boolean finished;

    public SwimmingServerRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.asyncStub = SwimmingServiceGrpc.newStub(channel);
        this.finished = false;
        logger.info("gRPC client initialized for {} : {}", host, port);
    }

    private void startNotificationStream() {
        CountDownLatch latch = new CountDownLatch(1);
        asyncStub.subscribeToNotifications(SwimmingProto.Empty.newBuilder().build(),
                new StreamObserver<>() {
                    @Override
                    public void onNext(SwimmingProto.InscriereNotification notif) {

                        try {
                            Inscriere inscriere = ModelConverter.fromGrpcInscriere(notif.getInscriere());
                            logger.info("Received registration notification for: {}", inscriere.getParticipant().getNume());
                            if (clientObserver != null) {
                                clientObserver.notifyRegister(inscriere);
                            }
                        } catch (Exception e) {
                            logger.error("Notification error: {}", e.getMessage(), e);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        logger.error("Notification stream error: {}", t.getMessage(), t);
                        finished = true;
                        latch.countDown();
                    }

                    @Override
                    public void onCompleted() {
                        logger.info("Notification stream completed");
                        finished = true;
                        latch.countDown();
                    }
                });
    }

    @Override
    public CompletableFuture<User> login(User user, ISwimmingObserver client) {
        this.clientObserver = client;
        startNotificationStream();

        SwimmingProto.LoginRequest request = SwimmingProto.LoginRequest.newBuilder()
                .setUsername(user.getUsername())
                .setPassword(user.getPassword())
                .build();

        CompletableFuture<User> future = new CompletableFuture<>();

        asyncStub.login(request, new StreamObserver<>() {
            @Override
            public void onNext(SwimmingProto.LoginResponse response) {
                if (!response.getError().isEmpty()) {
                    future.completeExceptionally(new Exception(response.getError()));
                } else {
                    future.complete(ModelConverter.fromGrpcUser(response.getUser()));
                }
            }

            @Override
            public void onError(Throwable t) {
                future.completeExceptionally(t);
            }

            @Override
            public void onCompleted() {}
        });

        return future;
    }

    @Override
    public CompletableFuture<Void> logout(User user, ISwimmingObserver client) {
        SwimmingProto.LogoutRequest request = SwimmingProto.LogoutRequest.newBuilder()
                .setUserId(Optional.ofNullable(user.getId()).orElse(0L))
                .build();

        CompletableFuture<Void> future = new CompletableFuture<>();

        asyncStub.logout(request, new StreamObserver<>() {
            @Override
            public void onNext(SwimmingProto.Empty response) {
                future.complete(null);
            }

            @Override
            public void onError(Throwable t) {
                future.completeExceptionally(t);
            }

            @Override
            public void onCompleted() {
                closeConnection();
            }
        });

        return future;
    }

    @Override
    public CompletableFuture<List<Proba>> getAllProbes() {
        CompletableFuture<List<Proba>> future = new CompletableFuture<>();

        asyncStub.getAllProbes(SwimmingProto.Empty.newBuilder().build(), new StreamObserver<>() {
            @Override
            public void onNext(SwimmingProto.ProbesResponse response) {
                List<Proba> probes = response.getProbesList().stream()
                        .map(ModelConverter::fromGrpcProba)
                        .collect(Collectors.toList());
                future.complete(probes);
            }

            @Override
            public void onError(Throwable t) {
                future.completeExceptionally(t);
            }

            @Override
            public void onCompleted() {}
        });

        return future;
    }

    @Override
    public CompletableFuture<Void> addInscriere(Inscriere inscriere, ISwimmingObserver client) {
        SwimmingProto.InscriereRequest request = SwimmingProto.InscriereRequest.newBuilder()
                .setInscriere(ModelConverter.toGrpcInscriere(inscriere))
                .build();

        CompletableFuture<Void> future = new CompletableFuture<>();

        asyncStub.addInscriere(request, new StreamObserver<>() {
            @Override
            public void onNext(SwimmingProto.Empty empty) {
                future.complete(null);
            }

            @Override
            public void onError(Throwable t) {
                future.completeExceptionally(t);
            }

            @Override
            public void onCompleted() {}
        });

        return future;
    }

    @Override
    public CompletableFuture<Participant> saveParticipant(Participant participant, ISwimmingObserver observer) {
        SwimmingProto.ParticipantRequest request = SwimmingProto.ParticipantRequest.newBuilder()
                .setParticipant(ModelConverter.toGrpcParticipant(participant))
                .build();

        CompletableFuture<Participant> future = new CompletableFuture<>();

        asyncStub.saveParticipant(request, new StreamObserver<>() {
            @Override
            public void onNext(SwimmingProto.ParticipantResponse response) {
                if (!response.getError().isEmpty()) {
                    future.completeExceptionally(new Exception(response.getError()));
                } else {
                    future.complete(ModelConverter.fromGrpcParticipant(response.getParticipant()));
                }
            }

            @Override
            public void onError(Throwable t) {
                future.completeExceptionally(t);
            }

            @Override
            public void onCompleted() {}
        });

        return future;
    }

    @Override
    public CompletableFuture<List<Participant>> findParticipantsByProba(int distanta, String stil) {
        SwimmingProto.ProbaSearchRequest request = SwimmingProto.ProbaSearchRequest.newBuilder()
                .setDistanta(distanta)
                .setStil(stil)
                .build();

        CompletableFuture<List<Participant>> future = new CompletableFuture<>();

        asyncStub.findParticipantsByProba(request, new StreamObserver<>() {
            @Override
            public void onNext(SwimmingProto.ParticipantsResponse response) {
                List<Participant> participants = response.getParticipantsList().stream()
                        .map(ModelConverter::fromGrpcParticipant)
                        .collect(Collectors.toList());
                future.complete(participants);
            }

            @Override
            public void onError(Throwable t) {
                future.completeExceptionally(t);
            }

            @Override
            public void onCompleted() {}
        });

        return future;
    }

    @Override
    public CompletableFuture<Optional<Participant>> findNameAndPrenume(String nume, String prenume) {
        SwimmingProto.NameSearchRequest request = SwimmingProto.NameSearchRequest.newBuilder()
                .setNume(nume)
                .setPrenume(prenume)
                .build();

        CompletableFuture<Optional<Participant>> future = new CompletableFuture<>();

        asyncStub.findNameAndPrenume(request, new StreamObserver<>() {
            @Override
            public void onNext(SwimmingProto.ParticipantResponse response) {

                ModelConverter.handleFindParticipantResponse(response).whenComplete((result, ex) -> {
                    if (ex != null) {
                        future.completeExceptionally(ex);
                    } else {
                        future.complete(result);
                    }
                });
            }

            @Override public void onError(Throwable t) { future.completeExceptionally(t); }
            @Override public void onCompleted() {}
        });

        return future;
    }


    public void closeConnection() {
        finished = true;
        try {
            if (channel != null && !channel.isShutdown()) {
                channel.shutdownNow();
                logger.info("gRPC channel shutdown");
            }
        } catch (Exception e) {
            logger.error("Error shutting down gRPC channel: {}", e.getMessage(), e);
        }
    }
}
