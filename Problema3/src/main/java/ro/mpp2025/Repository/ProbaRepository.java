package ro.mpp2025.Repository;

import Utils.JdbcUtils;
import ro.mpp2025.Domain.Proba;
import ro.mpp2025.Repository.Interface.IProbaRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProbaRepository extends AbstractRepository<Long, Proba> implements IProbaRepository {

    public ProbaRepository(JdbcUtils jdbc) {
        super(jdbc, "Proba");
    }

    @Override
    protected Proba extractEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String distanta = resultSet.getString("distanta");
        String stil = resultSet.getString("stil");
        return new Proba(id, distanta, stil);
    }

    @Override
    protected void setEntitySave(PreparedStatement statement, Proba entity) throws SQLException {
        statement.setLong(1, entity.getId());
        statement.setString(2, entity.getDistanta());
        statement.setString(3, entity.getStil());
    }

    @Override
    protected void setEntityUpdate(PreparedStatement statement, Proba entity) throws SQLException {
        statement.setString(1, entity.getDistanta());
        statement.setString(2, entity.getStil());
        statement.setLong(3, entity.getId());
    }

    @Override
    protected String getInsert() {
        return "INSERT INTO Proba (id, distanta, stil) VALUES (?, ?, ?)";
    }

    @Override
    protected String getUpdate() {
        return "UPDATE Proba SET distanta = ?, stil = ? WHERE id = ?";
    }

    @Override
    public List<String> findDistinctDistances() {
        List<String> distances = new ArrayList<>();
        String query = "SELECT DISTINCT distanta FROM Proba";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                distances.add(resultSet.getString("distanta"));
            }
        } catch (SQLException e) {
            logger.error("Error in findDistinctDistances: ", e);
        }
        return distances;
    }

    @Override
    public List<String> findDistinctStyles() {
        List<String> styles = new ArrayList<>();
        String query = "SELECT DISTINCT stil FROM Proba";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                styles.add(resultSet.getString("stil"));
            }
        } catch (SQLException e) {
            logger.error("Error in findDistinctStyles: ", e);
        }
        return styles;
    }

    @Override
    public Proba findByDistanceAndStyle(String distance, String style) {
        logger.debug("Finding Proba by distance: {} and style: {}", distance, style);
        String query = "SELECT * FROM Proba WHERE distanta = ? AND stil = ?";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setString(1, distance);
            statement.setString(2, style);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Proba proba = extractEntity(resultSet);
                logger.debug("Proba found: {}", proba);
                return proba;
            } else {
                logger.debug("Proba not found for distance: {} and style: {}", distance, style);
            }
        } catch (SQLException e) {
            logger.error("Error in findByDistanceAndStyle: ", e);
        }
        return null;
    }
    @Override
    public int getParticipantCount(Long probaId) {
        String query = "SELECT COUNT(*) AS count FROM Inscriere WHERE Proba = ?";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setLong(1, probaId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
        } catch (SQLException e) {
            logger.error("Error in getParticipantCount: ", e);
        }
        return 0;
    }
}