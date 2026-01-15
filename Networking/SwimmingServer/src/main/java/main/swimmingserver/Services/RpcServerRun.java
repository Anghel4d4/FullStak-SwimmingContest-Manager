package main.swimmingserver.Services;


import main.swimmingpersistance.Repositories.UserRepository;
import main.swimmingpersistance.hibernate.InscriereRepository;
import main.swimmingpersistance.hibernate.ParticipantRepository;
import main.swimmingpersistance.hibernate.ProbaRepository;
import main.swimmingpersistance.Utils.HibernateUtils;
import main.swimmingnetworking.AbstractServer;
import main.swimmingnetworking.ConcurrentServer;
import main.swimmingpersistance.Interface.*;
import main.swimmingpersistance.Utils.JdbcUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;

import java.util.Properties;

public class RpcServerRun {
    private static final Logger logger = LogManager.getLogger(RpcServerRun.class);

    public static void main(String[] args) {
        Properties props = new Properties();
        Service service = null;

        try {
            logger.info("Loading server configuration...");
            props.load(RpcServerRun.class.getClassLoader().getResourceAsStream("server.properties"));

            int port = Integer.parseInt(props.getProperty("swimming.server.port"));
            logger.info("Configured server port: {}", port);

            SessionFactory sessionFactory = HibernateUtils.getSessionFactory();

            IParticipantRepository participantRepository = new ParticipantRepository(sessionFactory);
            IProbaRepository probaRepository = new ProbaRepository(sessionFactory);
            IInscriereRepository inscriereRepository = new InscriereRepository(sessionFactory);

            ParticipantService participantService = new ParticipantService(participantRepository);
            ProbaService probaService = new ProbaService(probaRepository);
            InscriereService inscriereService = new InscriereService(inscriereRepository);

            JdbcUtils jdbcUtils = new JdbcUtils(props);
            UserRepository userRepository = new UserRepository(jdbcUtils);
            UserService userService = new UserService(userRepository);

            service = new Service(userService, probaService, participantService, inscriereService);
            AbstractServer server = new ConcurrentServer(port, service);

            logger.info("Starting server on port {}", port);
            server.start();
        } catch (Exception e) {
            logger.error("Error during server startup: {}", e.getMessage(), e);
        } finally {
            if (service != null) {
                service.shutdown();
                logger.info("Service shutdown completed.");
            }
            HibernateUtils.closeSessionFactory();
            logger.info("Hibernate SessionFactory shut down.");
        }
    }
}
