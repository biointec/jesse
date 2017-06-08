package graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
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
		DanglingGraph dg = readGraph("data/Test.txt");
		dg.printMatrix();
		dg.print();
	}
	
	public static DanglingGraph ErdosRenyi(int nNodes, int nEdges) {
		Random r = new Random();
		DanglingGraph result = new DanglingGraph();
		for(int i=0;i<nNodes;i++){
			result.addNode(""+i);
		}
		
		int maxEdges = (nNodes * (nNodes - 1)) / 2;
		List<Integer> l = new ArrayList<>(maxEdges);
		for (int i = 0; i < maxEdges; i++) {
			l.add(i);
		}
		for (int i = 0; i < nEdges; i++) {
			int n = r.nextInt(l.size());
			int f = l.remove(n);
			int x = (int) Math.floor(.5 + Math.sqrt(1. + 8 * f) / 2.);
			int y = f - (x * (x - 1)) / 2;
			result.addEdge(x, y);
		}
		result.finalise();
		return result;
	}
}