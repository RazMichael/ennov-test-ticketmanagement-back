package test.ennov.ticketmanagement.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;

@Entity
@Data
@Table(name = "user")
public class User {
    @Id
    @UuidGenerator
    private String id;
    private String username;
    private String email;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private List<Ticket> tickets;

}
