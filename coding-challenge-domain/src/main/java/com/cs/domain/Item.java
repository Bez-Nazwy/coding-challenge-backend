package com.cs.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "items")
public class Item {
    private static int nextId = 0;

    @Id
    private int id = nextId++;
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
