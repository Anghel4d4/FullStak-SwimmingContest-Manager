package main.swimmingpersistance.hibernate;

import main.swimmingpersistance.Interface.IProbaRepository;
import main.swimmingmodel.Proba;
import main.swimmingmodel.dto.ProbaDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProbaRepository implements IProbaRepository {
    private static final Logger logger = LogManager.getLogger(ProbaRepository.class);
    private final SessionFactory sessionFactory;

    public ProbaRepository(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
        logger.info("ProbaRepository Initialized");
    }

    @Override
    public Optional<Proba> findOne(Integer id){
        logger.traceEntry("Finding Proba with id: {}", id);
        try(Session session = sessionFactory.openSession()){
            Proba proba = session.get(Proba.class, id);
            logger.traceExit("Found: {}", proba);
            return Optional.ofNullable(proba);
        }
    }

    @Override
    public Iterable<Proba> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Proba", Proba.class).list();
        }
    }

    @Override
    public List<ProbaDTO> findAllWithParticipantCount() {
        logger.trace("Enter Finding all Probas with participant count");
        try (Session session = sessionFactory.openSession()) {
            List<Proba> probas = session.createQuery("FROM Proba", Proba.class).list();
            List<ProbaDTO> result = new ArrayList<>();

            for (Proba proba : probas) {
                Long count = session.createQuery(
                                "SELECT COUNT(i) FROM Inscriere i WHERE i.proba.id = :probaId", Long.class)
                        .setParameter("probaId", proba.getId())
                        .getSingleResult();
                result.add(new ProbaDTO(proba, count.intValue()));
            }
            logger.trace("Exit findAllWithParticipantCount: {}", result);
            return result;
        } catch (Exception e) {
            logger.error("Error in findAllWithParticipantCount: ", e);
            return List.of();
        }
    }

    @Override
    public Optional<Proba> save(Proba entity){
        logger.traceEntry("Saving Proba: {}",entity);
        Transaction tx = null;
        try(Session session = sessionFactory.openSession()){
            tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
            logger.info("Proba saved successfully: {}", entity);
            return Optional.empty();
        }catch(Exception e){
            logger.error("Error saving the Proba: ",e);
            if (tx != null) tx.rollback();
            return Optional.of(entity);
        }
    }

    @Override
    public Optional<Proba> delete(Integer id){
        logger.traceEntry("Deleting Proba with id: {}", id);
        Transaction tx = null;
        try(Session session = sessionFactory.openSession()){
            Proba entity = session.get(Proba.class, id);
            if (entity == null){
                logger.info("Proba with id {} not found", id);
                return Optional.empty();
            }
            tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
            logger.info("Proba deleted successfully: {}", entity);
            return Optional.of(entity);
        }catch (Exception e){
            logger.error("Error deleting the Proba: ", e);
            if (tx != null) tx.rollback();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Proba> update(Proba entity) {
        logger.traceEntry("Updating Proba: {}", entity);
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
            logger.info("Proba updated successfully: {}", entity);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error updating Proba: ", e);
            if (tx != null) tx.rollback();
            return Optional.of(entity);
        }
    }

    @Override
    public Proba findByDistanceAndStyle(String distance, String style) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "FROM Proba WHERE distanta = :distance AND stil = :style", Proba.class)
                    .setParameter("distance", distance)
                    .setParameter("style", style)
                    .uniqueResult();
        }
    }

    @Override
    public int getParticipantCount(Integer probaId) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                            "SELECT COUNT(i.id) FROM Inscriere i WHERE i.proba.id = :pid", Long.class)
                    .setParameter("pid", probaId)
                    .uniqueResult();
            return count != null ? count.intValue() : 0;
        }
    }
}
