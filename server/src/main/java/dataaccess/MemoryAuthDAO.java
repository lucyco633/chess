package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{

    HashMap<String, AuthData> authDB = new HashMap<>();

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authDB.get(authToken);
    }

    @Override
    public String createAuth(String username) throws DataAccessException {
        AuthData newAuth = new AuthData(UUID.randomUUID().toString(), username);
        authDB.put(newAuth.authToken(), newAuth);
        return newAuth.authToken();
    }

    @Override
    public void updateAuth(AuthData authData, String username) {
        AuthData updatedAuth = new AuthData(authData.authToken(), username);
        authDB.put(authData.authToken(), updatedAuth);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        authDB.remove(authToken);
    }
}
