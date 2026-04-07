package org.sleepless_artery.kv_spring.config.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sleepless_artery.kv_spring.service.KVGrpcService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


/**
 * gRPC server configuration.
 *
 * <p>Initializes and manages the lifecycle of the gRPC server,
 * including startup and graceful shutdown.</p>
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class GrpcServerConfig {

    private final KVGrpcService grpcService;

    @Value("${spring.grpc.server.port:9090}")
    private int port;

    private Server server;

    @PostConstruct
    public void start() throws IOException {
        log.info("Starting gRPC server on port {}", port);

        server = ServerBuilder.forPort(port)
                .addService(grpcService)
                .addService(ProtoReflectionService.newInstance())
                .build()
                .start();

        log.info("gRPC server started on port {}", port);

        addShutdownHook();
    }


    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("JVM shutdown hook triggered, stopping gRPC server...");
            try {
                if (server != null && !server.isShutdown()) {
                    server.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                    log.info("gRPC server stopped via shutdown hook");
                }
            } catch (InterruptedException e) {
                log.error("Error during shutdown hook", e);
                if (server != null) {
                    server.shutdownNow();
                }
                Thread.currentThread().interrupt();
            }
        }, "grpc-shutdown-hook"));
    }


    @PreDestroy
    public void stop() {
        log.info("Stopping gRPC server");

        if (server == null) {
            return;
        }

        server.shutdown();

        try {
            if (!server.awaitTermination(10, TimeUnit.SECONDS)) {
                log.warn("gRPC server did not terminate in time, forcing shutdown");
                server.shutdownNow();
                server.awaitTermination(5, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            log.warn("Interrupted during gRPC server shutdown", e);
            server.shutdownNow();
            Thread.currentThread().interrupt();
        }

        log.info("gRPC server stopped");
    }
}