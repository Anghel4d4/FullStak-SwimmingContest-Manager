

import Utils.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.mpp2025.Domain.Proba;
import ro.mpp2025.Repository.ProbaRepository;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class ProbaTest {
    private ProbaRepository probaRepository;
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
        } catch (Exception e) {
            fail("Error loading properties: " + e.getMessage());
        }
        jdbcUtils = new JdbcUtils(props);
        try (Connection con = jdbcUtils.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM Proba")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            fail("Failed to clear Proba table: " + e.getMessage());
        }
        probaRepository = new ProbaRepository(jdbcUtils);
    }

    @AfterEach
    public void tearDown() {
        try (Connection con = jdbcUtils.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM Proba")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            fail("TearDown failed: " + e.getMessage());
        }
    }

    @Test
    public void testAddUpdateDeleteProba() {
        Proba proba = new Proba(1L, "100m", "Freestyle");
        Optional<Proba> saved = probaRepository.save(proba);
        assertTrue(saved.isEmpty(), "Expected proba to be saved.");

        Proba updated = new Proba(1L, "200m", "Butterfly");
        Optional<Proba> updateRes = probaRepository.update(updated);
        assertTrue(updateRes.isEmpty(), "Expected update to succeed.");

        Optional<Proba> found = probaRepository.findOne(1L);
        assertTrue(found.isPresent(), "Expected proba to be found.");
        assertEquals("200m", found.get().getDistanta(), "Expected distance to be updated.");

        Optional<Proba> deleted = probaRepository.delete(1L);
        assertTrue(deleted.isPresent(), "Expected proba to be deleted.");

        Optional<Proba> notFound = probaRepository.findOne(1L);
        assertTrue(notFound.isEmpty(), "Proba should not exist.");
    }
}