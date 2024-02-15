package com.cooksys.socialmedia.dtos;

import com.cooksys.socialmedia.entities.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SimpleTweetRequestDto {
    
    private String content;

    private CredentialsDto credentials;
}
