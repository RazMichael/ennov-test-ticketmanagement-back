package test.ennov.ticketmanagement.service;

import org.springframework.dao.EmptyResultDataAccessException;
import test.ennov.ticketmanagement.model.entity.Ticket;
import test.ennov.ticketmanagement.utils.exceptions.UserNotFoundException;
import test.ennov.ticketmanagement.utils.exceptions.NoDataFoundException;
import test.ennov.ticketmanagement.utils.exceptions.NoTicketAccessException;

public interface TicketService extends GenericService<Ticket> {
    Ticket getTicketById(String ticketId, String connectedUserId) throws NoDataFoundException, NoTicketAccessException;
    Ticket updateTicketById(Ticket ticket, String connectedUserId) throws NoDataFoundException, NoTicketAccessException;
    Ticket assignTicketToUser(String ticketId, String newTicketAssigneeId, String connectedUserId) throws UserNotFoundException, NoDataFoundException, NoTicketAccessException;
    void deleteTicketById(String ticketId, String connectedUserId) throws EmptyResultDataAccessException, NoTicketAccessException;
    Boolean hasAccessOnTicket(String ticketId, String userId);
}
