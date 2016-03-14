
package calculator;

import calculator.CLogger;
//import calculator.CLogger;
import calculator.SyntaxException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Stack;
import java.util.StringTokenizer;

public class Main {

	public Main() {}

	/**
	 * Calculate an expression.
	 * @param exp an expression
	 * @return the result of the expression if it's legal; otherwise, null.
	 */
	public Long calc(String exp) {
		CLogger.info("exp: " + exp + ".");
		tokens = tokenize(exp);
		CLogger.info("tokens: " + Arrays.deepToString(tokens) + ".");
		counter = -1;
		return processState0();
	}
	
	/**
	 * Tokenize the input expression.
	 * @param exp an expression
	 * @return an array of tokens
	 */
	private String[] tokenize(String exp) {
		String[] r = null;
		if (exp != null) {
			StringBuffer sb = new StringBuffer();
			char[] chs = exp.toCharArray();
			for (Character c : chs)
				if (c == '(' || c == ')' || c == ',') {
					sb.append(' ');
					sb.append(c);
					sb.append(' ');					
				} else sb.append(c);
			StringTokenizer st = new StringTokenizer(sb.toString());
			r = new String[st.countTokens()];
			int c = 0;
			String token = null;
			Random rand = new Random();
			int scope = rand.nextInt(MAX_ID_NUM);
			Stack<Integer> scopes = new Stack<Integer>();
			int i = -1, z = 0;
			HashSet<String> vars = new HashSet<String>();
			while (st.hasMoreTokens()) {
				i++;
				token = st.nextToken().toLowerCase();
				if (isLet(token)) {// new scope
					scope = rand.nextInt(MAX_ID_NUM);
					scopes.push(scope);
				}
				if (isLeft(token) && !isLet(r[i - 1])) scopes.push(-2);
				if (isRight(token)) {// return to the upper scope 
					z = scopes.pop();
					if (z > -1) scope = z;
				}
				if (isVar(token)) {// bind the correct id declared in the current scope
					if(isLet(r[i - 2])) {
						r[c] = "id_" + token + "_" + scope;
						vars.add(r[c]);
					} else {
						ArrayList<Integer> asc = new ArrayList<Integer>(scopes);
						Collections.reverse(asc);
						for (Integer sc : asc)
							if (vars.contains("id_" + token + "_" + sc)) {
								r[c] = "id_" + token + "_" + sc;
								break;
							}
					}
				} else r[c] = token;
				c++;
			}
		}
		return r;
	}
	
	private static HashMap<String, Long> ids = new HashMap<String, Long>(); // var id table

	private static int MAX_ID_NUM = 65536;

	// keywords
	private static String LET = "let";
	private static String ADD = "add";
	private static String SUB = "sub";
	private static String MULT = "mult";
	private static String DIV = "div";
	private static String LEFT = "(";
	private static String RIGHT = ")";
	private static String COMMA = ",";

	private static String[] tokens; // tokens of the expression
	private static int counter = -1; // tokens visiting pointer

	/**
	 * Log debug message.
	 * @param state in which state the debug msg is produced
	 * @param msg the debug msg
	 */
	private static void debug(int state, String msg) {
		CLogger.debug("state " + state + ": "+ msg);
	}
	
	private static String nextToken() {
		return tokens[++counter];
	}

	private static String currentToken() {
		return tokens[counter];
	}
	
	private boolean isLet(String token) {
		return LET.equals(token);
	}

	private boolean isAdd(String token) {
		return ADD.equals(token);
	}

	private boolean isSub(String token) {
		return SUB.equals(token);
	}

	private boolean isMult(String token) {
		return MULT.equals(token);
	}

	private boolean isDiv(String token) {
		return DIV.equals(token);
	}

	private boolean isOperator(String token) {
		return isAdd(token) || isSub(token) || isMult(token) || isDiv(token);
	}

	private boolean isLeft(String token) {
		return LEFT.equals(token);
	}

	private boolean isRight(String token) {
		return RIGHT.equals(token);
	}

	private boolean isComma(String token) {
		return COMMA.equals(token);
	}

	private boolean isInt(String token)  {  
		try  {
			Long.parseLong(token);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	private Long toInt(String token) {
		return Long.parseLong(token);
	}

	/**
	 * Test whether a token is a legal var. This method is used in tokenizing phase.
	 */
	private boolean isVar(String token) {
		return token != null && token.matches("[a-zA-Z]+") && !isOperator(token) && !isLet(token);
	}
	
	/**
	 * Test whether a token is a legal var id.
	 */
	private boolean isId(String token) {
		return token != null && token.matches("id_[a-zA-Z]+_[0-9]+") && !isOperator(token) && !isLet(token);
	}

	private void throwSyntaxError(String unexpectedToken, String expectedToken) {
		try {
			throw new SyntaxException("unexpected token " + unexpectedToken + "; expected: " + expectedToken);
		} catch (SyntaxException e) {
			CLogger.error("syntax error: expect " + expectedToken + ", but " + unexpectedToken + " encountered.");
			CLogger.debug(e.traceStack());
		}
	}

	private void throwUndefinedIdError(String id) {
		try {
			throw new UndefinedIdException("undefined id: " + id + ".");
		} catch (UndefinedIdException e) {
			CLogger.error("undefined id: " + id);
			CLogger.debug(e.traceStack());
		}
	}

	private Long processExp() {// process an expression
		counter--;
		return processState0();
	}

	private Long processState0() {// process an expression
		String token = nextToken();
		debug(0, token);
		if (isOperator(token)) return processState1(token);
		else if (isLet(token)) return processState6();
		else throwSyntaxError(token, "let or operator.");
		return null;
	}

	private Long processState1(String operator) {// process operators: add, sub, mult, and div.
		
		if (isLeft(nextToken())) {
			debug(1, currentToken());
			Long r = processState2(operator);
			debug(1, "rval from state 2: " + r);
			if (isRight(nextToken())) return r;
			else throwSyntaxError(currentToken(), ").");
		}
		else throwSyntaxError(currentToken(), "(");
		return null;
	}

	private Long processState2(String operator) {
		String token = nextToken();
		debug(2, operator);
		if (isInt(token)) {
			Long r = processState3(operator, token);
			debug(21, r.toString());
			return r;
		} else if (isId(token)) {
			Long r = processState3d1(operator, token);
			debug(22, r.toString());
			return r;
		} else if (isLet(token) || isOperator(token)) {
			Long r = processExp();
			debug(23, r.toString());
			return processState3(operator, r.toString());
		}
		else throwSyntaxError(token, "a legal expression.");
		return null;
	}

	private Long processState3(String operator, String operand) {
		if (isComma(nextToken())) {
			Long r = processState4(operator, operand);
			debug(3, r.toString());
			return r;
		} else throwSyntaxError(currentToken(), ",.");
		return null;
	}

	private Long processState3d1(String operator, String idForOperandLeft) {
		Long operandLeft = ids.get(idForOperandLeft);
		debug(31, operandLeft.toString());
		if (operandLeft != null) return processState3(operator, operandLeft.toString());
		else throwUndefinedIdError(idForOperandLeft);
		return null;
	}

	private Long processState4(String operator, String operand) {
		String token = nextToken();
		if (isInt(token)) return processState5(operator, operand, token);
		else if (isId(token)) {
			Long r = processState5d1(operator, operand, token);
			debug(41, r.toString());
			return r;
		} else if (isLet(token) || isOperator(token)) {
			Long r = processExp();
			debug(42, r.toString());
			return processState5(operator, operand, r.toString());
		} else throwSyntaxError(token, "a legal expression.");
		return null;
	}

	private Long processState5(String operator, String operandLeft, String operandRight) {
		long left = toInt(operandLeft);
		long right = toInt(operandRight);
		if (isAdd(operator)) return new Long(left + right);
		else if (isSub(operator)) return new Long(left - right);
		else if (isMult(operator)) return new Long(left * right);
		else if (isDiv(operator)) return new Long(left / right);
		return null;
	}
	
	private Long processState5d1(String operator, String operandLeft, String idForOperandRight) {
		Long operandRight = ids.get(idForOperandRight);
		debug(51, operandRight.toString());
		if (operandRight != null) {
			Long r = processState5(operator, operandLeft, operandRight.toString());
			debug(52, r.toString());
			return r;
		}
		throwUndefinedIdError(idForOperandRight);
		return null;
	}
	
	private Long processState6() {// process `let' declaration
		if (isLeft(nextToken())) {
			Long r = processState7();
			debug(6, r.toString());
			if (isRight(nextToken())) return r;
			else throwSyntaxError(currentToken(), ").");
		} else throwSyntaxError(currentToken(), "(.");
		return null;
	}

	private Long processState7() {
		if (isId(nextToken())) return processState8(currentToken());
		else throwSyntaxError(currentToken(), "an id.");
		return null;
	}

	private Long processState8(String id) {
		if (isComma(nextToken())) return processState9(id);
		else throwSyntaxError(currentToken(), ",.");
		return null;
	}

	private Long processState9(String id) {
		String token = nextToken();
		Long operandLeft = null;
		if (isInt(token)) operandLeft = toInt(token);
		else operandLeft = processExp();
		ids.put(id, operandLeft);
		debug(9, id + " : " + operandLeft);
		return processState10();
	}

	private Long processState10() {
		if (isComma(nextToken())) return processState11();
		else throwSyntaxError(currentToken(), ",.");
		return null;
	}

	private Long processState11() {
		String token = nextToken();
		if (isInt(token)) return toInt(currentToken());
		else if (isId(currentToken())) {
			Long val = ids.get(currentToken());
			if (val != null) return val;
			else throwUndefinedIdError(currentToken());
		} else return processExp();
		return null;
	}
	
	public static void main(String... args) {
		if (args.length > 1 && ("--verbosity".equals(args[0])))
			CLogger.setLevel(args[1]);
		CLogger.info("calculator on");
		Main m = new Main();
		String rs = null;
		if (args.length == 3) {
			Long r = m.calc(args[2]);
			CLogger.info("result: " + (r == null ? null : r.toString() + "."));
		} else if  (args.length == 1) {
			Long r = m.calc(args[0]);
			CLogger.info("result: " + (r == null ? null : r.toString() + "."));
		} else {
			m.test();
			CLogger.info("usage:\n$ java calculator.Main [--verbosity VERB_LEVEL] exp\n\t- VERB_LEVEL: DEBUG, INFO, ERROR.");
		}
		CLogger.info("bye");
	}

	private void test() {
		Main m = new Main();
		assert 3 == m.calc("add(1, 2)");
		assert 6 == m.calc("mult(2, 3)");
		assert 5 == m.calc("let(a, 5, a)");
		assert 10 == m.calc("let(a, 5, add(a, a))");
		assert 7 == m.calc("add(1, mult(2, 3))");
		assert 9 == m.calc("add(sub(6, 3), mult(2, 3))");
		assert 10 == m.calc("let(x, div(8, 2), add(sub(6, x), mult(2, 4)))");
		assert 12 == m.calc("let(x, div(8, 2), add(sub(6, x), mult(2, let (y, add(9, 2), sub(16, y)))))");
		assert 55 == m.calc("let(a, 5, let(b, mult(a, 10), add(b, a)))");
		assert 40 == m.calc("let(a, let(b, 10, add(b, b)), let(b, 20, add(a, b)))");
		assert 39 == m.calc("let(a, let(b, 10, add(add(2, sub(8, 1)), b)), let(b, 20, add(a, b)))");
		assert -2 == m.calc("let(x, 1, sub(let(x, 2, add(x, 1)), 5))");
		assert 7 == m.calc("let(x, 1, sub(let(x, 12, add(x, 1)), add(5, x)))");
	}
	
}
