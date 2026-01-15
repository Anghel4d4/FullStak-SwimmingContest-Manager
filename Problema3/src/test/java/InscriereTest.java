
import Utils.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.mpp2025.Domain.Inscriere;
import ro.mpp2025.Domain.Participant;
import ro.mpp2025.Domain.Proba;
import ro.mpp2025.Repository.InscriereRepository;
import ro.mpp2025.Repository.ParticipantRepository;
import ro.mpp2025.Repository.ProbaRepository;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class InscriereTest {
    private JdbcUtils jdbcUtils;
    private ParticipantRepository participantRepo;
    private ProbaRepository probaRepo;
    private InscriereRepository inscriereRepo;

    @BeforeEach
    public void setUp() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("bd.config")) {
            if (input == null) {
                fail("Could not load bd.config from classpath.");
            }
            props.load(input);
        } catch (Exception e) {
            fail("Error loading properties: " + e.getMessage());
        }
        jdbcUtils = new JdbcUtils(props);

        try (Connection con = jdbcUtils.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM Inscriere")) {
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM Participant")) {
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM Proba")) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            fail("Failed to clear tables: " + e.getMessage());
        }
        participantRepo = new ParticipantRepository(jdbcUtils);
        probaRepo = new ProbaRepository(jdbcUtils);
        inscriereRepo = new InscriereRepository(jdbcUtils, participantRepo, probaRepo);

        Participant participant = new Participant(1L, "Doe", "Jane", "28");
        Optional<Participant> partRes = participantRepo.save(participant);
        assertTrue(partRes.isEmpty(), "Participant should be saved.");

        Proba proba = new Proba(1L, "100m", "Freestyle");
        Optional<Proba> probaRes = probaRepo.save(proba);
        assertTrue(probaRes.isEmpty(), "Proba should be saved.");
    }

    @AfterEach
    public void tearDown() {
        try (Connection con = jdbcUtils.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM Inscriere")) {
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM Participant")) {
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM Proba")) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            fail("TearDown failed: " + e.getMessage());
        }
    }

    @Test
    public void testAddUpdateDeleteInscriere() {

        Inscriere inscriere = new Inscriere(1L, participantRepo.findOne(1L).get(), probaRepo.findOne(1L).get());
        Optional<Inscriere> saved = inscriereRepo.save(inscriere);
        assertTrue(saved.isEmpty(), "Expected inscriere to be saved.");

        Participant newParticipant = new Participant(2L, "Smith", "Anna", "32");
        Optional<Participant> newPartRes = participantRepo.save(newParticipant);
        assertTrue(newPartRes.isEmpty(), "New participant should be saved.");

        Proba newProba = new Proba(2L, "200m", "Butterfly");
        Optional<Proba> newProbaRes = probaRepo.save(newProba);
        assertTrue(newProbaRes.isEmpty(), "New proba should be saved.");


        Inscriere updated = new Inscriere(1L, newParticipant, newProba);
        Optional<Inscriere> updateRes = inscriereRepo.update(updated);
        assertTrue(updateRes.isEmpty(), "Expected update to succeed.");

        Optional<Inscriere> found = inscriereRepo.findOne(1L);
        assertTrue(found.isPresent(), "Expected inscriere to be found.");
        assertEquals(newParticipant.getId(), found.get().getParticipant().getId(), "Participant should be updated.");
        assertEquals(newProba.getId(), found.get().getProba().getId(), "Proba should be updated.");


        Optional<Inscriere> deleted = inscriereRepo.delete(1L);
        assertTrue(deleted.isPresent(), "Expected inscriere to be deleted.");

        Optional<Inscriere> notFound = inscriereRepo.findOne(1L);
        assertTrue(notFound.isEmpty(), "Inscriere should no longer exist.");
    }
}