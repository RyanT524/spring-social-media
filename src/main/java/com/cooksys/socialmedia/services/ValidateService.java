package com.cooksys.socialmedia.services;

public interface ValidateService {
    boolean validateUsernameExists(String username);

    boolean validateHashtagExists(String label);

    boolean validateUsernameAvailable(String username);

}
