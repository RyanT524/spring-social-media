package com.cooksys.socialmedia.services.impl;

import com.cooksys.socialmedia.dtos.HashtagDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.entities.Hashtag;
import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.HashtagMapper;
import com.cooksys.socialmedia.mappers.TweetMapper;
import com.cooksys.socialmedia.repositories.HashtagRepository;
import com.cooksys.socialmedia.services.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {

    private final HashtagRepository hashtagRepository;

    private final TweetMapper tweetMapper;

    private final HashtagMapper hashtagMapper;
    @Override
    public List<TweetResponseDto> getTweetsByTag(String label) {

        Optional<Hashtag> optionalHashtag = hashtagRepository.findByLabelIgnoreCase(label);

        if (optionalHashtag.isEmpty()) {
            throw new NotFoundException("No hashtag found with label: " + label);
        }

        List<Tweet> tweets = optionalHashtag.get().getTweets();

        // Sort tweets in descending order
        Collections.sort(tweets, (tweet1, tweet2) -> tweet2.getPosted().compareTo(tweet1.getPosted()));

        return tweetMapper.entitiesToResponseDtos(tweets);
    }

    @Override
    public List<HashtagDto> getAllHashtags() {
        List<Hashtag> hashtags = hashtagRepository.findAll();
        return hashtagMapper.entitiesToDtos(hashtags);
    }
}
