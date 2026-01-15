package ro.mpp2025.Service;

import ro.mpp2025.Domain.User;
import ro.mpp2025.Repository.UserRepository;
import java.util.Optional;

public class UserService {
    private final UserRepository userRepository;


    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public Optional<User> authenticate(String username, String password){
        Optional<User> user = userRepository.findByUsername(username);
        return user.filter(u -> u.getPassword().equals(password));
    }

}
