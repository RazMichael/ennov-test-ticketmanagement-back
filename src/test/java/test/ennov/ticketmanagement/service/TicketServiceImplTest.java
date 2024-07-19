package test.ennov.ticketmanagement.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import test.ennov.ticketmanagement.model.entity.Ticket;
import test.ennov.ticketmanagement.model.entity.TicketStatusEnum;
import test.ennov.ticketmanagement.model.entity.User;
import test.ennov.ticketmanagement.repository.TicketRepository;
import test.ennov.ticketmanagement.repository.UserRepository;
import test.ennov.ticketmanagement.utils.exceptions.NoDataFoundException;
import test.ennov.ticketmanagement.utils.exceptions.NoTicketAccessException;
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
class TicketServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TicketRepository ticketRepository;
    @InjectMocks
    private TicketServiceImpl ticketService;
    @Mock
    private User user;
    @Mock
    private List<Ticket> tickets;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("userTestID1");
        user.setUsername("username1");
        user.setEmail("user1@user.com");

        tickets = new ArrayList<Ticket>();

        Ticket ticketTemp = new Ticket();
        ticketTemp.setUser(user);
        ticketTemp.setId("1");
        ticketTemp.setStatus(TicketStatusEnum.IN_PROGRESS);
        ticketTemp.setTitle("Ticket 1");
        ticketTemp.setCreatedByUser(user);
        tickets.add(ticketTemp);

        ticketTemp = new Ticket();
        ticketTemp.setUser(user);
        ticketTemp.setId("2");
        ticketTemp.setStatus(TicketStatusEnum.IN_PROGRESS);
        ticketTemp.setTitle("Ticket 2");
        ticketTemp.setCreatedByUser(user);
        tickets.add(ticketTemp);

        ticketTemp = new Ticket();
        ticketTemp.setUser(user);
        ticketTemp.setId("3");
        ticketTemp.setStatus(TicketStatusEnum.IN_PROGRESS);
        ticketTemp.setTitle("Ticket 3");
        ticketTemp.setCreatedByUser(user);
        tickets.add(ticketTemp);

        user.setTickets(tickets);
    }

    @Test
    void getAllTickets() throws NoDataFoundException {
        Mockito.when(ticketRepository.findAll())
                .thenReturn(tickets);

        List<Ticket> ticketsResponse = ticketService.getAllElements();

        assertTrue(!ticketsResponse.isEmpty());
        assertTrue(ticketsResponse.size() == 3);
    }

    @Test
    void getAllTicketsKo_NoDataFoundException() {
        Mockito.when(ticketRepository.findAll())
                .thenReturn(new ArrayList<Ticket>());

        AtomicReference<List<Ticket>> ticketsResponse = new AtomicReference<>();
        NoDataFoundException exception = assertThrows(
                NoDataFoundException.class,
                () -> ticketsResponse.set(ticketService.getAllElements()),
                "Expected getAllTickets() to throw NoDataFoundException, but it didn't"
        );
        assertTrue(exception.getMessage().equals("No Ticket(s) found"));
        assertTrue(ticketsResponse.get() == null);
    }

    @Test
    void getTicketById() throws NoDataFoundException, NoTicketAccessException {
        Mockito.when(ticketRepository.findById(any()))
                .thenReturn(Optional.ofNullable(tickets.get(0)));
        Mockito.when(ticketRepository.existsByIdAndUserIdOrCreatedByUser(any(), any(), any()))
                .thenReturn(true);

        Ticket ticket = ticketService.getTicketById("", "");

        assertTrue(ticket != null);
        assertEquals(ticket.getId(), tickets.get(0).getId());
    }

    @Test
    void getTicketById_noDataFoundException() {
        Mockito.when(ticketRepository.findById(any()))
                .thenReturn(Optional.empty());
        Mockito.when(ticketRepository.existsByIdAndUserIdOrCreatedByUser(any(), any(), any()))
                .thenReturn(true);

        AtomicReference<Ticket> ticket = new AtomicReference<>();
        NoDataFoundException exception = assertThrows(
                NoDataFoundException.class,
                () -> ticket.set(ticketService.getTicketById("", "")),
                "Expected getTicketById() to throw NoDataFoundException, but it didn't"
        );
        assertTrue(exception.getMessage().equals("No Ticket found by this id"));
        assertTrue(ticket.get() == null);
    }

    @Test
    void getTicketById_noTicketAccessException() {
        String ticketId = "ticketId";
        String connectedUserId = "connectedUserId";

        Mockito.when(ticketRepository.existsByIdAndUserIdOrCreatedByUser(any(), any(), any()))
                .thenReturn(false);

        AtomicReference<Ticket> ticket = new AtomicReference<>();
        NoTicketAccessException exception = assertThrows(
                NoTicketAccessException.class,
                () -> ticket.set(ticketService.getTicketById(ticketId, connectedUserId)),
                "Expected getTicketById() to throw NoTicketAccessException, but it didn't"
        );
        assertTrue(exception.getMessage().equals("User " + connectedUserId + " has no access to ticket " + ticketId + " or ticket does not exist"));
        assertTrue(ticket.get() == null);
    }

    @Test
    void createTicket() {
        Mockito.when(ticketRepository.save(any()))
                .thenReturn(tickets.get(0));
        Ticket ticketSaved = ticketService.createElement(tickets.get(0));

        assertEquals(ticketSaved.getCreatedByUser().getId(), tickets.get(0).getCreatedByUser().getId());
        assertEquals(ticketSaved.getUser().getId(), tickets.get(0).getUser().getId());
        assertEquals(ticketSaved.getTitle(), tickets.get(0).getTitle());
        assertEquals(ticketSaved.getStatus(), tickets.get(0).getStatus());
    }

    @Test
    void updateTicketById() throws NoTicketAccessException, NoDataFoundException {
        Mockito.when(ticketRepository.save(any()))
                .thenReturn(tickets.get(0));
        Mockito.when(ticketRepository.existsByIdAndUserIdOrCreatedByUser(any(), any(), any()))
                .thenReturn(true);

        Ticket ticketUpdated = ticketService.updateTicketById(tickets.get(0), user.getId());

        assertEquals(ticketUpdated.getCreatedByUser().getId(), tickets.get(0).getCreatedByUser().getId());
        assertEquals(ticketUpdated.getUser().getId(), tickets.get(0).getUser().getId());
        assertEquals(ticketUpdated.getTitle(), tickets.get(0).getTitle());
        assertEquals(ticketUpdated.getStatus(), tickets.get(0).getStatus());
    }

    @Test
    void updateTicketById_noDataFoundException() {
        Mockito.when(ticketRepository.save(tickets.get(0)))
                .thenReturn(null);
        Mockito.when(ticketRepository.existsByIdAndUserIdOrCreatedByUser(any(), any(), any()))
                .thenReturn(true);

        AtomicReference<Ticket> ticket = new AtomicReference<>();
        NoDataFoundException exception = assertThrows(
                NoDataFoundException.class,
                () -> ticket.set(ticketService.updateTicketById(tickets.get(0), user.getId())),
                "Expected updateTicketById() to throw NoDataFoundException, but it didn't"
        );
        assertTrue(exception.getMessage().equals("Ticket id " + tickets.get(0).getId() + " not found"));
        assertTrue(ticket.get() == null);
    }

    @Test
    void updateTicketById_noTicketAccessException() {
        Mockito.when(ticketRepository.existsByIdAndUserIdOrCreatedByUser(any(), any(), any()))
                .thenReturn(false);

        AtomicReference<Ticket> ticket = new AtomicReference<>();
        NoTicketAccessException exception = assertThrows(
                NoTicketAccessException.class,
                () -> ticket.set(ticketService.updateTicketById(tickets.get(0), user.getId())),
                "Expected updateTicketById() to throw NoTicketAccessException, but it didn't"
        );
        assertTrue(exception.getMessage().equals("User " + user.getId() + " has no access to ticket " + tickets.get(0).getId() + " or ticket does not exist"));
        assertTrue(ticket.get() == null);
    }

    @Test
    void assignTicketToUser() throws NoTicketAccessException, UserNotFoundException, NoDataFoundException {
        User userNewAssignee = new User();
        userNewAssignee.setId("userNewAssigneeId");
        userNewAssignee.setUsername("userNewAssignee");
        tickets.get(0).setUser(user);
        userNewAssignee.setTickets(new ArrayList<Ticket>());

        Mockito.when(ticketRepository.existsByIdAndUserIdOrCreatedByUser(any(), any(), any()))
                .thenReturn(true);
        Mockito.when(ticketRepository.findById(tickets.get(0).getId()))
                .thenReturn(Optional.ofNullable(tickets.get(0)));
        Mockito.when(userRepository.findById(userNewAssignee.getId()))
                .thenReturn(Optional.of(userNewAssignee));

        Ticket ticketAssigned = ticketService.assignTicketToUser(tickets.get(0).getId(), userNewAssignee.getId(), user.getId());

        assertEquals(ticketAssigned.getUser().getId(), userNewAssignee.getId());
        assertEquals(userNewAssignee.getTickets().get(0).getId(), ticketAssigned.getId());
    }

    @Test
    void assignTicketToUser_noDataFoundException() {
        User userNewAssignee = new User();
        userNewAssignee.setId("userNewAssigneeId");
        userNewAssignee.setUsername("userNewAssignee");
        tickets.get(0).setUser(user);
        userNewAssignee.setTickets(new ArrayList<Ticket>());

        Mockito.when(ticketRepository.existsByIdAndUserIdOrCreatedByUser(any(), any(), any()))
                .thenReturn(true);
        Mockito.when(ticketRepository.findById(tickets.get(0).getId()))
                .thenReturn(Optional.empty());

        AtomicReference<Ticket> ticket = new AtomicReference<>();
        NoDataFoundException exception = assertThrows(
                NoDataFoundException.class,
                () -> ticket.set(ticketService.assignTicketToUser(tickets.get(0).getId(), userNewAssignee.getId(), user.getId())),
                "Expected assignTicketToUser() to throw NoDataFoundException, but it didn't"
        );
        assertTrue(exception.getMessage().equals("No Ticket found by this id"));
        assertEquals(ticket.get(), null);
    }

    @Test
    void assignTicketToUser_userNotFoundException() {
        User userNewAssignee = new User();
        userNewAssignee.setId("userNewAssigneeId");
        userNewAssignee.setUsername("userNewAssignee");
        tickets.get(0).setUser(user);
        userNewAssignee.setTickets(new ArrayList<Ticket>());

        Mockito.when(ticketRepository.existsByIdAndUserIdOrCreatedByUser(any(), any(), any()))
                .thenReturn(true);
        Mockito.when(ticketRepository.findById(tickets.get(0).getId()))
                .thenReturn(Optional.ofNullable(tickets.get(0)));
        Mockito.when(userRepository.findById(userNewAssignee.getId()))
                .thenReturn(Optional.empty());

        AtomicReference<Ticket> ticket = new AtomicReference<>();
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> ticket.set(ticketService.assignTicketToUser(tickets.get(0).getId(), userNewAssignee.getId(), user.getId())),
                "Expected assignTicketToUser() to throw UserNotFoundException, but it didn't"
        );
        assertTrue(exception.getMessage().equals("New assignee (user) not found - id : " + userNewAssignee.getId()));
        assertEquals(ticket.get(), null);
    }

    @Test
    void assignTicketToUser_noTicketAccessException() {
        User userNewAssignee = new User();
        userNewAssignee.setId("userNewAssigneeId");
        userNewAssignee.setUsername("userNewAssignee");
        tickets.get(0).setUser(user);
        userNewAssignee.setTickets(new ArrayList<Ticket>());

        Mockito.when(ticketRepository.existsByIdAndUserIdOrCreatedByUser(any(), any(), any()))
                .thenReturn(false);

        AtomicReference<Ticket> ticket = new AtomicReference<>();
        NoTicketAccessException exception = assertThrows(
                NoTicketAccessException.class,
                () -> ticket.set(ticketService.assignTicketToUser(tickets.get(0).getId(), userNewAssignee.getId(), user.getId())),
                "Expected assignTicketToUser() to throw NoTicketAccessException, but it didn't"
        );
        assertTrue(exception.getMessage().equals("User " + user.getId() + " has no access to ticket " + tickets.get(0).getId() + " or ticket does not exist"));
        assertEquals(ticket.get(), null);
    }

    @Test
    void deleteTicketById_NoTicketAccessException() {
        Mockito.when(ticketRepository.existsByIdAndUserIdOrCreatedByUser(any(), any(), any()))
                .thenReturn(false);

        NoTicketAccessException exception = assertThrows(
                NoTicketAccessException.class,
                () -> ticketService.deleteTicketById(tickets.get(0).getId(), user.getId()),
                "Expected deleteTicketById() to throw NoTicketAccessException, but it didn't"
        );

        assertTrue(exception.getMessage().equals("User " + user.getId() + " has no access to ticket " + tickets.get(0).getId() + " or ticket does not exist"));
    }

    @Test
    void deleteTicketById_EmptyResultDataAccessException() {
        Mockito.when(ticketRepository.existsByIdAndUserIdOrCreatedByUser(any(), any(), any()))
                .thenReturn(true);
        Mockito.doThrow(EmptyResultDataAccessException.class).doNothing().when(ticketRepository).deleteById(any());

        assertThrows(
                EmptyResultDataAccessException.class,
                () -> ticketService.deleteTicketById(tickets.get(0).getId(), user.getId()),
                "Expected deleteTicketById() to throw EmptyResultDataAccessException, but it didn't"
        );
    }
}