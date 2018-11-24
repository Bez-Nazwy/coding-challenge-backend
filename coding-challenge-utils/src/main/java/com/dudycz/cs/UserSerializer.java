package com.dudycz.cs;

import com.cs.domain.User;
import com.google.gson.JsonObject;

public final class UserSerializer {

    public static User deserialize(JsonObject json) {
        var username = json.get("username").getAsString();
        var password = json.get("username").getAsString();

        return new User(username, password);
    }

    private UserSerializer() {
    }
}
