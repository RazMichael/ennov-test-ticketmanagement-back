package test.ennov.ticketmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import test.ennov.ticketmanagement.model.dto.TicketDTO;
import test.ennov.ticketmanagement.model.dto.UserDTO;
import test.ennov.ticketmanagement.model.entity.Ticket;
import test.ennov.ticketmanagement.model.entity.User;
import test.ennov.ticketmanagement.service.UserServiceImpl;
import test.ennov.ticketmanagement.utils.exceptions.NoDataFoundException;
import test.ennov.ticketmanagement.utils.exceptions.UserNotFoundException;
import test.ennov.ticketmanagement.utils.mapper.TicketMapper;
import test.ennov.ticketmanagement.utils.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserServiceImpl userService;
    @MockBean
    private UserMapper userMapper;
    @MockBean
    private TicketMapper ticketMapper;
    private ObjectMapper objectMapper;
    private Ticket ticket;
    @Mock
    private TicketDTO ticketDTO;
    private List<User> userList;
    private List<UserDTO> userDtoList;
    private User user;
    @Mock
    private UserDTO userDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        ticket = new Ticket();
        ticketDTO = new TicketDTO();
        userList = new ArrayList<User>();
        userDtoList = new ArrayList<UserDTO>();

        user = new User();
        user.setId("userTestID1");
        user.setUsername("username1");
        user.setEmail("user1@user.com");

        User userTemp = new User();
        userTemp.setId("user1");
        userTemp.setUsername("username1");
        userTemp.setEmail("user1@user.com");
        userList.add(userTemp);

        userTemp = new User();
        userTemp.setId("user2");
        userTemp.setUsername("username2");
        userTemp.setEmail("user2@user.com");
        userList.add(userTemp);

        userTemp = new User();
        userTemp.setId("user3");
        userTemp.setUsername("username3");
        userTemp.setEmail("user3@user.com");
        userList.add(userTemp);

        user = userList.get(0);
    }

    @Test
    void getAllUsers() throws Exception {
        given(userService.getAllElements()).willReturn(userList);

        ResultActions response = mockMvc.perform(get("/users"));

        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getAllUsers_noDataFoundException() throws Exception {
        given(userService.getAllElements()).willThrow(NoDataFoundException.class);

        ResultActions response = mockMvc.perform(get("/users"));

        response.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getUserTickets() throws Exception {
        given(userService.getUserTickets(any())).willReturn(user.getTickets());

        ResultActions response = mockMvc.perform(get("/users/"+ user.getId() +"/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ticketDTO)));

        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getUserTickets_UserNotFoundException() throws Exception {
        given(userService.getUserTickets(any())).willThrow(UserNotFoundException.class);

        ResultActions response = mockMvc.perform(get("/users/"+ user.getId() +"/tickets"));

        response.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void createUser() throws Exception {
        given(userService.createElement(any())).willReturn(user);

        ResultActions response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)));

        response.andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void updateUser() throws Exception {
        given(userMapper.userDtoToUser(any())).willReturn(user);
        given(userService.updateUserById(any())).willReturn(user);
        given(userMapper.userToUserDto(any())).willReturn(userDto);

        ResultActions response = mockMvc.perform(put("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)));

        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void updateUser_UserNotFoundException() throws Exception {
        given(userMapper.userDtoToUser(any())).willThrow(UserNotFoundException.class);

        ResultActions response = mockMvc.perform(put("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)));

        response.andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}