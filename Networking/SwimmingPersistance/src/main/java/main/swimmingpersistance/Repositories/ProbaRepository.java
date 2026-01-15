//package main.swimmingpersistance.Repositories;
//
//import main.swimmingmodel.Proba;
//import main.swimmingpersistance.Interface.IProbaRepository;
//import main.swimmingpersistance.Utils.JdbcUtils;
//
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ProbaRepository extends AbstractRepository<Integer, Proba> implements IProbaRepository {
//
//    public ProbaRepository(JdbcUtils jdbc) {
//        super(jdbc, "Proba");
//    }
//
//    @Override
//    protected Proba extractEntity(ResultSet resultSet) throws SQLException {
//        Integer id = resultSet.getInt("id");
//        String distanta = resultSet.getString("distanta");
//        String stil = resultSet.getString("stil");
//        return new Proba(id, distanta, stil);
//    }
//
//    @Override
//    protected void setEntitySave(PreparedStatement statement, Proba entity) throws SQLException {
//        statement.setInt(1, entity.getId());
//        statement.setString(2, entity.getDistanta());
//        statement.setString(3, entity.getStil());
//    }
//
//    @Override
//    protected void setEntityUpdate(PreparedStatement statement, Proba entity) throws SQLException {
//        statement.setString(1, entity.getDistanta());
//        statement.setString(2, entity.getStil());
//        statement.setInt(3, entity.getId());
//    }
//
//    @Override
//    protected String getInsert() {
//        return "INSERT INTO Proba (id, distanta, stil) VALUES (?, ?, ?)";
//    }
//
//    @Override
//    protected String getUpdate() {
//        return "UPDATE Proba SET distanta = ?, stil = ? WHERE id = ?";
//    }
//
//    @Override
//    public Proba findByDistanceAndStyle(String distance, String style) {
//        String query = "SELECT * FROM Proba WHERE distanta = ? AND stil = ?";
//        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
//            statement.setString(1, distance);
//            statement.setString(2, style);
//            ResultSet resultSet = statement.executeQuery();
//            if (resultSet.next()) {
//                return extractEntity(resultSet);
//            }
//        } catch (SQLException e) {
//            logger.error("Error in findByDistanceAndStyle: ", e);
//        }
//        return null;
//    }
//
//    @Override
//    public int getParticipantCount(Integer probaId) {
//        String query = "SELECT COUNT(*) AS count FROM Inscriere WHERE Proba = ?";
//        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
//            statement.setInt(1, probaId);
//            ResultSet resultSet = statement.executeQuery();
//            if (resultSet.next()) {
//                return resultSet.getInt("count");
//            }
//        } catch (SQLException e) {
//            logger.error("Error in getParticipantCount: ", e);
//        }
//        return 0;
//    }
//}
