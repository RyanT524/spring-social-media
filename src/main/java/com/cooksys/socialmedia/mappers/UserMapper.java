package com.cooksys.socialmedia.mappers;

import com.cooksys.socialmedia.dtos.UserRequestDto;
import com.cooksys.socialmedia.dtos.UserResponseDto;
import com.cooksys.socialmedia.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

import java.util.List;

@Mapper(componentModel = "spring", uses = { CredentialsMapper.class, ProfileMapper.class })
public interface UserMapper {

    User requestDtoToEntity(UserRequestDto userRequestDto);

    @Mapping(source = "credentials.username", target = "username")
    UserResponseDto entityToResponseDto(User user);

    @Mapping(source = "credentials.username", target = "username")
    List<UserResponseDto> entitiesToResponseDtos(List<User> users);

}
