
package calculator;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CLogger {

	private static Logger logger = Logger.getLogger("calculator_logger");

	public static Logger getLogger() {
		return logger;
	}

	public static void info(String msg) {
		logger.info(msg);
	}

	public static void warn(String msg) {
		logger.warning(msg);
	}

	public static void error(String msg) {
		logger.severe(msg);
	}

	public static void debug(String msg) {
		logger.fine(msg);
	}
	
	public static void setLevel(Level level) {
		logger.setLevel(level);
		Handler systemOut = new ConsoleHandler();
		systemOut.setLevel(level);
		logger.addHandler(systemOut);
		logger.setUseParentHandlers( false );
	}

	public static void setLevel(String level) {
		if ("debug".equalsIgnoreCase(level)) setLevel(Level.FINER);
		else if ("error".equalsIgnoreCase(level)) setLevel(Level.SEVERE);
		else setLevel(Level.INFO);
	}

	public static Level getLevel() {
		return logger.getLevel();
	}
	
}
