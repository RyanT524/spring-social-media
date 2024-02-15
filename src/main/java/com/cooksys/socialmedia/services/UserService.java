package com.cooksys.socialmedia.services;

import com.cooksys.socialmedia.dtos.CredentialsDto;
import com.cooksys.socialmedia.dtos.ProfileDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.dtos.UserRequestDto;
import com.cooksys.socialmedia.dtos.UserResponseDto;

import java.util.List;

public interface UserService {
    List<UserResponseDto> getAllUsers();

    UserResponseDto updateUser(String username, CredentialsDto credentialsDto, ProfileDto profileDto);

    UserResponseDto deleteUser(String username, CredentialsDto credentialsDto);

    UserResponseDto getUser(String username);

    List<UserResponseDto> getFollowers(String username);

    List<UserResponseDto> getFollowing(String username);

    UserResponseDto createUser(UserRequestDto userRequestDto);

    List<TweetResponseDto> getUserTweets(String username);

    List<TweetResponseDto> getUserFeed(String username);

    List<TweetResponseDto> getUserMentions(String username);

    void followUser(String username, CredentialsDto credentialsDto);

    void unfollowUser(String username, CredentialsDto credentialsDto);

}
