package test.ennov.ticketmanagement.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

@Data
@Entity
@Table(name = "ticket")
public class Ticket {
    @Id
    @UuidGenerator
    private String id;
    private String title;
    private String description;
    private TicketStatusEnum status;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "user_creator_id", nullable = false)
    private User createdByUser;
}
