package rest.Persistence.Repositories;




import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rest.Model.Entity;
import rest.Persistence.Interface.Repository;
import rest.Persistence.Utils.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("ALL")
public abstract class AbstractRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {

    protected final JdbcUtils jdbc;
    protected final String tablename;
    public final Logger logger = LogManager.getLogger(AbstractRepository.class);

    public AbstractRepository(JdbcUtils jdbc, String tablename) {
        this.jdbc = jdbc;
        this.tablename = tablename;
    }

    protected Connection getConnection() {
        logger.traceEntry("Getting connection for table {}", tablename);
        Connection conn = jdbc.getConnection();
        logger.traceExit(conn);
        return conn;
    }

    @Override
    public Optional<E> findOne(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        logger.traceEntry("findOne with id: {}", id);
        String query = "SELECT * FROM " + tablename + " WHERE id = ?";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                E entity = extractEntity(resultSet);
                logger.traceExit("Entity found: {}", entity);
                return Optional.of(entity);
            }
            logger.traceExit("Entity not found");
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error in findOne: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Iterable<E> findAll() {
        logger.traceEntry("findAll for table {}", tablename);
        List<E> entities = new ArrayList<>();
        String query = "SELECT * FROM " + tablename;
        try (PreparedStatement statement = getConnection().prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                E entity = extractEntity(resultSet);
                entities.add(entity);
            }
            logger.traceExit("Found {} entities", entities.size());
            return entities;
        } catch (SQLException e) {
            logger.error("Error in findAll: ", e);
            return entities;
        }
    }

    @Override
    public Optional<E> save(E entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }
        logger.info("Saving entity: {}", entity);
        if (entity.getId() != null && findOne(entity.getId()).isPresent()) {
            logger.info("Entity with id {} already exists.", entity.getId());
            return Optional.of(entity);
        }
        try (PreparedStatement statement = getConnection().prepareStatement(getInsert(), PreparedStatement.RETURN_GENERATED_KEYS)) {
            setEntitySave(statement, entity);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        entity.setId((ID) Long.valueOf(generatedKeys.getLong(1)));
                    }
                }
                logger.info("Entity saved successfully.");
                return Optional.empty();
            } else {
                logger.warn("Entity was not saved: {}", entity);
                return Optional.of(entity);
            }
        } catch (SQLException e) {
            logger.error("Error in save: ", e);
            return Optional.of(entity);
        }
    }


    @Override
    public Optional<E> delete(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        logger.info("Deleting entity with id: {}", id);
        Optional<E> entity = findOne(id);
        if (entity.isEmpty()) {
            logger.info("Entity with id {} not found.", id);
            return Optional.empty();
        }
        String query = "DELETE FROM " + tablename + " WHERE id = ?";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setObject(1, id);
            statement.executeUpdate();
            logger.info("Entity with id {} deleted.", id);
            return entity;
        } catch (SQLException e) {
            logger.error("Error in delete: ", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<E> update(E entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }
        logger.info("Updating entity: {}", entity);
        if (findOne(entity.getId()).isEmpty()) {
            logger.info("Entity with id {} does not exist.", entity.getId());
            return Optional.of(entity);
        }
        try (PreparedStatement statement = getConnection().prepareStatement(getUpdate())) {
            setEntityUpdate(statement, entity);
            statement.setObject(statement.getParameterMetaData().getParameterCount(), entity.getId());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Entity updated successfully.");
                return Optional.empty();
            } else {
                logger.warn("Update failed for entity: {}", entity);
                return Optional.of(entity);
            }
        } catch (SQLException e) {
            logger.error("Error in update: ", e);
            return Optional.of(entity);
        }
    }

    protected abstract E extractEntity(ResultSet resultSet) throws SQLException;
    protected abstract void setEntitySave(PreparedStatement statement, E entity) throws SQLException;
    protected abstract void setEntityUpdate(PreparedStatement statement, E entity) throws SQLException;
    protected abstract String getInsert();
    protected abstract String getUpdate();
}