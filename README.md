# my-calculator
a toy calculator

# usage

$ java calculator.Main --verbosity DEBUG "let(x, 1, sub(let(x, 2, add(x, 1)), 5))"

$ javac calculator/Main.java && java -enableassertions -cp . calculator.Main --verbosity INFO "let(a, 5, let(b, mult(a, 10), add(b, a)))"

$ javac calculator/Main.java && java -enableassertions -cp . calculator.Main "let(a, 5, let(b, mult(a, 10), add(b, a)))"

$ mvn package && mvn -e exec:java -Dexec.mainClass="calculator.Main"
