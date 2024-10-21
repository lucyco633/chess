package model;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public record AuthData(String authToken, String username) {

    public static Map<String, AuthData> authList = new HashMap<>();

    public AuthData setAuthToken(String authToken){
        return new AuthData(authToken, this.username);
    }

    public String getAuthToken(){
        return this.authToken;
    }

    public AuthData setUsername(String username){
        return new AuthData(this.authToken, username);
    }

    public String getUsername(){
        return this.username;
    }

    public AuthData createAuth(AuthData authorization){
        authList.put(authorization.authToken, authorization);
        return authorization;
    }

    public AuthData deleteAuthData(){
        authList.clear();
        return null;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
