package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserUpdateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserInputDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
    User userInputDtoToUser(UserInputDto userInputDto);
    UserDetailsDto userToUserDetailsDto(User user);
    User userUpdateDtoToUser(UserUpdateDto userUpdateDto);
}
