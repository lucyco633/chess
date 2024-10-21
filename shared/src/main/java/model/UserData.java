package model;

import com.google.gson.*;

import java.util.HashMap;
import java.util.Map;

public record UserData(String username, String password, String email) {

    public static Map<String, UserData> userList = new HashMap<>();

    public UserData setUsername(String username){
        return new UserData(username, this.password, this.email);
    }

    public UserData setPassword(String password){
        return new UserData(this.username, password, this.email);
    }

    public String getPassword(){
        return this.password;
    }

    public UserData setEmail(String email){
        return new UserData(this.username, this.password, email);
    }

    public String getEmail(){
        return this.email;
    }

    public UserData getUser(String username){
        if (userList.containsKey(username)){
            return userList.get(username);
        }
        return null;
    }

    public UserData createUser(UserData user){
        userList.put(user.username, user);
        return user;
    }

    public UserData deleteUserData(){
        userList.clear();
        return null;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
