package test.ennov.ticketmanagement.utils.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import test.ennov.ticketmanagement.model.dto.UserDTO;
import test.ennov.ticketmanagement.model.entity.User;
import test.ennov.ticketmanagement.service.UserService;
import test.ennov.ticketmanagement.utils.exceptions.UserNotFoundException;

import java.util.List;

@Component
public class UserMapperImpl implements UserMapper {

    @Autowired
    private UserService userService;

    @Override
    public User userDtoToUser(UserDTO userDto) throws UserNotFoundException {
        User user = null;
        if (userDto != null) {
            user = new User();
            user.setId(userDto.getId());
            user.setEmail(userDto.getEmail());
            user.setUsername(userDto.getUsername());
            if (userDto.getId() != null) {
                user.setTickets(userService.getUserTickets(userDto.getId()));
            }
        }
        return user;
    }

    @Override
    public UserDTO userToUserDto(User user) {
        UserDTO userDto = null;
        if (user != null) {
            userDto = new UserDTO();
            userDto.setId(user.getId());
            userDto.setEmail(user.getEmail());
            userDto.setUsername(user.getUsername());
        }
        return userDto;
    }

    @Override
    public List<UserDTO> mapListToDto(List<User> users) {
        List<UserDTO> usersDto = users.stream().map(this::userToUserDto).toList();
        return usersDto;
    }
}
