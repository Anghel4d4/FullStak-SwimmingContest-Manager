package main.swimmingpersistance.Repositories;


import main.swimmingmodel.User;
import main.swimmingpersistance.Interface.IUserRepository;
import main.swimmingpersistance.Utils.JdbcUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserRepository extends AbstractRepository<Long ,User> implements IUserRepository {

    public UserRepository(JdbcUtils jdbc){
        super(jdbc, "User");

    }

    @Override
    protected User extractEntity(ResultSet resultSet) throws SQLException{
        Long id = resultSet.getLong("id");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        return new User(username,password);
    }

    @Override
    protected void setEntitySave(PreparedStatement statement, User entity) throws SQLException{
        statement.setLong(1, entity.getId());
        statement.setString(2,entity.getUsername());
        statement.setString(3,entity.getPassword());
    }

    @Override
    protected void setEntityUpdate(PreparedStatement statement, User entity) throws SQLException{
        statement.setString(1,entity.getUsername());
        statement.setString(2,entity.getPassword());
        statement.setLong(3,entity.getId());
    }

    @Override
    protected String getInsert(){
        return "INSERT INTO User (id, username, password) VALUES (?,?,?)";
    }

    @Override
    protected String getUpdate(){
        return "Update User SET username=?, password=? WHERE id=?";
    }


    @Override
    public Optional<User> findByUsername(String username) {
        String query = "SELECT * FROM User WHERE username = ?";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = extractEntity(resultSet);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            logger.error("Error in findByUsername: ", e);
        }
        return Optional.empty();
    }

}
