
import Utils.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.mpp2025.Domain.Participant;
import ro.mpp2025.Repository.ParticipantRepository;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class ParticipantTest {
    private ParticipantRepository participantRepository;
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
             PreparedStatement ps = con.prepareStatement("DELETE FROM Participant")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            fail("Failed to clear Participant table: " + e.getMessage());
        }
        participantRepository = new ParticipantRepository(jdbcUtils);
    }

    @AfterEach
    public void tearDown() {
        try (Connection con = jdbcUtils.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM Participant")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            fail("TearDown failed: " + e.getMessage());
        }
    }

    @Test
    public void testAddUpdateDeleteParticipant() {
        Participant participant = new Participant(1L, "Doe", "John", "25");
        Optional<Participant> saved = participantRepository.save(participant);
        assertTrue(saved.isEmpty(), "Expected participant to be saved.");

        Participant updated = new Participant(1L, "Smith", "John", "30");
        Optional<Participant> updateRes = participantRepository.update(updated);
        assertTrue(updateRes.isEmpty(), "Expected update to succeed.");

        Optional<Participant> found = participantRepository.findOne(1L);
        assertTrue(found.isPresent(), "Expected participant to be found.");
        assertEquals("Smith", found.get().getNume(), "Expected name to be updated.");

        Optional<Participant> deleted = participantRepository.delete(1L);
        assertTrue(deleted.isPresent(), "Expected participant to be deleted.");

        Optional<Participant> notFound = participantRepository.findOne(1L);
        assertTrue(notFound.isEmpty(), "Participant should no longer exist.");
    }
}