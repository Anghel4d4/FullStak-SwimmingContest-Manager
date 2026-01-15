//package main.swimmingpersistance.Repositories;
//
//import main.swimmingmodel.Participant;
//import main.swimmingpersistance.Interface.IParticipantRepository;
//import main.swimmingpersistance.Utils.JdbcUtils;
//
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//public class ParticipantRepository extends AbstractRepository<Integer, Participant> implements IParticipantRepository {
//
//    public ParticipantRepository(JdbcUtils jdbc) {
//        super(jdbc, "Participant");
//    }
//
//    @Override
//    public Participant extractEntity(ResultSet resultSet) throws SQLException {
//        Integer id = resultSet.getInt("id");
//        String nume = resultSet.getString("Nume");
//        String prenume = resultSet.getString("Prenume");
//        String varsta = resultSet.getString("Varsta");
//        return new Participant(id, nume, prenume, varsta);
//    }
//
//    @Override
//    public void setEntitySave(PreparedStatement statement, Participant entity) throws SQLException {
//        statement.setString(1, entity.getNume());
//        statement.setString(2, entity.getPrenume());
//        statement.setString(3, entity.getVarsta());
//    }
//
//    @Override
//    public void setEntityUpdate(PreparedStatement statement, Participant entity) throws SQLException {
//        statement.setString(1, entity.getNume());
//        statement.setString(2, entity.getPrenume());
//        statement.setString(3, entity.getVarsta());
//        statement.setInt(4, entity.getId());
//    }
//
//    @Override
//    public String getInsert() {
//        return "INSERT INTO Participant (Nume, Prenume, Varsta) VALUES (?, ?, ?)";
//    }
//
//    @Override
//    public String getUpdate() {
//        return "UPDATE Participant SET Nume=?, Prenume=?, Varsta=? WHERE id=?";
//    }
//
//    @Override
//    public List<Participant> findByProba(Integer probaId, String columnName) {
//        List<Participant> participants = new ArrayList<>();
//        String query = "SELECT * FROM Participant WHERE " + columnName + " IN (SELECT Participant FROM Inscriere WHERE Proba = ?)";
//        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
//            statement.setInt(1, probaId);
//            ResultSet resultSet = statement.executeQuery();
//            while (resultSet.next()) {
//                participants.add(extractEntity(resultSet));
//            }
//        } catch (SQLException e) {
//            logger.error("Error in findByProba: ", e);
//        }
//        return participants;
//    }
//
//    @Override
//    public int getEventCount(Integer participantId) {
//        String query = "SELECT COUNT(*) AS count FROM Inscriere WHERE Participant = ?";
//        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
//            statement.setInt(1, participantId);
//            ResultSet resultSet = statement.executeQuery();
//            if (resultSet.next()) {
//                return resultSet.getInt("count");
//            }
//        } catch (SQLException e) {
//            logger.error("Error in getEventCount: ", e);
//        }
//        return 0;
//    }
//
//    @Override
//    public Optional<Participant> findNameAndPrenume(String nume, String prenume) {
//        String query = "SELECT * FROM Participant WHERE Nume = ? AND Prenume = ?";
//        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
//            statement.setString(1, nume);
//            statement.setString(2, prenume);
//            ResultSet resultSet = statement.executeQuery();
//            if (resultSet.next()) {
//                return Optional.of(extractEntity(resultSet));
//            }
//        } catch (SQLException e) {
//            logger.error("Error in findNameAndPrenume: ", e);
//        }
//        return Optional.empty();
//    }
//}
