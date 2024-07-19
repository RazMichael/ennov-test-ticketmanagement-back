package test.ennov.ticketmanagement.service;

import test.ennov.ticketmanagement.model.entity.Ticket;
import test.ennov.ticketmanagement.model.entity.User;
import test.ennov.ticketmanagement.utils.exceptions.UserNotFoundException;

import java.util.List;

public interface UserService extends GenericService<User> {
    List<Ticket> getUserTickets(String userId) throws UserNotFoundException;
    User updateUserById(User user);
    User getUserById(String userId) throws UserNotFoundException;
}
