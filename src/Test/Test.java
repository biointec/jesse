package Test;

/*
 * #%L
 * Jesse
 * %%
 * Copyright (C) 2017 Intec/UGent - Ine Melckenbeeck
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import codegenerating.CountingInterpreter;
import codegenerating.DanglingInterpreter;
import codegenerating.NegativeCountException;
import codegenerating.TreeInterpreter;
import equations.ActiveEquationManager;
import equations.Equation;
import equations.EquationComparator;
import equations.EquationManager;
import equations.RHSLengthComparator;
import equations.RHSTermComparator;
import equations.RandomEquationManager;
import equations.SelectiveEquationManager;
import equations.EquationGenerator;
import graph.DanglingGraph;
import graph.GraphReader;
import orbits.OrbitIdentification;
import tree.OrbitTree;

public class Test {

	static int counter = 0;
	public static void main(String[] args) throws Exception {
		int x = 0;
		if(args.length>0) {
			x = Integer.parseInt(args[0]);
		}
//		System.out.println("test");
//		test("graphs/test.txt",5);
		String[]names = {"candida","ecoli","human"};
		for(int i=x;i<6;i++) {
			System.out.println(names[i/2]+" "+(i%2+5)+" nodes");
			test("graphs/"+names[i/2]+".txt", (i%2+5));
			System.out.println();
		}
	}


	public static void testLijst(int order, int graphorder, List<String> l) {
		DanglingGraph g = GraphReader.ErdosRenyi(graphorder, graphorder / 10);
		// System.out.println(g.size());
		g.calculateCommons(order - 2);
		OrbitIdentification.readGraphlets("data/Przulj.txt", order);
		OrbitTree tree;
		tree = new OrbitTree(order - 1);
		DanglingInterpreter di = new DanglingInterpreter(g, tree, new EquationManager(order));
		long[][] a = di.run(l);
		for (long[] x : a) {
			System.out.println(Arrays.toString(x));
		}
	}

	public static void testSmallParts(int order, int graphorder, int subgraphorder, int times)
			throws FileNotFoundException {
		long start;
		// long result = 0;
		PrintWriter pw = new PrintWriter("choiceSP-" + order + "-" + graphorder + "-" + subgraphorder + ".txt");
		System.out.println("ER " + order);

		for (int i = 0; i < times; i++) {
			System.out.println(i);
			DanglingGraph g = GraphReader.smallPartsGraph(graphorder, subgraphorder);
			// pw.println(g.size());
			g.calculateCommons(order - 2);
			OrbitIdentification.readGraphlets("data/Przulj.txt", order);
			OrbitTree tree;
			tree = new OrbitTree(order - 1);
			DanglingInterpreter di = new DanglingInterpreter(g, tree, new RandomEquationManager(order));
			start = System.nanoTime();
			List<Comparator<Equation>> comparators = new ArrayList<>();
			comparators.add(new RHSTermComparator());
			comparators.add(new RHSLengthComparator());
			di.run();
			pw.print((System.nanoTime() - start) / 1e9);
			pw.print(" " + g.numberOfCalls);
			g.numberOfCalls = 0;
			di = new DanglingInterpreter(g, tree, new SelectiveEquationManager(order, new RHSTermComparator(), true));
			start = System.nanoTime();
			di.run();
			pw.print(" " + (System.nanoTime() - start) / 1e9);
			pw.print(" " + g.numberOfCalls);
			g.numberOfCalls = 0;
			di = new DanglingInterpreter(g, tree, new SelectiveEquationManager(order, new RHSTermComparator(), false));
			start = System.nanoTime();
			di.run();
			pw.print(" " + (System.nanoTime() - start) / 1e9);
			pw.println(" " + g.numberOfCalls);
			g.numberOfCalls = 0;
			// di = new DanglingInterpreter(g, tree, new SelectiveEquationManager(order, new
			// RHSLengthComparator(), true));
			// start = System.nanoTime();
			// di.run();
			// pw.print(" " + (System.nanoTime() - start) / 1e9);
			// di = new DanglingInterpreter(g, tree,
			// new SelectiveEquationManager(order, new RHSLengthComparator(), false));
			// start = System.nanoTime();
			// di.run();
			// pw.print(" " + (System.nanoTime() - start) / 1e9);
			// di = new DanglingInterpreter(g, tree, new SelectiveEquationManager(order, new
			// LHSLengthComparator(), true));
			// start = System.nanoTime();
			// di.run();
			// pw.print(" " + (System.nanoTime() - start) / 1e9);
			// di = new DanglingInterpreter(g, tree,
			// new SelectiveEquationManager(order, new LHSLengthComparator(), false));
			// start = System.nanoTime();
			// di.run();
			// pw.println(" " + (System.nanoTime() - start) / 1e9);
		}
		pw.close();
	}

	public static void testTypeER(int order, int graphorder, int graphsize, int times) throws FileNotFoundException {
		long start;
		// long result = 0;
		PrintWriter pw = new PrintWriter("ER-" + order + "-" + graphorder + "-" + graphsize + ".txt");
		System.out.println("ER " + order);

		// PrintStream pw = System.out;
		for (int i = 0; i < times; i++) {
			System.out.println(i);
			DanglingGraph g = GraphReader.ErdosRenyi(graphorder, graphsize);
			// System.out.println(g.density());
			g.calculateCommons(order - 2);
			OrbitTree tree;
			tree = new OrbitTree(order - 1);

			DanglingInterpreter di = new DanglingInterpreter(g, tree, new RandomEquationManager(order));
			start = System.nanoTime();
			di.run();
			pw.print((System.nanoTime() - start) / 1e9);

			// di = new DanglingInterpreter(g, tree, new SelectiveEquationManager(order, new
			// RHSLengthComparator(), true));
			// start = System.nanoTime();
			// di.run();
			// pw.print(" " + (System.nanoTime() - start) / 1e9);
			//
			// di = new DanglingInterpreter(g, tree,
			// new SelectiveEquationManager(order, new RHSLengthComparator(), false));
			// start = System.nanoTime();
			// di.run();
			// pw.print(" " + (System.nanoTime() - start) / 1e9);

			di = new DanglingInterpreter(g, tree, new SelectiveEquationManager(order, new RHSTermComparator(), true));
			start = System.nanoTime();
			di.run();
			pw.print(" " + (System.nanoTime() - start) / 1e9);

			di = new DanglingInterpreter(g, tree, new SelectiveEquationManager(order, new RHSTermComparator(), false));
			start = System.nanoTime();
			di.run();
			pw.print(" " + (System.nanoTime() - start) / 1e9);
			pw.println(" " + g.density());
		}
		pw.close();
	}

	public static void testFile(int order, String filename, int times) throws IOException {
		long start;
		// long result = 0;

		PrintWriter pw = new PrintWriter(new FileWriter(filename.substring(0, filename.length() - 4) + order + ".txt"),
				true);
		System.out.println(filename + " " + order);

//		 PrintStream pw = System.out;
		for (int i = 0; i < times; i++) {
			System.out.println(i);
			DanglingGraph g = GraphReader.readGraph(filename);
			System.out.println(g.density());
			g.calculateCommons(order - 2);
			OrbitTree tree;
			tree = new OrbitTree(order - 1);

			DanglingInterpreter di = new DanglingInterpreter(g, tree, new EquationManager(order));
			start = System.nanoTime();
			di.run();
			pw.print((System.nanoTime() - start) / 1e9);

			di = new DanglingInterpreter(g, tree, new RandomEquationManager(order));
			start = System.nanoTime();
			di.run();
			pw.print(" " + (System.nanoTime() - start) / 1e9);

			di = new DanglingInterpreter(g, tree, new SelectiveEquationManager(order, new RHSTermComparator(), true));
			start = System.nanoTime();
			di.run();
			pw.print(" " + (System.nanoTime() - start) / 1e9);

			di = new DanglingInterpreter(g, tree, new SelectiveEquationManager(order, new RHSTermComparator(), false));
			start = System.nanoTime();
			di.run();
			pw.print(" " + (System.nanoTime() - start) / 1e9);
			pw.println(" " + g.density());
		}
		pw.close();
	}

	public static void testTypeER2(int order, int graphorder, int graphsize, int times) throws FileNotFoundException {
		long start;
		// long result = 0;
		PrintWriter pw = new PrintWriter("detailER-" + order + "-" + graphorder + "-" + graphsize + ".txt");
		System.out.println("ER " + order);

		// PrintStream pw = System.out;
		for (int i = 0; i < times; i++) {
			System.out.println(i);
			DanglingGraph g = GraphReader.ErdosRenyi(graphorder, graphsize);
			// System.out.println(g.density());
			g.calculateCommons(order - 2);
			OrbitTree tree;
			tree = new OrbitTree(order - 1);

			DanglingInterpreter di = new DanglingInterpreter(g, tree, new RandomEquationManager(order));
			start = System.nanoTime();
			di.run();
			pw.print((System.nanoTime() - start) / 1e9);

			di = new DanglingInterpreter(g, tree, new SelectiveEquationManager(order, new RHSLengthComparator(), true));
			start = System.nanoTime();
			di.run();
			pw.print(" " + (System.nanoTime() - start) / 1e9);

			di = new DanglingInterpreter(g, tree,
					new SelectiveEquationManager(order, new RHSLengthComparator(), false));
			start = System.nanoTime();
			di.run();
			pw.print(" " + (System.nanoTime() - start) / 1e9);

			di = new DanglingInterpreter(g, tree, new SelectiveEquationManager(order, new RHSTermComparator(), true));
			start = System.nanoTime();
			di.run();
			pw.print(" " + (System.nanoTime() - start) / 1e9);

			di = new DanglingInterpreter(g, tree, new SelectiveEquationManager(order, new RHSTermComparator(), false));
			start = System.nanoTime();
			di.run();
			pw.print(" " + (System.nanoTime() - start) / 1e9);
			pw.println(" " + g.density());
		}
		pw.close();
	}

	public static void testTypeBA(int order, int graphorder, int graphdegree, int times) throws IOException {
		long start;
		// long result = 0;

		System.out.println("BA " + order);
		PrintWriter pw = new PrintWriter("BA" + order + "-" + graphorder + "-" + graphdegree + ".txt");
		for (int i = 0; i < times; i++) {
			System.out.println(i);
			DanglingGraph g = GraphReader.barabasiAlbert(graphorder, graphdegree);
			// System.out.println(g.density());
			g.calculateCommons(order - 2);
			OrbitTree tree;
			tree = new OrbitTree(order - 1);

			DanglingInterpreter di = new DanglingInterpreter(g, tree, new RandomEquationManager(order));
			start = System.nanoTime();
			di.run();
			pw.print((System.nanoTime() - start) / 1e9);

			// di = new DanglingInterpreter(g, tree, new SelectiveEquationManager(order, new
			// RHSLengthComparator(), true));
			// start = System.nanoTime();
			// di.run();
			// pw.print(" " + (System.nanoTime() - start) / 1e9);
			//
			// di = new DanglingInterpreter(g, tree,
			// new SelectiveEquationManager(order, new RHSLengthComparator(), false));
			// start = System.nanoTime();
			// di.run();
			// pw.print(" " + (System.nanoTime() - start) / 1e9);

			di = new DanglingInterpreter(g, tree, new SelectiveEquationManager(order, new RHSTermComparator(), true));
			start = System.nanoTime();
			di.run();
			pw.print(" " + (System.nanoTime() - start) / 1e9);

			di = new DanglingInterpreter(g, tree, new SelectiveEquationManager(order, new RHSTermComparator(), false));
			start = System.nanoTime();
			di.run();
			pw.print(" " + (System.nanoTime() - start) / 1e9);
			pw.println(" " + g.density());
		}
		pw.close();
	}

	public static void quicktestTypeGeo(int order, int graphorder, int graphradius, int times)
			throws FileNotFoundException {
		long start;
		// long result = 0;
		// PrintWriter pw = new
		// PrintWriter("choiceGeo"+order+"-"+graphorder+"-"+graphradius+".txt");
		System.out.println("Geo " + order + " " + graphorder);
		for (int i = 0; i < times; i++) {
			System.out.println(i);
			DanglingGraph g = GraphReader.ErdosRenyi(graphorder, graphradius);
			System.out.println(g.density());
			System.out.println(1. * g.size() / graphorder);
			g.calculateCommons(order - 2);
			OrbitIdentification.readGraphlets("data/Przulj.txt", order);
			OrbitTree tree;
			tree = new OrbitTree(order - 1);
			TreeInterpreter di;
			di = new DanglingInterpreter(g, tree, new SelectiveEquationManager(order, new RHSTermComparator(), true));
			start = System.nanoTime();
			di.run();
			System.out.print(" " + (System.nanoTime() - start) / 1e9);
			g.numberOfCalls = 0;
			di = new CountingInterpreter(g, order - 1, tree);
			start = System.nanoTime();
			long[][] result = di.run();
			System.out.print(" " + (System.nanoTime() - start) / 1e9);
			// tree = new OrbitTree(order);
			g.numberOfCalls = 0;
			di = new DanglingInterpreter(g, tree, new ActiveEquationManager(order, result));
			start = System.nanoTime();
			di.run();
			System.out.print(" " + (System.nanoTime() - start) / 1e9);
		}
	}

	public static void testTypeGeo(int order, int graphorder, double graphradius, int times)
			throws FileNotFoundException {
		long start;
		// long result = 0;
		PrintWriter pw = new PrintWriter(
				"Geo" + order + "-" + graphorder + "-" + ("" + graphradius).substring(6) + ".txt");
		System.out.println("Geo " + order + " " + graphorder);
		for (int i = 0; i < times; i++) {
			System.out.println(i);
			DanglingGraph g = GraphReader.geometricTorus(graphorder, 3, graphradius);
			// System.out.println(g.density());
			g.calculateCommons(order - 2);
			OrbitTree tree;
			tree = new OrbitTree(order - 1);

			DanglingInterpreter di = new DanglingInterpreter(g, tree, new RandomEquationManager(order));
			start = System.nanoTime();
			di.run();
			pw.print((System.nanoTime() - start) / 1e9);

			// di = new DanglingInterpreter(g, tree, new SelectiveEquationManager(order, new
			// RHSLengthComparator(), true));
			// start = System.nanoTime();
			// di.run();
			// pw.print(" " + (System.nanoTime() - start) / 1e9);
			//
			// di = new DanglingInterpreter(g, tree,
			// new SelectiveEquationManager(order, new RHSLengthComparator(), false));
			// start = System.nanoTime();
			// di.run();
			// pw.print(" " + (System.nanoTime() - start) / 1e9);

			di = new DanglingInterpreter(g, tree, new SelectiveEquationManager(order, new RHSTermComparator(), true));
			start = System.nanoTime();
			di.run();
			pw.print(" " + (System.nanoTime() - start) / 1e9);

			di = new DanglingInterpreter(g, tree, new SelectiveEquationManager(order, new RHSTermComparator(), false));
			start = System.nanoTime();
			di.run();
			pw.print(" " + (System.nanoTime() - start) / 1e9);
			pw.println(" " + g.density());
		}
		pw.close();
	}

	public static void speedTest(int order, int graphorder, int times) throws NegativeCountException {
		long start;
		long result = 0;
		for (int i = 0; i < times; i++) {
			DanglingGraph g = GraphReader.ErdosRenyi(graphorder, graphorder * 10);
			// System.out.println(g.size());
			g.calculateCommons(order - 2);
			OrbitIdentification.readGraphlets("Przulj.txt", order);
			OrbitTree tree;
			tree = new OrbitTree(order - 1);
			DanglingInterpreter di = new DanglingInterpreter(g, tree, new EquationManager(order));

			start = System.nanoTime();
			di.run();
			result += (System.nanoTime() - start);
		}
		System.out.println(result * 1e-9 / times);
	}

	public static void testGraphs(int iterations) {
		for (int i = 150; i < 250; i += 50) {
			// for ( int j = 1; j < 5; j +=2) {
			int j = 3;
			for (double r = 0.05; r < 0.20; r += .05) {
				// speeddifferenceGeo(5, iterations, i, j, Math.pow(r, 1./j));
				if (i != 150 || r != .05)
					speeddifferenceGeo(6, iterations, i, j, Math.pow(r, 1. / j));
				// speeddifferenceGeo(7, iterations, i, j, Math.pow(r, 1./j));
			}
			// }
		}
		for (int i = 200; i < 250; i += 50) {
			for (int j = 9; j < 13; j += 2) {
				// speeddifferenceBA(5, iterations, i, j);
				speeddifferenceBA(6, iterations, i, j);
				// speeddifferenceER(7, iterations, i, j);
			}
		}
		for (int i = 100; i < 250; i += 50) {
			for (int j = i * 8; j < i * 13; j += i) {
				// speeddifferenceER(5, iterations, i, j);
				// speeddifferenceER(6, iterations, i, j);
				speeddifferenceER(7, 1, i, j);
			}
		}
		for (int i = 100; i < 250; i += 50) {
			for (int j = 8; j < 13; j++) {
				// speeddifferenceBA(5, iterations, i, j);
				// speeddifferenceBA(6, iterations, i, j);
				speeddifferenceER(7, 1, i, j);
			}
		}
		for (int i = 100; i < 250; i += 50) {
			for (int j = 1; j < 5; j++) {
				for (double r = 0.05; r < 0.25; r += .05) {
					// speeddifferenceGeo(5, iterations, i, j, Math.pow(r, 1./j));
					// speeddifferenceGeo(6, iterations, i, j, Math.pow(r, 1./j));
					speeddifferenceGeo(7, 1, i, j, Math.pow(r, 1. / j));
				}
			}
		}
	}

	public static void toOrca(String naam) throws FileNotFoundException {
		File file = new File(naam);
		Scanner scanner = new Scanner(file);
		PrintWriter pw = new PrintWriter("orca.txt");
		HashMap<String, Integer> h = new HashMap<>();
		int counter = 0;
		while (scanner.hasNextLine()) {
			String a = scanner.nextLine();
			String[] s = a.split("\\t");
			if (!h.containsKey(s[0])) {
				h.put(s[0], counter++);
			}
			if (!h.containsKey(s[1])) {
				h.put(s[1], counter++);
			}
			pw.println(h.get(s[0]) + " " + h.get(s[1]));
		}
		scanner.close();
		pw.close();
	}

	public static void copyWithout(String naam, Set<String> ss) throws FileNotFoundException {
		File file = new File(naam);
		Scanner scanner = new Scanner(file);
		PrintWriter pw = new PrintWriter("copy.txt");
		while (scanner.hasNextLine()) {
			String a = scanner.nextLine();
			String[] s = a.split("\\t");
			if (!ss.contains(s[0]) && !ss.contains(s[1])) {
				pw.println(a);
			}
		}

		pw.close();
		scanner.close();

	}

	public static void test7() {
		Random r = new Random();
		OrbitIdentification.readGraphlets("data/Przulj.txt", 7);
		OrbitTree ot7 = new OrbitTree(7);
		OrbitTree ot6 = new OrbitTree(6);
		DanglingGraph dg = null;
		// PrintWriter writer = null ;
		try {
			// writer = new PrintWriter("speedDifference"+order+".txt",
			// "UTF-8");
			// writer = new PrintWriter("speedDifference"+order+".txt", "UTF-8");

			// for (int i = 0; i < times; i++) {
			int n = 6;
			while (true) {
				n += n / 4;
				int max = (n * (n - 1)) / 2;
				int m = r.nextInt(max * 4 / 10) + max / 10;
				// int n = 100;
				// int m = 2413;
				System.out.println(n + "," + m);
				dg = GraphReader.barabasiAlbert(n, m);
				long start = System.nanoTime();
				CountingInterpreter ci = new CountingInterpreter(dg, 7, ot7);
				long[][] result1 = ci.run();
				long stop = System.nanoTime() - start;
				start = System.nanoTime();
				dg.calculateCommons(5);
				DanglingInterpreter di = new DanglingInterpreter(dg, ot6, new EquationManager(7));

				long[][] result2 = di.run();
				System.out.println(count(result2, 7));
				if (!Arrays.deepEquals(result1, result2)) {
					throw new NegativeCountException();
				}
				// writer.print(count(result, order));
				// writer.print("\t");
				// writer.print(count(result, order) / (double) count(result,
				// order - 1));
				// writer.print(count(result, order) / (double) count(result, order - 1));
				// writer.print("\t");
				// writer.println(stop/(double)(System.nanoTime()-start));
			}

		} catch (NegativeCountException e) {
			dg.save("data/brokengraph.txt");
			e.printStackTrace();
			// writer.close();
		}
	}

	public static void test(String naam, int order) throws Exception {
		long start;

		// g.print();
		DanglingGraph g = GraphReader.readGraph(naam);

		// g.save("copyBefore.txt");
		System.out.println(g.order() + "," + g.size());
		System.out.println(g.density());
		System.out.println("commons\tinterpreter\trun");
		start = System.nanoTime();
		g.calculateCommons(order - 2);
		System.out.print((System.nanoTime() - start) * 1e-9 + "\t");
		start = System.nanoTime();
		OrbitIdentification.readGraphlets("data/Przulj.txt", order);
		OrbitTree tree;
		tree = new OrbitTree(order - 1);
		// tree.getRoot().printTree("");
		DanglingInterpreter di = new DanglingInterpreter(g, tree);
		System.out.print((System.nanoTime() - start) * 1e-9 + "\t");
		start = System.nanoTime();
		long[][] result = di.run();
		long[] result1 = result[0];
		System.out.print((System.nanoTime() - start) * 1e-9 + "\t");
		PrintWriter pw = new PrintWriter("results/test"+counter++ +".txt");
		for(long[] l :result) {
			pw.println(Arrays.toString(l));
		}
//		start = System.nanoTime();
//		tree = new OrbitTree(order);
//		CountingInterpreter ci = new CountingInterpreter(g, order, tree);
//		System.out.print((System.nanoTime() - start) * 1e-9 + "\t");
//		start = System.nanoTime();
//		result = ci.run();
//		System.out.println((System.nanoTime() - start) * 1e-9 + "\t");
//		// tree.print();
//		long[] result2 = result[0];
//		if (!Arrays.equals(result1, result2)) {
//
//			for (int i = 0; i < result1.length; i++) {
//				if (result1[i] != result2[i]) {
//					System.out.println(i);
//				}
//			}
//
//		}
//		System.out.println(Arrays.toString(result1));
//		System.out.println(Arrays.toString(result2));
		// System.out.println(OrbitIdentification.getNOrbitsTotal(order));
		// g.save("copyAfter.txt");
		System.out.println();
	}
	// tree.getRoot().printTree("");

	public static void speeddifferenceBA(int order, int times, int n, int m) {
		Random r = new Random();
		OrbitIdentification.readGraphlets("data/Przulj.txt", order);
		DanglingGraph dg = null;
		FileWriter writer = null;
		try {
			writer = new FileWriter("data/speedDifferenceBA" + order + "-" + n + "-" + m + ".txt", false);

			for (int i = 0; i < times; i++) {
				// while(true){
				// int n = r.nextInt(150) + 50;
				// int m = r.nextInt(max *19/100) + max / 100;
				// int m = r.nextInt(10)+1;
				// double d = r.nextDouble()/3+.1;
				// int n = 100;
				// int m = 2413;
				// System.out.println(n + "," + m);
				dg = GraphReader.barabasiAlbert(n, m);
				System.out.println(dg.order() + "," + dg.size());
				long start = System.nanoTime();
				CountingInterpreter ci = new CountingInterpreter(dg, order, new OrbitTree(order));
				long[][] result = ci.run();
				long stop = System.nanoTime() - start;
				start = System.nanoTime();
				dg.calculateCommons(order - 2);
				DanglingInterpreter di = new DanglingInterpreter(dg, new OrbitTree(order - 1));
				result = di.run();
				long stop2 = System.nanoTime() - start;

				// writer.print(count(result, order));
				// writer.print("\t");
				writer.write("" + (count(result, order)));
				writer.write("\t");
				writer.write("" + count(result, order - 1));
				writer.write("\t");
				writer.write("" + ((double) stop / 1e9));
				writer.write("\t");
				writer.write("" + ((double) stop2 / 1e9));
				writer.write("\t" + dg.ncommon);
				writer.write("\n");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NegativeCountException e) {
			dg.save("data/brokengraph.txt");
			e.printStackTrace();
		} finally {

			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static void speeddifferenceGeo(int order, int times, int n, int dimension, double d) {
		Random r = new Random();
		OrbitIdentification.readGraphlets("data/Przulj.txt", order);
		DanglingGraph dg = null;
		FileWriter writer = null;
		try {
			writer = new FileWriter("data/speedDifferenceGeo" + order + "-" + n + "-" + dimension + "-" + d + ".txt",
					false);

			for (int i = 0; i < times; i++) {
				// while(true){
				// int n = r.nextInt(200) + 50;
				// int max = (n * (n - 1)) / 2;
				// int m = r.nextInt(max *19/100) + max / 100;
				// int m = r.nextInt(10)+1;
				// int dimension = r.nextInt(3)+1;
				// double d = r.nextDouble()*dimension* dimension *.05;
				// int n = 100;
				// int m = 2413;
				// System.out.println(n + "," + m);
				dg = GraphReader.geometric(n, dimension, d);
				System.out.println(dimension + "," + d);
				System.out.println(dg.order() + "," + dg.size());
				long start = System.nanoTime();
				CountingInterpreter ci = new CountingInterpreter(dg, order, new OrbitTree(order));
				long[][] result = ci.run();
				long stop = System.nanoTime() - start;
				start = System.nanoTime();
				dg.calculateCommons(order - 2);
				DanglingInterpreter di = new DanglingInterpreter(dg, new OrbitTree(order - 1),
						new EquationManager(order));
				result = di.run();
				long stop2 = System.nanoTime() - start;

				// writer.print(count(result, order));
				// writer.print("\t");
				writer.write("" + (count(result, order)));
				writer.write("\t");
				writer.write("" + count(result, order - 1));
				writer.write("\t");
				writer.write("" + ((double) stop / 1e9));
				writer.write("\t");
				writer.write("" + ((double) stop2 / 1e9));
				writer.write("\t" + dg.ncommon);
				writer.write("\n");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NegativeCountException e) {
			dg.save("data/brokengraph.txt");
			e.printStackTrace();
		} finally {

			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static void speeddifferenceER(int order, int times, int n, int m) {
		Random r = new Random();
		OrbitIdentification.readGraphlets("data/Przulj.txt", order);
		DanglingGraph dg = null;
		FileWriter writer = null;
		try {
			writer = new FileWriter("data/speedDifferenceER" + order + "-" + n + "-" + m + ".txt", false);

			for (int i = 0; i < times; i++) {
				// while(true){
				// int n = r.nextInt(200) + 50;
				// int max = (n * (n - 1)) / 2;
				// int m = r.nextInt(max *9/100) + max / 100;
				// int m = r.nextInt(10)+1;
				// double d = r.nextDouble()/3+.1;
				// int n = 100;
				// int m = 2413;
				// System.out.println(n + "," + m);
				dg = GraphReader.ErdosRenyi(n, m);
				System.out.println(dg.order() + "," + dg.size());
				long start = System.nanoTime();
				CountingInterpreter ci = new CountingInterpreter(dg, order, new OrbitTree(order));
				long[][] result = ci.run();
				long stop = System.nanoTime() - start;
				start = System.nanoTime();
				dg.calculateCommons(order - 2);
				DanglingInterpreter di = new DanglingInterpreter(dg, new OrbitTree(order - 1));
				result = di.run();
				long stop2 = System.nanoTime() - start;
				// writer.print(count(result, order));
				// writer.print("\t");
				writer.write("" + (count(result, order)));
				writer.write("\t");
				writer.write("" + count(result, order - 1));
				writer.write("\t");
				writer.write("" + ((double) stop / 1e9));
				writer.write("\t");
				writer.write("" + ((double) stop2 / 1e9));
				writer.write("\t" + dg.ncommon);
				writer.write("\n");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NegativeCountException e) {
			dg.save("data/brokengraph.txt");
			e.printStackTrace();
		} finally {

			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static void graphletdegreeBA(int order, int times) {
		OrbitIdentification.readGraphlets("data/Przulj.txt", order);
		DanglingGraph dg = null;
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("data/graphletDegreeBA100" + order + ".txt");

			for (int i = 0; i < times; i++) {
				// int n = 100;
				// int m = 10;
				// System.out.println(n + "," + m);
				dg = GraphReader.barabasiAlbert(100, 10);
				System.out.println(dg.order() + "," + dg.size());
				dg.calculateCommons(order - 2);
				DanglingInterpreter di = new DanglingInterpreter(dg, new OrbitTree(order - 1));
				long[][] result = di.run();

				for (int j = 0; j < result.length; j++) {
					for (int k = 0; k < result[j].length; k++) {
						writer.print(result[j][k]);
						writer.print("\t");
					}
					writer.println();
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		writer.close();

	}

	public static void graphletdegreeER(int order, int times) {
		OrbitIdentification.readGraphlets("data/Przulj.txt", order);
		DanglingGraph dg = null;
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("data/graphletDegreeER100" + order + ".txt");

			for (int i = 0; i < times; i++) {
				// int n = 100;
				// int m = 10;
				// System.out.println(n + "," + m);
				dg = GraphReader.ErdosRenyi(100, 900);
				System.out.println(dg.order() + "," + dg.size());
				dg.calculateCommons(order - 2);
				DanglingInterpreter di = new DanglingInterpreter(dg, new OrbitTree(order - 1));
				long[][] result = di.run();

				for (int j = 0; j < result.length; j++) {
					for (int k = 0; k < result[j].length; k++) {
						writer.print(result[j][k]);
						writer.print("\t");
					}
					writer.println();
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		writer.close();

	}

	public static void graphletdegreeGeo(int order, int times) {
		OrbitIdentification.readGraphlets("data/Przulj.txt", order);
		DanglingGraph dg = null;
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("data/graphletDegreeGeo100" + order + ".txt");

			for (int i = 0; i < times; i++) {
				// int n = 100;
				// int m = 10;
				// System.out.println(n + "," + m);
				dg = GraphReader.geometric(100, 3, 0.41);
				System.out.println(dg.order() + "," + dg.size());
				dg.calculateCommons(order - 2);
				DanglingInterpreter di = new DanglingInterpreter(dg, new OrbitTree(order - 1));
				long[][] result = di.run();

				for (int j = 0; j < result.length; j++) {
					for (int k = 0; k < result[j].length; k++) {
						writer.print(result[j][k]);
						writer.print("\t");
					}
					writer.println();
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		writer.close();

	}

	public static void compare(int order, int graphorder, int graphsize)
			throws FileNotFoundException, UnsupportedEncodingException {
		long start;

		// g.print();

		PrintWriter writer = new PrintWriter("data/compare" + graphorder + "-" + graphsize + ".txt", "UTF-8");
		for (int j = 0; j < 20; j++) {
			// {
			DanglingGraph g = GraphReader.barabasiAlbert(graphorder, graphsize / graphorder);
			start = System.nanoTime();
			g.calculateCommons(order - 2);
			writer.print((System.nanoTime() - start) * 1e-9 + "\t");
			OrbitIdentification.readGraphlets("data/Przulj.txt", order);
			start = System.nanoTime();
			OrbitTree tree;
			tree = new OrbitTree(order - 1);
			DanglingInterpreter di = new DanglingInterpreter(g, tree, new EquationManager(order));
			writer.print((System.nanoTime() - start) * 1e-9 + "\t");
			start = System.nanoTime();
			long[][] result = di.run();
			writer.print((System.nanoTime() - start) * 1e-9 + "\t");
			long[] result1 = result[1];
			start = System.nanoTime();
			tree = new OrbitTree(order);
			CountingInterpreter ci = new CountingInterpreter(g, order, tree);
			writer.print((System.nanoTime() - start) * 1e-9 + "\t");
			start = System.nanoTime();
			result = ci.run();
			writer.println((System.nanoTime() - start) * 1e-9 + "\t");
			long[] result2 = result[1];
			// g.print();
			if (!Arrays.equals(result1, result2)) {
				for (int i = 0; i < result1.length; i++) {
					if (result1[i] != result2[i]) {
						System.out.println(i);
					}
				}
				System.out.println(Arrays.toString(result1));
				System.out.println(Arrays.toString(result2));

			}
		}
	}

	public static long count(long[][] a, int order) throws NegativeCountException {
		long total = 0;
		for (int i = 0; i < a.length; i++) {
			if (a[i] != null)
				for (int j = 0; j < OrbitIdentification.getNOrbitsTotal(order); j++) {

					if (a[i][j] >= 0) {

						total += a[i][j];
					} else {
						System.out.println(i);
						System.out.println(Arrays.toString(a[i]));
						throw new NegativeCountException();
					}
				}
		}
		return total;
	}

	public static int count(String filename, int order) {
		File file = new File(filename + ".txt");
		int total = 0;
		try {
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String s = scanner.nextLine();
				String[] pieces = s.split(" ");
				for (int i = 1; i <= OrbitIdentification.getNOrbitsTotal(order); i++) {
					int a = Integer.parseInt(pieces[i]);
					total += a;
				}
			}
			scanner.close();
		} catch (Exception e) {
			System.out.println("Ongeldige bestandsnaam");
		}
		return total;
	}
}
