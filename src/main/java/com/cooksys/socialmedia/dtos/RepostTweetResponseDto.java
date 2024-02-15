package com.cooksys.socialmedia.dtos;

import com.cooksys.socialmedia.entities.Tweet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RepostTweetResponseDto {

    private Long id;
    
    private UserResponseDto author;
    
    private Timestamp posted;
    
    private Tweet repostOf;
}
