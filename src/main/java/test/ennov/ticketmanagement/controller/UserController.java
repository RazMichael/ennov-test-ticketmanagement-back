package test.ennov.ticketmanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import test.ennov.ticketmanagement.model.dto.TicketDTO;
import test.ennov.ticketmanagement.model.dto.UserDTO;
import test.ennov.ticketmanagement.model.entity.User;
import test.ennov.ticketmanagement.service.UserService;
import test.ennov.ticketmanagement.utils.exceptions.NoDataFoundException;
import test.ennov.ticketmanagement.utils.exceptions.UserNotFoundException;
import test.ennov.ticketmanagement.utils.mapper.TicketMapper;
import test.ennov.ticketmanagement.utils.mapper.UserMapper;

import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "User API")
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private TicketMapper ticketMapper;
    @Autowired
    private UserMapper userMapper;

    @Operation(
            description = "Get all users from database",
            summary = "Get all user",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "User not found",
                            responseCode = "404"
                    )
            }
    )
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = null;
        try {
            users = userMapper.mapListToDto(userService.getAllElements());
            return new ResponseEntity<List<UserDTO>>(users, HttpStatus.OK);
        } catch (NoDataFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
            description = "Get user Tickets",
            summary = "Get user Tickets",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "User not found",
                            responseCode = "404"
                    )
            }
    )
    @GetMapping("/{id}/tickets")
    public ResponseEntity<List<TicketDTO>> getUserTickets(@PathVariable("id") String userId) {
        List<TicketDTO> tickets = null;
        try {
            tickets = ticketMapper.mapListToDto(userService.getUserTickets(userId));
            return new ResponseEntity<List<TicketDTO>>(tickets, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

    }

    @Operation(
            description = "Create a user",
            summary = "Create a user",
            responses = {
                    @ApiResponse(
                            description = "Created",
                            responseCode = "201"
                    ),
                    @ApiResponse(
                            description = "Internal server error",
                            responseCode = "500"
                    )
            }
    )
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userToSaveDto) {
        User newUser = null;
        try {
            newUser = userMapper.userDtoToUser(userToSaveDto);
            newUser = userService.createElement(newUser);
            return new ResponseEntity<UserDTO>(userMapper.userToUserDto(newUser), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Operation(
            description = "Update user by ID",
            summary = "Update user by ID",
            responses = {
                    @ApiResponse(
                            description = "Ok",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "User not found",
                            responseCode = "404"
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("id") String userId, @RequestBody UserDTO userDto) {
        userDto.setId(userId);
        User user = null;
        try {
            user = userMapper.userDtoToUser(userDto);
            user = userService.updateUserById(user);
            return new ResponseEntity<UserDTO>(userMapper.userToUserDto(user), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
