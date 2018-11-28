package com.cs.core.http.handlers;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
import static org.springframework.web.reactive.function.server.ServerResponse.created;
import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import com.cs.core.data.services.ItemService;
import com.cs.domain.Item;
import com.cs.utils.ResponseUtils;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ItemsHandler {

    private ItemService itemService;

    @Autowired
    public ItemsHandler(ItemService itemService) {
        this.itemService = itemService;
    }


    public Mono<ServerResponse> getItem(ServerRequest request) {
        var id = Integer.parseInt(request.pathVariable("id"));
        return itemService
            .getItem(id)
            .flatMap(item -> ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromObject(item))
            )
            .switchIfEmpty(notFound().build());
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(itemService.getAll(), Item.class);
    }

    public Mono<ServerResponse> addItem(ServerRequest request) {
        return request
            .bodyToMono(Item.class)
            .flatMap(itemService::addItem)
            .flatMap(item -> created(constructResourceURI(request, item)).build())
            .switchIfEmpty(badRequest().build())
            .onErrorResume(ResponseUtils::handleReactiveError);
    }

    private URI constructResourceURI(ServerRequest request, Item item) {
        return request.uri().resolve("/" + item.getId());
    }
}
