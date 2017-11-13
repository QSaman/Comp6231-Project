package assignment.client;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MyLogger {

    private Logger logger;

    public MyLogger(String ID) {
        logger = Logger.getLogger("Log" + ID);
        FileHandler fileHandler;
        try {

            fileHandler = new FileHandler("C:/Users/farid/Google Drive/Projects/Java/Corba/corbaDRRS/LogClients/" + ID + "-LogFile.log");
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);

        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
        logger.setUseParentHandlers(false);
    }

    public void log(String logText) {
        logger.info(logText);

    }

    public void close() {
        for (Handler h : logger.getHandlers()) {
            h.close();
        }
    }
}
