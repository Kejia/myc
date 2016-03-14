package calculator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for Main.
 */
public class MainTest extends TestCase {

	private Main m;

    public MainTest(String testName) {
        super(testName);
		m = new Main();
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( MainTest.class );
    }

    public void testApp() {
		assertTrue(3 == m.calc("add(1, 2)"));
		assertTrue(-1 == m.calc("sub(1, 2)"));
		assertTrue(6 == m.calc("mult(2, 3)"));
		assertTrue(5 == m.calc("let(a, 5, a)"));
		assertTrue(10 == m.calc("let(a, 5, add(a, a))"));
		assertTrue(7 == m.calc("add(1, mult(2, 3))"));
		assertTrue(9 == m.calc("add(sub(6, 3), mult(2, 3))"));
		assertTrue(10 == m.calc("let(x, div(8, 2), add(sub(6, x), mult(2, 4)))"));
		assertTrue(12 == m.calc("let(x, div(8, 2), add(sub(6, x), mult(2, let (y, add(9, 2), sub(16, y)))))"));
		assertTrue(55 == m.calc("let(a, 5, let(b, mult(a, 10), add(b, a)))"));
		assertTrue(40 == m.calc("let(a, let(b, 10, add(b, b)), let(b, 20, add(a, b)))"));
		assertTrue(39 == m.calc("let(a, let(b, 10, add(add(2, sub(8, 1)), b)), let(b, 20, add(a, b)))"));
		assertTrue(-2 == m.calc("let(x, 1, sub(let(x, 2, add(x, 1)), 5))"));
		assertTrue(7 == m.calc("let(x, 1, sub(let(x, 12, add(x, 1)), add(5, x)))"));
    }
	
}
