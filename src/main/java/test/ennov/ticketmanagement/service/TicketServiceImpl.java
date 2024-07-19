package test.ennov.ticketmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.ennov.ticketmanagement.model.entity.Ticket;
import test.ennov.ticketmanagement.model.entity.User;
import test.ennov.ticketmanagement.repository.TicketRepository;
import test.ennov.ticketmanagement.repository.UserRepository;
import test.ennov.ticketmanagement.utils.exceptions.UserNotFoundException;
import test.ennov.ticketmanagement.utils.exceptions.NoDataFoundException;
import test.ennov.ticketmanagement.utils.exceptions.NoTicketAccessException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private UserRepository userRepository;

    public List<Ticket> getAllElements() throws NoDataFoundException {
        List<Ticket> tickets = ticketRepository.findAll();
        if (tickets.isEmpty()) {
            throw new NoDataFoundException("No Ticket(s) found");
        }
        return tickets;
    }

    @Override
    public Ticket getTicketById(String ticketId, String connectedUserId) throws NoDataFoundException, NoTicketAccessException {
        if (hasAccessOnTicket(ticketId, connectedUserId)) {
            return ticketRepository.findById(ticketId).orElseThrow(() -> new NoDataFoundException("No Ticket found by this id"));
        } else {
            throw new NoTicketAccessException("User " + connectedUserId + " has no access to ticket " + ticketId + " or ticket does not exist");
        }
    }

    @Override
    public Ticket createElement(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket updateTicketById(Ticket ticket, String connectedUserId) throws NoDataFoundException, NoTicketAccessException {
        if (hasAccessOnTicket(ticket.getId(), connectedUserId)) {
            Ticket ticketSaved = ticketRepository.save(ticket);
            if (ticketSaved != null) {
                return ticketSaved;
            } else {
                throw new NoDataFoundException("Ticket id " + ticket.getId() + " not found");
            }
        } else {
            throw new NoTicketAccessException("User " + connectedUserId + " has no access to ticket " + ticket.getId() + " or ticket does not exist");
        }
    }

    @Override
    @Transactional
    public Ticket assignTicketToUser(String ticketId, String newTicketAssigneeId, String connectedUserId) throws UserNotFoundException, NoDataFoundException, NoTicketAccessException {
        if (hasAccessOnTicket(ticketId, connectedUserId)) {
            Ticket ticket = this.getTicketById(ticketId, connectedUserId);
            User previousAssignee = ticket.getUser();
            Optional<User> newAssignee = userRepository.findById(newTicketAssigneeId);

            if (newAssignee.isPresent()) {
                // Update the ticket user
                ticket.setUser(newAssignee.get());

                // Unassign ticket from previous assignee
                List<Ticket> userTicketsAfterUnassignment = previousAssignee.getTickets().stream()
                        .filter(ticket1 -> ticket.getId() != ticketId).collect(Collectors.toList());
                previousAssignee.setTickets(userTicketsAfterUnassignment);
                userRepository.save(previousAssignee);

                // Assign ticket to new assignee
                newAssignee.ifPresent(user -> {
                    user.getTickets().add(ticket);
                    userRepository.save(user);
                });
            } else {
                throw new UserNotFoundException("New assignee (user) not found - id : " + newTicketAssigneeId);
            }
            return ticket;
        } else {
            throw new NoTicketAccessException("User " + connectedUserId + " has no access to ticket " + ticketId + " or ticket does not exist");
        }
    }

    @Override
    public void deleteTicketById(String ticketId, String connectedUserId) throws EmptyResultDataAccessException, NoTicketAccessException {
        if (hasAccessOnTicket(ticketId, connectedUserId)) {
            ticketRepository.deleteById(ticketId);
        } else {
            throw new NoTicketAccessException("User " + connectedUserId + " has no access to ticket " + ticketId + " or ticket does not exist");
        }
    }

    @Override
    public Boolean hasAccessOnTicket(String ticketId, String userId) {
        return ticketRepository.existsByIdAndUserIdOrCreatedByUser(ticketId, userId, userId);
    }
}
