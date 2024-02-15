package com.cooksys.socialmedia.controllers;

import com.cooksys.socialmedia.dtos.CredentialsDto;
import com.cooksys.socialmedia.dtos.ProfileDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.dtos.UserRequestDto;
import com.cooksys.socialmedia.dtos.UserResponseDto;
import com.cooksys.socialmedia.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }
    
    @PatchMapping("/@{username}")
    public UserResponseDto updateUser(@PathVariable("username") String username, @RequestBody Map<String, Object> request) {
        CredentialsDto credentialsDto = objectMapper.convertValue(request.get("credentials"), CredentialsDto.class);
        ProfileDto profileDto = objectMapper.convertValue(request.get("profile"), ProfileDto.class);
        return userService.updateUser(username, credentialsDto, profileDto);
    }
    
    @DeleteMapping("/@{username}")
    public UserResponseDto deleteUser(@PathVariable("username") String username, @RequestBody CredentialsDto credentialsDto) {
        return userService.deleteUser(username, credentialsDto);
    }
    
    @GetMapping("/@{username}")
    public UserResponseDto getUser(@PathVariable("username") String username) {
        return userService.getUser(username);
    }
    
    @GetMapping("/@{username}/following")
    public List<UserResponseDto> getFollowers(@PathVariable("username") String username) {
        return userService.getFollowing(username);
    }
    
    @GetMapping("/@{username}/followers")
    public List<UserResponseDto> getFollowing(@PathVariable("username") String username) {
        return userService.getFollowers(username);
    }
    
    @GetMapping("/@{username}/tweets")
    public List<TweetResponseDto> getUserTweets(@PathVariable("username") String username) {
        return userService.getUserTweets(username);
    }
    
    @GetMapping("/@{username}/feed")
    public List<TweetResponseDto> getUserFeed(@PathVariable("username") String username) {
        return userService.getUserFeed(username);
    }
    
    @GetMapping("/@{username}/mentions")
    public List<TweetResponseDto> getUserMentions(@PathVariable("username") String username) {
        return userService.getUserMentions(username);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserResponseDto createUser(@RequestBody UserRequestDto userRequestDto) {
        return userService.createUser(userRequestDto);
    }
    
    @PostMapping("/@{username}/follow")
    public void followUser(@PathVariable("username") String username, @RequestBody CredentialsDto credentialsDto) {
        userService.followUser(username, credentialsDto);
    }

    @PostMapping("/@{username}/unfollow")
    public void unfollowUser(@PathVariable("username") String username, @RequestBody CredentialsDto credentialsDto) {
        userService.unfollowUser(username, credentialsDto);
    }
  
}
