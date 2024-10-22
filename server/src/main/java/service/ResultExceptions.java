package service;

public class ResultExceptions extends Exception{
    public ResultExceptions(String message) {super(message);}

    public static class BadRequestError extends Exception{
        public BadRequestError(String message) {super(message);}
    }

    public static class AlreadyTakenError extends Exception{
        public AlreadyTakenError(String message) {super(message);}
    }

    public static class AuthorizationError extends Exception{
        public AuthorizationError(String message) {super(message);}
    }
}
