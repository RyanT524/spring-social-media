package com.cooksys.socialmedia.mappers;

import java.util.List;
import org.mapstruct.Mapper;
import com.cooksys.socialmedia.dtos.TweetRequestDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.entities.Tweet;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface TweetMapper {


    Tweet tweetRequestDtoToEntity(TweetRequestDto tweetRequestDto);

    TweetResponseDto entityToTweetResponseDto(Tweet tweet);

    List<TweetResponseDto> entitiesToResponseDtos(List<Tweet> tweets);

}
