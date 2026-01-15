package ro.mpp2025.Repository;

import Utils.JdbcUtils;
import ro.mpp2025.Domain.Inscriere;
import ro.mpp2025.Domain.Participant;
import ro.mpp2025.Domain.Proba;
import ro.mpp2025.Repository.Interface.IInscriereRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InscriereRepository extends AbstractRepository<Long, Inscriere> implements IInscriereRepository {

    private final ParticipantRepository participantRepository;
    private final ProbaRepository probaRepository;

    public InscriereRepository(JdbcUtils jdbcUtils, ParticipantRepository participantRepository, ProbaRepository probaRepository) {
        super(jdbcUtils, "Inscriere");
        this.participantRepository = participantRepository;
        this.probaRepository = probaRepository;
    }

    @Override
    protected Inscriere extractEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        Long participantId = resultSet.getLong("Participant");
        Long probaId = resultSet.getLong("Proba");

        Participant participant = participantRepository.findOne(participantId).orElse(null);
        Proba proba = probaRepository.findOne(probaId).orElse(null);

        return new Inscriere(id, participant, proba);
    }

    @Override
    protected void setEntitySave(PreparedStatement statement, Inscriere entity) throws SQLException {
        statement.setLong(1, entity.getParticipant().getId());
        statement.setLong(2, entity.getProba().getId());
    }

    @Override
    protected void setEntityUpdate(PreparedStatement statement, Inscriere entity) throws SQLException {
        statement.setLong(1, entity.getParticipant().getId());
        statement.setLong(2, entity.getProba().getId());
        statement.setLong(3, entity.getId());
    }

    @Override
    protected String getInsert() {
        return "INSERT INTO Inscriere (Participant, Proba) VALUES ( ?, ?)";
    }

    @Override
    protected String getUpdate() {
        return "UPDATE Inscriere SET Participant = ?, Proba = ? WHERE id = ?";
    }
}