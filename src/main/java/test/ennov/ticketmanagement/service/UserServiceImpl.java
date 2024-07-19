package test.ennov.ticketmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.ennov.ticketmanagement.model.entity.Ticket;
import test.ennov.ticketmanagement.model.entity.User;
import test.ennov.ticketmanagement.repository.UserRepository;
import test.ennov.ticketmanagement.utils.exceptions.NoDataFoundException;
import test.ennov.ticketmanagement.utils.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getAllElements() throws NoDataFoundException {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new NoDataFoundException("No User(s) found");
        }
        return users;
    }

    @Override
    public List<Ticket> getUserTickets(String userId) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            if (!user.get().getTickets().isEmpty()) {
                return user.get().getTickets();
            }
        } else {
            throw new UserNotFoundException("User not found");
        }
        return null;
    }

    @Override
    public User createElement(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public User updateUserById(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public User getUserById(String userId) throws UserNotFoundException {
        return this.userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found by id " + userId));
    }
}
