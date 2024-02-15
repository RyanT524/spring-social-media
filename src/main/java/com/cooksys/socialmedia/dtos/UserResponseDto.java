package com.cooksys.socialmedia.dtos;

import com.cooksys.socialmedia.entities.Profile;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@NoArgsConstructor
@Data
public class UserResponseDto {

    private String username;

    private ProfileDto profile;

    private Timestamp joined;
}
