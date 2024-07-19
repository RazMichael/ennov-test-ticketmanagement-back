package test.ennov.ticketmanagement.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import test.ennov.ticketmanagement.model.entity.Ticket;
import test.ennov.ticketmanagement.model.entity.TicketStatusEnum;
import test.ennov.ticketmanagement.model.entity.User;
import test.ennov.ticketmanagement.repository.UserRepository;
import test.ennov.ticketmanagement.utils.exceptions.NoDataFoundException;
import test.ennov.ticketmanagement.utils.exceptions.UserNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private List<User> users;
    private List<Ticket> tickets;

    @BeforeEach
    void setUp() {
        users = new ArrayList<>();
        User user = new User();
        user.setId("userTestID1");
        user.setUsername("username1");
        user.setEmail("user1@user.com");
        users.add(user);

        user = new User();
        user.setId("userTestID2");
        user.setUsername("username2");
        user.setEmail("user2@user.com");
        users.add(user);

        tickets = new ArrayList<Ticket>();

        Ticket ticketTemp = new Ticket();
        ticketTemp.setUser(users.get(0));
        ticketTemp.setId("1");
        ticketTemp.setStatus(TicketStatusEnum.IN_PROGRESS);
        ticketTemp.setTitle("Ticket 1");
        ticketTemp.setCreatedByUser(users.get(0));
        tickets.add(ticketTemp);

        ticketTemp = new Ticket();
        ticketTemp.setUser(users.get(0));
        ticketTemp.setId("2");
        ticketTemp.setStatus(TicketStatusEnum.IN_PROGRESS);
        ticketTemp.setTitle("Ticket 2");
        ticketTemp.setCreatedByUser(users.get(0));
        tickets.add(ticketTemp);

        ticketTemp = new Ticket();
        ticketTemp.setUser(users.get(0));
        ticketTemp.setId("3");
        ticketTemp.setStatus(TicketStatusEnum.IN_PROGRESS);
        ticketTemp.setTitle("Ticket 3");
        ticketTemp.setCreatedByUser(users.get(0));
        tickets.add(ticketTemp);

        users.get(0).setTickets(tickets);
    }

    @Test
    void getAllUsers() throws NoDataFoundException {
        Mockito.when(userRepository.findAll())
                .thenReturn(users);

        List<User> usersResponse = userService.getAllElements();

        assertTrue(!usersResponse.isEmpty());
    }

    @Test
    void getUserTickets() throws UserNotFoundException {
        Mockito.when(userRepository.findById(any()))
                .thenReturn(Optional.ofNullable(users.get(0)));
        List<Ticket> tickets = userService.getUserTickets(any());

        assertTrue(!tickets.isEmpty());
    }

    @Test
    void getUserTickets_userNotFoundException() throws UserNotFoundException {
        Mockito.when(userRepository.findById(any()))
                .thenReturn(Optional.empty());

        AtomicReference<List<Ticket>> tickets = new AtomicReference<>(new ArrayList<>());
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> tickets.set(userService.getUserTickets(users.get(0).getId())),
                "Expected getUserTickets() to throw UserNotFoundException, but it didn't"
        );
        assertTrue(exception.getMessage().equals("User not found"));
        assertTrue(tickets.get().isEmpty());
    }

    @Test
    void createUser() {
        Mockito.when(userRepository.save(any()))
                .thenReturn(users.getFirst());

        User userSaved = userService.createElement(users.getFirst());

        assertTrue(userSaved != null);
        assertEquals(userSaved.getId(), users.getFirst().getId());
    }

    @Test
    void updateUserById() {
        Mockito.when(userRepository.save(any()))
                .thenReturn(users.getFirst());

        User userSaved = userService.updateUserById(users.getFirst());

        assertTrue(userSaved != null);
        assertEquals(userSaved.getId(), users.getFirst().getId());
    }

    @Test
    void getUserById() throws UserNotFoundException {
        Mockito.when(userRepository.findById(any()))
                .thenReturn(Optional.ofNullable(users.getFirst()));

        User user = userService.getUserById(users.getFirst().getId());

        assertTrue(user != null);
        assertEquals(user.getId(), users.getFirst().getId());
    }

    @Test
    void getUserById_userNotFoundException() throws UserNotFoundException {
        Mockito.when(userRepository.findById(any()))
                .thenReturn(Optional.empty());

        AtomicReference<User> user = new AtomicReference<>();
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> user.set(userService.getUserById(users.getFirst().getId())),
                "Expected getUserById() to throw UserNotFoundException, but it didn't"
        );
        assertTrue(exception.getMessage().equals("User not found by id " + users.getFirst().getId()));
        assertTrue(user.get() == null);
    }
}