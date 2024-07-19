package test.ennov.ticketmanagement.utils.mapper;

import test.ennov.ticketmanagement.model.dto.TicketDTO;
import test.ennov.ticketmanagement.model.entity.Ticket;
import test.ennov.ticketmanagement.utils.exceptions.UserNotFoundException;

import java.util.List;

public interface TicketMapper {
    Ticket ticketDtoToTicket(TicketDTO ticketDto) throws UserNotFoundException;
    TicketDTO ticketToTicketDto(Ticket ticket);
    List<TicketDTO> mapListToDto(List<Ticket> tickets);
}
