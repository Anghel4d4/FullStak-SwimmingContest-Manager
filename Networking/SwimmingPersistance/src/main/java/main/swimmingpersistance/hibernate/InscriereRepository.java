package main.swimmingpersistance.hibernate;

import main.swimmingmodel.Inscriere;
import main.swimmingpersistance.Interface.IInscriereRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class InscriereRepository implements IInscriereRepository {

    private static final Logger logger = LogManager.getLogger(InscriereRepository.class);
    private final SessionFactory sessionFactory;

    public InscriereRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        logger.info("HibernateInscriereRepository initialized");
    }

    @Override
    public Optional<Inscriere> findOne(Integer id) {
        logger.traceEntry("Finding Inscriere with id: {}", id);
        try (Session session = sessionFactory.openSession()) {
            Inscriere inscriere = session.get(Inscriere.class, id);
            logger.traceExit("Found: {}", inscriere);
            return Optional.ofNullable(inscriere);
        }
    }

    @Override
    public Iterable<Inscriere> findAll() {
        logger.traceEntry("Finding all Inscrieres");
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Inscriere", Inscriere.class).list();
        } catch (Exception e) {
            logger.error("Error in findAll: ", e);
            return List.of();
        }
    }

    @Override
    public Optional<Inscriere> save(Inscriere entity) {
        logger.traceEntry("Saving Inscriere: {}", entity);
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
            logger.info("Inscriere saved successfully: {}", entity);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error saving Inscriere: ", e);
            if (tx != null) tx.rollback();
            return Optional.of(entity);
        }
    }

    @Override
    public Optional<Inscriere> delete(Integer id) {
        logger.traceEntry("Deleting Inscriere with id: {}", id);
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            Inscriere entity = session.get(Inscriere.class, id);
            if (entity == null) {
                logger.info("Inscriere with id {} not found", id);
                return Optional.empty();
            }
            tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
            logger.info("Inscriere deleted successfully: {}", entity);
            return Optional.of(entity);
        } catch (Exception e) {
            logger.error("Error deleting Inscriere: ", e);
            if (tx != null) tx.rollback();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Inscriere> update(Inscriere entity) {
        logger.traceEntry("Updating Inscriere: {}", entity);
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
            logger.info("Inscriere updated successfully: {}", entity);
            return Optional.of(entity);
        } catch (Exception e) {
            logger.error("Error updating Inscriere: ", e);
            if (tx != null) tx.rollback();
            return Optional.of(entity);
        }
    }
}