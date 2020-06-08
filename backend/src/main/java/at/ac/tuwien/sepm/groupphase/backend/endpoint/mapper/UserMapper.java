package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserEditInquiryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserInputDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserOutputDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
    User userInputDtoToUser(UserInputDto userInputDto);
    UserDetailsDto userToUserOutputDto(User user);
    User userEditInquiryDtoToUser(UserEditInquiryDto userEditInquiryDto);
}
