package graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class GraphReader {

	private static Random r = new Random();

	public static DanglingGraph readGraph(String filename) {
		File file = new File(filename);
//		System.out.println(file);
		DanglingGraph result = new DanglingGraph();
		try {
			Scanner scanner = new Scanner(file);
//			System.out.println(scanner);
			while (scanner.hasNextLine()) {
				String s = scanner.nextLine();
				if (!s.startsWith("#")) {
					String[] namen = s.split("\\t");
					if (namen.length >= 2) {
						result.addEdge(namen[0], namen[1]);
					}
				}
			}
			scanner.close();

			// System.out.println(nodeNames);
		} catch (FileNotFoundException e) {
			System.out.println("Invalid file name");
		}
		result.finalise();
		return result;

	}

	// public static void main(String[] args) {
	// DanglingGraph dg = geometric(100,1, 0.1);
	// dg.printMatrix();
	// dg.print();
	// }

	public static DanglingGraph ErdosRenyi(int nNodes, int nEdges) {
		DanglingGraph result = new DanglingGraph();
		for (int i = 0; i < nNodes; i++) {
			result.addNode("" + i);
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

	public static DanglingGraph barabasiAlbert(int nNodes, int edgesPerNode) {
//		System.out.println(edgesPerNode);
		DanglingGraph result = new DanglingGraph();
		List<Integer> degrees = new ArrayList<>();
		for (int i = 0; i < edgesPerNode + 1; i++) {
//			System.out.println(i);
			result.addNode("" + i);
			for (int j = 0; j < i; j++) {
				result.addEdge(i, j);
			}
			degrees.add(edgesPerNode);
		}
//		System.out.println(degrees);
		for (int i = edgesPerNode + 1; i < nNodes; i++) {
//			System.out.println(i);
			result.addNode("" + i);
			degrees.add(edgesPerNode);
			int totaldegrees = edgesPerNode*i;
			Set<Integer> used = new HashSet<>();
			outer: for (int j = 0; j < edgesPerNode; j++) {
//				System.out.println(totaldegrees);
				int gok = totaldegrees == 0? 0: r.nextInt(totaldegrees);
				// System.out.println(gok+" "+totaldegrees+" "+ degrees);
				for (int k = 0; k < i; k++) {
					if (used.contains(k)) {
						continue;
					}
					if (gok < degrees.get(k)) {
						totaldegrees -= degrees.get(k);
						degrees.set(k, degrees.get(k) + 1);
						used.add(k);
						result.addEdge(i, k);
						continue outer;
					} else {
						gok -= degrees.get(k);
					}
				}
				// System.out.println("fout");
			}

		}
		result.finalise();
		return result;

	}

	public static DanglingGraph geometric(int nNodes, int nDimensions, double radius) {
		List<List<Double>> coordinates = new ArrayList<>();
		DanglingGraph result = new DanglingGraph();
		double radiusSquared = radius * radius;
		for (int i = 0; i < nNodes; i++) {
			result.addNode("" + i);
			List<Double> x = new ArrayList<Double>();
			for (int j = 0; j < nDimensions; j++) {
				x.add(r.nextDouble());
			}
			coordinates.add(x);
			outer: for (int j = 0; j < i; j++) {
				double distance = 0.;
				for (int k = 0; k < nDimensions; k++) {
					double coor = coordinates.get(j).get(k) - x.get(k);
					distance += coor * coor;
					if (distance > radiusSquared) {
						continue outer;
					}
				}
				result.addEdge(i, j);
			}
		}
		result.finalise();
		return result;
	}

	public static DanglingGraph geometricTorus(int nNodes, int nDimensions, double radius) {
		List<List<Double>> coordinates = new ArrayList<>();
		DanglingGraph result = new DanglingGraph();
		// int counter = 0;
		double radiusSquared = radius * radius;
		for (int i = 0; i < nNodes; i++) {
			result.addNode("" + i);
			List<Double> x = new ArrayList<Double>();
			// int copy = counter;
			// double step =Math.pow(nNodes, -1./nDimensions);
			// int n = (int) Math.floor(1./step);
			for (int j = 0; j < nDimensions; j++) {
				x.add(r.nextDouble());
				// x.add(0.5*step+copy%n*step);
				// copy/=n;
			}
			// counter++;
			coordinates.add(x);
			outer: for (int j = 0; j < i; j++) {
				double distance = 0.;
				for (int k = 0; k < nDimensions; k++) {
					double coor = coordinates.get(j).get(k) - x.get(k);
					if (Math.abs(coor) < .5) {
						distance += coor * coor;
					} else {
						// System.out.println("ping");
						distance += (1 - Math.abs(coor)) * (1 - Math.abs(coor));
					}
					if (distance > radiusSquared) {
						continue outer;
					}
				}
				result.addEdge(i, j);
			}
		}
		result.finalise();
		return result;
	}

	public static DanglingGraph add(DanglingGraph g1, DanglingGraph g2) {
		DanglingGraph result = new DanglingGraph();
		for (int i = 0; i < g1.order(); i++) {
			result.addNode(g1.getName(i));
			for (int j : g1.getNeighbors(i)) {
				if (j < i) {
					result.addEdge(i, j);
				}
			}
		}
		for (int i = 0; i < g2.order(); i++) {
			if (!result.addNode(g2.getName(i))) {
				String naam = g2.getName(i);
				int x = 1;
				while (!result.addNode(naam + "_" + x)) {
					x++;
				}
			}
			for (int j : g2.getNeighbors(i)) {
				if (j < i) {
					result.addEdge(i + g1.order(), j + g1.order());
				}
			}
		}

		result.finalise();
		return result;
	}

	public static DanglingGraph completeGraph(int graphsize) {
		DanglingGraph result = new DanglingGraph();
		for (int i = 0; i < graphsize; i++) {
			result.addNode("" + ('a' + i));
			for (int j = 0; j < i; j++) {
				result.addEdge(i, j);
			}
		}
		result.finalise();
		return result;
	}

	public static DanglingGraph smallPartsGraph(int graphsize, int partsize) {
		DanglingGraph result = new DanglingGraph();
		for (int i = 0; i < graphsize / partsize; i += 1) {
			result = GraphReader.add(result, completeGraph(partsize));
			// System.out.println(result);
		}
		result.finalise();
		return result;
	}

	public static DanglingGraph wattsStrogatz(int graphsize, int halfdegree, double rewiring) {
		DanglingGraph result = new DanglingGraph();

		List<List<Integer>> edges = new ArrayList<>();
		for (int i = 0; i < graphsize; i++) {
			for (int j = 0; j < halfdegree; j++) {
				List<Integer> edge = new ArrayList<>(2);
				edge.add(i);
				edge.add((i + j + 1) % graphsize);
			}
		}

		result.finalise();
		return result;
	}

	public static void main(String[] args) {
		barabasiAlbert(6, 4);
	}
	// public static DanglingGraph hierarchical(int cluster, int generations,
	// List<Integer> outside){
	// if(generations == 0){
	// DanglingGraph result = new DanglingGraph();
	// result.add()
	// }
	// }
	//
	// private static

}