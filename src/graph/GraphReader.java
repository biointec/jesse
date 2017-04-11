package graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

public class GraphReader {

	public static DanglingGraph readGraph(String filename) {
		File file = new File(filename);
		DanglingGraph result = new DanglingGraph();
		try {
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String s = scanner.nextLine();
				if (!s.startsWith("#")) {
					String[] namen = s.split("\\t");
					if (namen.length == 2) {
						result.addEdge(namen[0], namen[1]);
					}
				}
			}
			scanner.close();

			// System.out.println(nodeNames);
		} catch (FileNotFoundException e) {
			System.out.println("Ongeldige bestandsnaam");
		}
		result.finalise();
		return result;

	}

	public static void main(String[] args) {
		DanglingGraph dg = readGraph("Test.txt");
		dg.printMatrix();
		dg.print();
	}
}