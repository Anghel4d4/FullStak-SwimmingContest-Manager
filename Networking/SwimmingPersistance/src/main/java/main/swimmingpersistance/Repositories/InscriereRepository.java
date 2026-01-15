//package main.swimmingpersistance.Repositories;
//
//import main.swimmingmodel.Inscriere;
//import main.swimmingmodel.Participant;
//import main.swimmingmodel.Proba;
//import main.swimmingpersistance.Interface.IInscriereRepository;
//import main.swimmingpersistance.Utils.JdbcUtils;
//
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//public class InscriereRepository extends AbstractRepository<Integer, Inscriere> implements IInscriereRepository {
//
//    private final ParticipantRepository participantRepository;
//    private final ProbaRepository probaRepository;
//
//    public InscriereRepository(JdbcUtils jdbcUtils, ParticipantRepository participantRepository, ProbaRepository probaRepository) {
//        super(jdbcUtils, "Inscriere");
//        this.participantRepository = participantRepository;
//        this.probaRepository = probaRepository;
//    }
//
//    @Override
//    protected Inscriere extractEntity(ResultSet resultSet) throws SQLException {
//        Integer id = resultSet.getInt("id");
//        Integer participantId = resultSet.getInt("Participant");
//        Integer probaId = resultSet.getInt("Proba");
//
//        Participant participant = participantRepository.findOne(participantId).orElse(null);
//        Proba proba = probaRepository.findOne(probaId).orElse(null);
//
//        return new Inscriere(id, participant, proba);
//    }
//
//    @Override
//    protected void setEntitySave(PreparedStatement statement, Inscriere entity) throws SQLException {
//        statement.setInt(1, entity.getParticipant().getId());
//        statement.setInt(2, entity.getProba().getId());
//    }
//
//    @Override
//    protected void setEntityUpdate(PreparedStatement statement, Inscriere entity) throws SQLException {
//        statement.setInt(1, entity.getParticipant().getId());
//        statement.setInt(2, entity.getProba().getId());
//        statement.setInt(3, entity.getId());
//    }
//
//    @Override
//    protected String getInsert() {
//        return "INSERT INTO Inscriere (Participant, Proba) VALUES (?, ?)";
//    }
//
//    @Override
//    protected String getUpdate() {
//        return "UPDATE Inscriere SET Participant = ?, Proba = ? WHERE id = ?";
//    }
//}
