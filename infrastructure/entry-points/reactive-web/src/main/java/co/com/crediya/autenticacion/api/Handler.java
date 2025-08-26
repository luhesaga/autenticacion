package co.com.crediya.autenticacion.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class Handler {
//private  final UseCase useCase;
//private  final UseCase2 useCase2;

    public Mono<ServerResponse> listenGETUseCase(ServerRequest serverRequest) {
        log.trace("[Handler] GET {}?{}", serverRequest.path(), serverRequest.uri().getQuery());
        // useCase.logic();
        return ServerResponse.ok().bodyValue("")
                .doOnSuccess(r -> log.debug("[Handler] Respuesta enviada para {}", serverRequest.path()))
                .doOnError(e -> log.warn("[Handler] Error atendiendo {}: {}", serverRequest.path(), e.getMessage(), e));
    }

    public Mono<ServerResponse> listenGETOtherUseCase(ServerRequest serverRequest) {
        log.trace("[Handler] GET {}?{}", serverRequest.path(), serverRequest.uri().getQuery());
        // useCase2.logic();
        return ServerResponse.ok().bodyValue("")
                .doOnSuccess(r -> log.debug("[Handler] Respuesta enviada para {}", serverRequest.path()))
                .doOnError(e -> log.warn("[Handler] Error atendiendo {}: {}", serverRequest.path(), e.getMessage(), e));
    }

    public Mono<ServerResponse> listenPOSTUseCase(ServerRequest serverRequest) {
        log.trace("[Handler] POST {}?{}", serverRequest.path(), serverRequest.uri().getQuery());
        // useCase.logic();
        return ServerResponse.ok().bodyValue("")
                .doOnSuccess(r -> log.debug("[Handler] Respuesta enviada para {}", serverRequest.path()))
                .doOnError(e -> log.warn("[Handler] Error atendiendo {}: {}", serverRequest.path(), e.getMessage(), e));
    }
}
