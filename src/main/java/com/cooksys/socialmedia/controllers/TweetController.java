package com.cooksys.socialmedia.controllers;

import com.cooksys.socialmedia.dtos.*;

import com.cooksys.socialmedia.services.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tweets")
@RequiredArgsConstructor
public class TweetController {

    private final TweetService tweetService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public TweetResponseDto createTweet(
        @RequestBody TweetRequestDto tweetRequestDto) {
        return tweetService.createTweet(tweetRequestDto);
    }


    @GetMapping("/{id}/reposts")
    public List<TweetResponseDto> getTweetReposts(@PathVariable("id") Long id) {
        return tweetService.getTweetReposts(id);
    }


    @GetMapping
    public List<TweetResponseDto> getAllTweets() {
        return tweetService.getAllTweets();
    }
    
    @DeleteMapping("/{id}")
    public TweetResponseDto deleteTweet(@PathVariable(name = "id") Long id) {
        return tweetService.deleteTweet(id);
    }
    
    @GetMapping("/{id}")
    public TweetResponseDto getTweetById(@PathVariable("id") Long id) {
        return tweetService.getTweetById(id);
    }

    @GetMapping("/{id}/context")
    public ContextDto getTweetContext(@PathVariable("id") Long id) {
        return tweetService.getTweetContext(id);
    }

    @PostMapping("/{id}/like")
    @ResponseStatus(HttpStatus.OK)
    public void likeTweet(@PathVariable("id") Long id, @RequestBody CredentialsDto credentialsDto) {
        tweetService.likeTweet(id, credentialsDto);
    }

    @GetMapping("/{id}/likes")
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDto> getTweetLikes(@PathVariable("id") Long id) {
        return tweetService.getTweetLikes(id);
    }

    @GetMapping("/{id}/mentions")
    public List<UserResponseDto> getMentionedUsers(@PathVariable("id") Long id) {
        return tweetService.getMentionedUsers(id);
    }

    @GetMapping("/{id}/tags")
    public List<HashtagDto> getTweetTags(@PathVariable(name = "id") Long id) {
        return tweetService.getTweetTags(id);
    }

    @PostMapping("/{id}/reply")
    public TweetResponseDto createReplyTweet(@PathVariable("id") Long id, @RequestBody TweetRequestDto tweetRequestDto) {
        return tweetService.createReplyTweet(id, tweetRequestDto);
    }
    
    @GetMapping("/{id}/replies")
    public List<TweetResponseDto> getTweetReplies(@PathVariable("id") Long id) {
        return tweetService.getTweetReplies(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{id}/repost")
    public TweetResponseDto repostTweet(@PathVariable("id") Long id, @RequestBody CredentialsDto credentialsDto) {
        return tweetService.repostTweet(id, credentialsDto);
    }
  
}
