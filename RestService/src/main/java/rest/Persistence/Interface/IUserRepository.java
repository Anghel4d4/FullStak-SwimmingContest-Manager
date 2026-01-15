package rest.Persistence.Interface;



import rest.Model.User;

import java.util.Optional;

public interface IUserRepository extends Repository<Long, User> {
    Optional<User> findByUsername(String username);
}