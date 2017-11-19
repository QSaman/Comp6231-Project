package comp6231.project.client;

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

			fileHandler = new FileHandler(
					"/Users/wmg/Documents/workspace/Comp6231-Project/log/mostafa" + ID + "-LogFile.log");
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
