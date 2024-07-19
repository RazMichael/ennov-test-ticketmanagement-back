package test.ennov.ticketmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import test.ennov.ticketmanagement.model.entity.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, String> {

    @Query("SELECT count(ticket) > 0 from Ticket as ticket where ticket.id = :ticketId and (ticket.user.id = :assigneeId or ticket.createdByUser.id = :userCreatorId)")
    Boolean existsByIdAndUserIdOrCreatedByUser(String ticketId, String assigneeId, String userCreatorId);
}
