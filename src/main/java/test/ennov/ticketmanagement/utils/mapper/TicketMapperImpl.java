package test.ennov.ticketmanagement.utils.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import test.ennov.ticketmanagement.model.dto.TicketDTO;
import test.ennov.ticketmanagement.model.entity.Ticket;
import test.ennov.ticketmanagement.service.UserService;
import test.ennov.ticketmanagement.utils.exceptions.UserNotFoundException;

import java.util.List;

@Component
public class TicketMapperImpl implements TicketMapper {

    @Autowired
    private UserService userService;

    @Override
    public Ticket ticketDtoToTicket(TicketDTO ticketDto) throws UserNotFoundException {
        Ticket ticket = null;
        if (ticketDto != null) {
            ticket = new Ticket();
            ticket.setId(ticketDto.getId());
            ticket.setTitle(ticketDto.getTitle());
            ticket.setStatus(ticketDto.getStatus());
            ticket.setDescription(ticketDto.getDescription());
            ticket.setUser(userService.getUserById(ticketDto.getUserId()));
        }
        return ticket;
    }

    @Override
    public TicketDTO ticketToTicketDto(Ticket ticket) {
        TicketDTO ticketDto = null;
        if (ticket != null) {
            ticketDto = new TicketDTO();
            ticketDto.setId(ticket.getId());
            ticketDto.setTitle(ticket.getTitle());
            ticketDto.setStatus(ticket.getStatus());
            ticketDto.setDescription(ticket.getDescription());
            ticketDto.setUserId(ticket.getUser().getId());
        }
        return ticketDto;
    }

    @Override
    public List<TicketDTO> mapListToDto(List<Ticket> tickets) {
        List<TicketDTO> ticketsDto = tickets.stream().map(this::ticketToTicketDto).toList();
        return ticketsDto;
    }
}
