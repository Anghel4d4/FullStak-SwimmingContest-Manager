// File: src/test/java/UserTest.java
import Utils.JdbcUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import ro.mpp2025.Domain.User;
import ro.mpp2025.Repository.UserRepository;

import java.io.InputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private UserRepository userRepo;
    private JdbcUtils jdbcUtils;
    private Properties props;

    @BeforeEach
    public void setUp() {
        props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("bd.config")) {
            if (input == null) {
                fail("Could not load bd.config from classpath.");
            }
            props.load(input);
        } catch (IOException e) {
            fail("Could not load bd.config: " + e.getMessage());
        }
        jdbcUtils = new JdbcUtils(props);

        try (Connection con = jdbcUtils.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM User")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            fail("setUp: Unable to clear User table: " + e.getMessage());
        }
        userRepo = new UserRepository(jdbcUtils);
    }

    @AfterEach
    public void tearDown() {
        try (Connection con = jdbcUtils.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM User")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            fail("TearDown failed: " + e.getMessage());
        }
    }

    @Test
    public void testAddUpdateDeleteUser() {
        User user1 = new User(1L, "demoUser", "demoPass");
        Optional<User> savedUser1 = userRepo.save(user1);
        assertTrue(savedUser1.isEmpty(), "Expected first user to be saved");

        User user2 = new User(2L, "secondUser", "secondPass");
        Optional<User> savedUser2 = userRepo.save(user2);
        assertTrue(savedUser2.isEmpty(), "Expected second user to be saved");

        User updatedUser1 = new User(1L, "updatedUser", "updatedPass");
        Optional<User> updateResult = userRepo.update(updatedUser1);
        assertTrue(updateResult.isEmpty(), "Expected update to be successful");

        Optional<User> fetchedUser1 = userRepo.findOne(1L);
        assertTrue(fetchedUser1.isPresent(), "First user should exist");
        assertEquals("updatedUser", fetchedUser1.get().getUsername(), "Username should be updated");

        Optional<User> deletedUser = userRepo.delete(2L);
        assertTrue(deletedUser.isPresent(), "Second user should be deleted");

        Optional<User> fetchedUser2 = userRepo.findOne(2L);
        assertTrue(fetchedUser2.isEmpty(), "Second user should no longer exist");
    }
}