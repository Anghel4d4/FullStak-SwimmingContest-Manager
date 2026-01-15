package ro.mpp2025.Repository;

import Utils.JdbcUtils;
import ro.mpp2025.Domain.Participant;
import ro.mpp2025.Repository.Interface.IParticipantRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ParticipantRepository extends AbstractRepository<Long, Participant> implements IParticipantRepository {

    public ParticipantRepository(JdbcUtils jdbc) {
        super(jdbc, "Participant");
    }

    @Override
    public Participant extractEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String nume = resultSet.getString("Nume");
        String prenume = resultSet.getString("Prenume");
        String varsta = resultSet.getString("Varsta");
        return new Participant(id, nume, prenume, varsta);
    }

    @Override
    public void setEntitySave(PreparedStatement statement, Participant entity) throws SQLException {
        statement.setString(1, entity.getNume());
        statement.setString(2, entity.getPrenume());
        statement.setString(3, entity.getVarsta());
    }

    @Override
    public void setEntityUpdate(PreparedStatement statement, Participant entity) throws SQLException {
        statement.setString(1, entity.getNume());
        statement.setString(2, entity.getPrenume());
        statement.setString(3, entity.getVarsta());
        statement.setLong(4, entity.getId());
    }

    @Override
    public String getInsert() {
        return "INSERT INTO Participant ( Nume, Prenume, Varsta) VALUES (?,?,?)";
    }

    @Override
    public String getUpdate() {
        return "UPDATE Participant SET Nume=?, Prenume=?, Varsta=? WHERE id=?";
    }

    @Override
    public List<Participant> findByProba(Long probaId, String columnName) {
        List<Participant> participants = new ArrayList<>();
        String query = "SELECT * FROM Participant WHERE " + columnName + " IN (SELECT Participant FROM Inscriere WHERE Proba = ?)";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setLong(1, probaId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                participants.add(extractEntity(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Error in findByProba: ", e);
        }
        return participants;
    }

    @Override
    public int getEventCount(Long participantId) {
        String query = "SELECT COUNT(*) AS count FROM Inscriere WHERE Participant = ?";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setLong(1, participantId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
        } catch (SQLException e) {
            logger.error("Error in getEventCount: ", e);
        }
        return 0;
    }
}