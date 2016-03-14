
package calculator;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CalculatorException extends Exception {
	
	public CalculatorException(String message) {
        super(message);
    }

	public static String getStackTrace(final Throwable throwable) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		throwable.printStackTrace(pw);
		return sw.getBuffer().toString();
	}

	public String traceStack() {
		return getStackTrace(this);
	}
	
}
