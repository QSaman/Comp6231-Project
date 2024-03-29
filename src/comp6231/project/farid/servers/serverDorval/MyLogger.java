package comp6231.project.farid.servers.serverDorval;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MyLogger {

    private Logger logger;

    public MyLogger(String campus) {
        logger = Logger.getLogger("Log" + campus);
        FileHandler fileHandler;
        try {

            fileHandler = new FileHandler("log/farid/" + campus + "-LogFile.log");
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);

        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String logText) {
        logger.info(logText);

    }

}
