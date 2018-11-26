package com.cs.core.data.services;

import com.cs.core.data.repositories.ItemRepository;
import com.cs.domain.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ItemService {

    private final static Logger logger = LoggerFactory.getLogger(UserService.class);

    private ItemRepository itemRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Mono<Item> getItem(int id) {
        return itemRepository
            .findById(id)
            .doOnError(err -> logger.warn("Error occurred when retrieving item with id {}: {}",
                id, err.getLocalizedMessage()));
    }


    public Flux<Item> getAll() {
        return itemRepository
            .findAll()
            .doOnError(err -> logger.warn("Error occurred when retrieving items: {}",
                err.getLocalizedMessage()));
    }

    public Mono<Item> addItem(Item item) {
        return itemRepository
            .save(item)
            .doOnError(err -> logger.warn("Error occurred when adding an item: {}",
                err.getLocalizedMessage()));
    }


}
