package com.cooksys.socialmedia.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;
import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReplyTweetResponseDto {

    private Long id;
    
    private User author;
    
    private Timestamp posted;
    
    private String content;
    
    private Tweet inReplyTo;
}
