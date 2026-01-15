package main.swimmingpersistance.hibernate;

import main.swimmingmodel.dto.ParticipantDTO;
import main.swimmingmodel.Participant;
import main.swimmingpersistance.Interface.IParticipantRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParticipantRepository implements IParticipantRepository {

    private static final Logger logger = LogManager.getLogger(ParticipantRepository.class);
    private final SessionFactory sessionFactory;

    public ParticipantRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        logger.info("ParticipantRepository initialized");
    }

    @Override
    public Optional<Participant> findOne(Integer id) {
        logger.traceEntry("Finding Participant with id: {}", id);
        try (Session session = sessionFactory.openSession()) {
            Participant participant = session.get(Participant.class, id);
            logger.traceExit("Found: {}", participant);
            return Optional.ofNullable(participant);
        }
    }

    @Override
    public Iterable<Participant> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Participant", Participant.class).list();
        }
    }

    @Override
    public List<ParticipantDTO> findAllWithEventCount() {
        logger.trace("Finding all Participants with event count");
        try (Session session = sessionFactory.openSession()) {
            List<Participant> participants = session.createQuery("FROM Participant", Participant.class).list();
            List<ParticipantDTO> result = new ArrayList<>();

            for (Participant participant : participants) {
                Long count = session.createQuery(
                                "SELECT COUNT(i) FROM Inscriere i WHERE i.participant.id = :participantId", Long.class)
                        .setParameter("participantId", participant.getId())
                        .getSingleResult();
                result.add(new ParticipantDTO(participant, count.intValue()));
            }
            logger.trace("Found Participants with counts: {}", result);
            return result;
        } catch (Exception e) {
            logger.error("Error in findAllWithEventCount: ", e);
            return List.of();
        }
    }

    @Override
    public Optional<Participant> save(Participant entity) {
        logger.traceEntry("Saving Participant: {}", entity);
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
            logger.info("Participant saved successfully: {}", entity);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error saving Participant: ", e);
            if (tx != null) tx.rollback();
            return Optional.of(entity);
        }
    }

    @Override
    public Optional<Participant> delete(Integer id) {
        logger.traceEntry("Deleting Participant with id: {}", id);
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            Participant entity = session.get(Participant.class, id);
            if (entity == null) {
                logger.info("Participant with id {} not found", id);
                return Optional.empty();
            }
            tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
            logger.info("Participant {} deleted successfully !", entity);
            return Optional.of(entity);
        } catch (Exception e) {
            logger.error("Error deleting Participant: ", e);
            if (tx != null) tx.rollback();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Participant> update(Participant entity) {
        logger.traceEntry("Updating Participant: {}", entity);
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
            logger.info("Participant updated successfully: {}", entity);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error when updating Participant: ", e);
            if (tx != null) tx.rollback();
            return Optional.empty();
        }
    }

    @Override
    public int getEventCount(Integer participantId) {
        logger.traceEntry("Getting event count for Participant id: {}", participantId);
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                            "SELECT COUNT(i.id) FROM Inscriere i WHERE i.participant.id = :pid", Long.class)
                    .setParameter("pid", participantId)
                    .uniqueResult();
            int result = count != null ? count.intValue() : 0;
            logger.traceExit("Event count: {}", result);
            return result;
        } catch (Exception e) {
            logger.error("Error in getEventCount: ", e);
            return 0;
        }
    }

    @Override
    public List<Participant> findByProba(Integer probaId, String ignoredColumn) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "SELECT i.participant FROM Inscriere i WHERE i.proba.id = :probaId", Participant.class)
                    .setParameter("probaId", probaId)
                    .list();
        }
    }

    @Override
    public Optional<Participant> findNameAndPrenume(String nume, String prenume) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "FROM Participant WHERE nume = :nume AND prenume = :prenume", Participant.class)
                    .setParameter("nume", nume)
                    .setParameter("prenume", prenume)
                    .uniqueResultOptional();
        }
    }
}
