package test.ennov.ticketmanagement.utils.mapper;

import test.ennov.ticketmanagement.model.dto.UserDTO;
import test.ennov.ticketmanagement.model.entity.User;
import test.ennov.ticketmanagement.utils.exceptions.UserNotFoundException;

import java.util.List;

public interface UserMapper {
    User userDtoToUser(UserDTO userDto) throws UserNotFoundException;
    UserDTO userToUserDto(User user);
    List<UserDTO> mapListToDto(List<User> users);
}
