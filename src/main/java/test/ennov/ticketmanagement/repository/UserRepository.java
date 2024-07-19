package test.ennov.ticketmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import test.ennov.ticketmanagement.model.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

}
