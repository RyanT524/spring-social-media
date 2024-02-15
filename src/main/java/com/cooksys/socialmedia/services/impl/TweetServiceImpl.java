package com.cooksys.socialmedia.services.impl;

import com.cooksys.socialmedia.dtos.*;

import com.cooksys.socialmedia.entities.Credentials;
import com.cooksys.socialmedia.entities.Hashtag;
import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.exceptions.BadRequestException;
import com.cooksys.socialmedia.exceptions.NotAuthorizedException;
import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.CredentialsMapper;
import com.cooksys.socialmedia.mappers.HashtagMapper;
import com.cooksys.socialmedia.mappers.TweetMapper;
import com.cooksys.socialmedia.mappers.UserMapper;
import com.cooksys.socialmedia.repositories.HashtagRepository;
import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

    private final TweetMapper tweetMapper;
    private final CredentialsMapper credentialsMapper;
    private final UserMapper userMapper;
    private final HashtagMapper hashtagMapper;

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final HashtagRepository hashtagRepository;

    @Override
    public List<TweetResponseDto> getAllTweets() {

        return tweetMapper.entitiesToResponseDtos(tweetRepository
            .findAllByDeletedFalseOrderByPostedDesc());
    }

    private Tweet getNotDeletedTweet(Long id) {

        Optional<Tweet> optionalTweet = tweetRepository.findByIdAndDeletedFalse(id);

        if (optionalTweet.isEmpty()) {
            throw new NotFoundException("No Tweet found with id: " + id);
        }

        return optionalTweet.get();
    }


    private List<String> parseTweetContent(
        String content,
        String regex,
        boolean caseSensitive) {
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(content);

        List<String> matches = new ArrayList<>();
        while (m.find()) {
            String match = caseSensitive ? m.group() : m.group().toLowerCase();
            matches.add(match);
        }
        return matches;
    }


    private void processMentionedUsers(Tweet tweetToSave) {
        List<String> mentionedUsernames = parseTweetContent(tweetToSave
            .getContent(), "(?<=@)\\w+", true);
        List<User> mentionedUsers = new ArrayList<>();

        for (String username : mentionedUsernames) {
            Optional<User> optionalMentionedUser = userRepository
                .findByDeletedFalseAndCredentials_UsernameIgnoreCase(username);

            if (optionalMentionedUser.isPresent()) {
                mentionedUsers.add(optionalMentionedUser.get());
            }
        }

        tweetToSave.setMentionedUsers(mentionedUsers);
    }


    private void processHashtags(Tweet tweetToSave) {

        List<String> hashtagsFound = parseTweetContent(tweetToSave.getContent(),
            "(?<=#)\\w+", true);
        List<Hashtag> hashtags = new ArrayList<>();

        for (String label : hashtagsFound) {
            Optional<Hashtag> optionalHashtag = hashtagRepository.findByLabel(
                label);
            Hashtag hashtag;

            if (optionalHashtag.isEmpty()) {
                hashtag = new Hashtag(label);
            }
            else {
                hashtag = optionalHashtag.get();
                hashtag.setLastUsed(Timestamp.from(Instant.now()));
            }
            hashtagRepository.saveAndFlush(hashtag);
            hashtags.add(hashtag);
        }

        tweetToSave.setHashtags(hashtags);
    }


    @Override
    public TweetResponseDto createTweet(TweetRequestDto tweetRequestDto) {

        Tweet tweetToSave = tweetMapper.tweetRequestDtoToEntity(
            tweetRequestDto);

        if (tweetToSave.getContent() == null) {
            throw new BadRequestException("New tweet must have content");
        }

        Credentials credentials = credentialsMapper.dtoToEntity(tweetRequestDto
            .getCredentials());
        Optional<User> optionalUser = userRepository.findByCredentials(
            credentials);

        if (optionalUser.isEmpty() || optionalUser.get().isDeleted()) {
            throw new NotAuthorizedException(
                "User with given credentials does not exist");
        }

        tweetToSave.setAuthor(optionalUser.get());
        processMentionedUsers(tweetToSave);
        processHashtags(tweetToSave);

        return tweetMapper.entityToTweetResponseDto(tweetRepository
            .saveAndFlush(tweetToSave));
    }


    @Override
    public List<TweetResponseDto> getTweetReposts(Long id) {

        Optional<Tweet> optionalRepostedTweet = tweetRepository
            .findByIdAndDeletedFalse(id);

        if (optionalRepostedTweet.isEmpty()) {
            throw new NotFoundException("No tweet found with id: " + id);
        }

        Tweet repostedTweet = getNotDeletedTweet(id);

        List<Tweet> notDeletedReposts = new ArrayList<>();

        for (Tweet tweet : repostedTweet.getReposts()) {
            if (!tweet.isDeleted()) {
                notDeletedReposts.add(tweet);
            }
        }

        return tweetMapper.entitiesToResponseDtos(notDeletedReposts);
    }

    @Override
    public TweetResponseDto deleteTweet(Long id) {
        Tweet tweetToDelete = getNotDeletedTweet(id);

        tweetToDelete.setDeleted(true);

        return tweetMapper.entityToTweetResponseDto(tweetRepository
            .saveAndFlush(tweetToDelete));

    }

    @Override
    public TweetResponseDto getTweetById(Long id) {
        Tweet tweetToGet = getNotDeletedTweet(id);

        return tweetMapper.entityToTweetResponseDto(tweetToGet);
    }
  
    @Override
    public List<HashtagDto> getTweetTags(Long id) {

        Tweet tweetWithTags = getNotDeletedTweet(id);
        if (tweetWithTags.isDeleted() == true) {
            throw new NotAuthorizedException("Tweet has been deleted");
        }

        List<Hashtag> hashtags = hashtagRepository.findByTweets_Id(tweetWithTags
            .getId());

        return hashtagMapper.hashtagEntitiestoDtos(hashtags);
    }

    @Override
    public TweetResponseDto createReplyTweet(
        Long id,
        TweetRequestDto tweetRequestDto) {

        Optional<Tweet> optionalTweet = tweetRepository.findByIdAndDeletedFalse(id);
        if (optionalTweet.isEmpty()) {
            throw new NotFoundException("No Tweet found with id: " + id);
        }
        
        Tweet tweetToReplyTo = optionalTweet.get();
        
        Credentials providedCredentials = credentialsMapper.dtoToEntity(tweetRequestDto.getCredentials());
        Optional<User> optionalUser = userRepository.findByCredentials(providedCredentials);
        
        if(optionalUser.isEmpty()) {
            throw new NotFoundException("No user found with provided credentials");
        }
        
        Tweet reply = tweetMapper.tweetRequestDtoToEntity(tweetRequestDto);
        reply.setAuthor(optionalUser.get());


        reply.setInReplyTo(tweetToReplyTo);

        return tweetMapper.entityToTweetResponseDto(tweetRepository
            .saveAndFlush(reply));
    }

    @Override
    public List<TweetResponseDto> getTweetReplies(Long id) {

        Tweet tweetWithReplies = getNotDeletedTweet(id);
        
        List<Tweet> notDeletedReplies = new ArrayList<>();

        for (Tweet tweet : tweetWithReplies.getReplies()) {
            if (!tweet.isDeleted()) {
                notDeletedReplies.add(tweet);
            }
        }

        return tweetMapper.entitiesToResponseDtos(notDeletedReplies);

    }

    private void getAllNotDeletedReplies(Tweet target, List<Tweet> allReplies) {

        for (Tweet reply : target.getReplies()) {
            if (!reply.isDeleted()) {
                allReplies.add(reply);
            }
            getAllNotDeletedReplies(reply, allReplies);
        }
    }

    private void getAllNotDeletedInReplyToTweets(Tweet target, List<Tweet> allInReplyToTweets) {

        Tweet inReplyTo = target.getInReplyTo();

        if (inReplyTo == null) return;

        if (!inReplyTo.isDeleted()) {
            allInReplyToTweets.add(inReplyTo);
        }

        getAllNotDeletedInReplyToTweets(inReplyTo, allInReplyToTweets);
    }

    @Override
    public ContextDto getTweetContext(Long id) {

        Tweet target = getNotDeletedTweet(id);

        List<Tweet> afterContext = new ArrayList<>();
        getAllNotDeletedReplies(target, afterContext);
        Collections.sort(afterContext, (tweet1, tweet2) -> tweet1.getPosted().compareTo(tweet2.getPosted()));

        List<Tweet> beforeContext = new ArrayList<>();
        getAllNotDeletedInReplyToTweets(target, beforeContext);
        Collections.sort(beforeContext, (tweet1, tweet2) -> tweet1.getPosted().compareTo(tweet2.getPosted()));

        ContextDto context = new ContextDto();
        context.setTarget(tweetMapper.entityToTweetResponseDto(target));
        context.setBefore(tweetMapper.entitiesToResponseDtos(beforeContext));
        context.setAfter(tweetMapper.entitiesToResponseDtos(afterContext));

        return context;

    }

    @Override
    public void likeTweet(Long id, CredentialsDto credentialsDto) {
        Optional<Tweet> optionalTweet = tweetRepository.findByIdAndDeletedFalse(id);

        if (optionalTweet.isEmpty()) {
            throw new NotFoundException("No tweet found with id: " + id);
        }

        Tweet tweet = optionalTweet.get();

        Credentials credentials = credentialsMapper.dtoToEntity(credentialsDto);
        Optional<User> optionalUser = userRepository.findByCredentials(credentials);

        if (optionalUser.isEmpty() || optionalUser.get().isDeleted()) {
            throw new NotAuthorizedException("User with given credentials does not exist");
        }

        User user = optionalUser.get();

        tweet.getLikedBy().add(user);
        user.getUserLikes().add(tweet);

        tweetRepository.save(tweet);
        userRepository.save(user);
    }

    @Override
    public List<UserResponseDto> getTweetLikes(Long id) {
        Optional<Tweet> optionalTweet = tweetRepository.findByIdAndDeletedFalse(id);

        if (optionalTweet.isEmpty()) {
            throw new NotFoundException("No tweet found with id: " + id);
        }

        Tweet tweet = optionalTweet.get();

//        return tweet.getLikedBy().stream()
//                .filter(user -> !user.isDeleted())
//                .map(userMapper::entityToResponseDto)
//                .collect(Collectors.toList());
        Set<User> likers = new HashSet<User>();
        
        for (User user : tweet.getLikedBy()) {
            if (!user.isDeleted()) {
                likers.add(user);
            }
        }
        return userMapper.entitiesToResponseDtos(new ArrayList<User>(likers));
    }

    @Override
    public List<UserResponseDto> getMentionedUsers(Long id) {

        Tweet tweet = getNotDeletedTweet(id);

        List<User> notDeletedMentionedUsers = new ArrayList<>();

        for (User u : tweet.getMentionedUsers()) {
            if (!u.isDeleted()) {
                notDeletedMentionedUsers.add(u);
            }
        }

        return userMapper.entitiesToResponseDtos(notDeletedMentionedUsers);
    }

    @Override
    public TweetResponseDto repostTweet(Long id, CredentialsDto credentialsDto) {

        Tweet tweetToRepost = getNotDeletedTweet(id);
        Credentials providedAuthorCredentials = credentialsMapper.dtoToEntity(credentialsDto);

        // Unclear if username should be case-insensitive while looking up a user by credentials
        // Here I've written it so that the username IS case-sensitive
        Optional<User> optionalUser = userRepository.findByCredentialsAndDeletedFalse(providedAuthorCredentials);

        if (optionalUser.isEmpty()) {
            throw new NotFoundException("No user found with provided credentials");
        }

        Tweet repostTweetToCreate = new Tweet();
        repostTweetToCreate.setAuthor(optionalUser.get());
        repostTweetToCreate.setRepostOf(tweetToRepost);

        return tweetMapper.entityToTweetResponseDto(tweetRepository.saveAndFlush(repostTweetToCreate));

    }

}
