// File: Problema3/src/main/java/ro/mpp2025/Repository/UserRepository.java
package main.swimmingpersistance.Interface;


import main.swimmingmodel.User;

import java.util.Optional;

public interface IUserRepository extends Repository<Long, User> {
    Optional<User> findByUsername(String username);
}