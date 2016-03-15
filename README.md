# my-calculator
a toy calculator

# usage

$ java calculator.Main "let(x, 1, sub(let(x, 2, add(x, 1)), 5))"

$ java calculator.Main --verbosity DEBUG "let(x, 1, sub(let(x, 2, add(x, 1)), 5))"

$ java -enableassertions calculator.Main --verbosity INFO "let(a, 5, let(b, mult(a, 10), add(b, a)))"

$ java -enableassertions calculator.Main "let(a, 5, let(b, mult(a, 10), add(b, a)))"

$ mvn package && mvn -e exec:java -Dexec.mainClass="calculator.Main"

Three verbosity levels are supported: INFO, ERROR, DEBUG.
