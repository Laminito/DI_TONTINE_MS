package sn.ditontineplateform.domaine.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import sn.ditontineplateform.domaine.dto.UserDto;
import sn.ditontineplateform.domaine.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto toDto (User user);

    User toEntity (UserDto userDto);

    List<UserDto> toDtoList (List<User> users);

    List<User> toEntityList (List<UserDto> userDtos);

}
