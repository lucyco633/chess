package model;

import com.google.gson.*;

public record User(String username, String password, String email) {
    public User setUsername(String username){
        return new User(username, this.password, this.email);
    }

    public User setPassword(String password){
        return new User(this.username, password, this.email);
    }

    public User setEmail(String email){
        return new User(this.username, this.password, email);
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
