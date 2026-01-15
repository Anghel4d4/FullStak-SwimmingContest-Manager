// File: Problema3/src/main/java/ro/mpp2025/Repository/UserRepository.java
package ro.mpp2025.Repository.Interface;

import ro.mpp2025.Domain.User;

import java.util.Optional;

public interface IUserRepository extends Repository<Long, User> {
    Optional<User> findByUsername(String username);
}