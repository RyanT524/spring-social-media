package com.cooksys.socialmedia.services.impl;

import com.cooksys.socialmedia.repositories.HashtagRepository;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.ValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidateServiceImpl implements ValidateService {

    private final UserRepository userRepository;

    private final HashtagRepository hashtagRepository;

    @Override
    public boolean validateUsernameExists(String username) {
        return userRepository.findByDeletedFalseAndCredentials_UsernameIgnoreCase(username).isPresent();
    }

    @Override
    public boolean validateUsernameAvailable(String username) {
        return !(validateUsernameExists(username));
    }
  
      @Override
    public boolean validateHashtagExists(String label) {
        return hashtagRepository.findByLabelIgnoreCase(label).isPresent();
    }
  
}
