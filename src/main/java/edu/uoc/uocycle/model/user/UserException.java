package edu.uoc.uocycle.model.user;

public class UserException extends Exception {

    public static final String INVALID_NAME = "Name cannot be null or empty";
    public static final String USER_NOT_FOUND = "User not found";

    public UserException(String message) {
        super(message);
    }

}
