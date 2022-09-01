// Java program to demonstrate
// forEach(BiConsumer) method.
<<<<<<< HEAD
// New person
=======
// new test
>>>>>>> 7cc4601 (more stuff)

import java.util.*;

public class GFG {

	// Main method
	public static void main(String[] args)
	{

		// create a table and add some values
		Map<String, Integer>
			table = new Hashtable<>();

		table.put("Pen", 10);
		table.put("Book", 500);
		table.put("Clothes", 400);
		table.put("Mobile", 5000);
		table.put("Booklet", 2500);

		// add 100 in each value using forEach()
		table.forEach((k, v) -> {

			v = v + 100;
			table.replace(k, v);
		});

		// print new mapping using forEcah()
		table.forEach(
			(k, v) -> System.out.println("Key : " + k + ", Value : " + v));
	}
}
