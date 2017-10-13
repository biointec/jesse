package codegenerating;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import equations.Equation;
import equations.EquationManager;
import equations.RHSLengthComparator;
import equations.RHSTermComparator;
import equations.SelectiveEquationManager;
import graph.DanglingGraph;
import graph.GraphReader;
import orbits.OrbitIdentification;
import tree.OrbitTree;

public class Test {

	public static void main(String[] args) throws Exception {
//		testType(6,100,10);
//		OrbitIdentification.readGraphlets("Przulj.txt", 5);
//		List<Comparator<Equation>> comparators = new ArrayList<>();
//		comparators.add(new RHSTermComparator());
//		comparators.add(new RHSLengthComparator());
//		EquationManager em = new SelectiveEquationManager(5, comparators,true);
//		em.addAll(EquationGenerator.generateEquations(5));
//		em.finalise();
//		em.toOrcaCode();
		test("data/Pu.txt", 6);
	}

	public static void testType(int order, int graphorder, int times) {
		long start;
		// long result = 0;
		for (int i = 0; i < times; i++) {
			DanglingGraph g = GraphReader.ErdosRenyi(graphorder, graphorder * 10);
			// System.out.println(g.size());
			g.calculateCommons(order - 2);
			OrbitIdentification.readGraphlets("data/Przulj.txt", order);
			OrbitTree tree;
			tree = new OrbitTree(order - 1);
			DanglingInterpreter di = new DanglingInterpreter(g, tree, new EquationManager(order));
			start = System.nanoTime();
			List<Comparator<Equation>> comparators = new ArrayList<>();
			comparators.add(new RHSTermComparator());
			comparators.add(new RHSLengthComparator());
			di.run();
			System.out.print((System.nanoTime() - start) / 1e9);
			di = new DanglingInterpreter(g, tree, new SelectiveEquationManager(order, new RHSTermComparator(), true));
			start = System.nanoTime();
			di.run();
			System.out.print(" " + (System.nanoTime() - start) / 1e9);
			di = new DanglingInterpreter(g, tree, new SelectiveEquationManager(order,comparators, true));
			start = System.nanoTime();
			di.run();
			System.out.println(" " + (System.nanoTime() - start) / 1e9);
//			di = new DanglingInterpreter(g, tree, new SelectiveEquationManager(order, new RHSLengthComparator(), true));
//			start = System.nanoTime();
//			di.run();
//			System.out.print(" " + (System.nanoTime() - start) / 1e9);
//			di = new DanglingInterpreter(g, tree,
//					new SelectiveEquationManager(order, new RHSLengthComparator(), false));
//			start = System.nanoTime();
//			di.run();
//			System.out.print(" " + (System.nanoTime() - start) / 1e9);
//			di = new DanglingInterpreter(g, tree, new SelectiveEquationManager(order, new LHSLengthComparator(), true));
//			start = System.nanoTime();
//			di.run();
//			System.out.print(" " + (System.nanoTime() - start) / 1e9);
//			di = new DanglingInterpreter(g, tree,
//					new SelectiveEquationManager(order, new LHSLengthComparator(), false));
//			start = System.nanoTime();
//			di.run();
//			System.out.println(" " + (System.nanoTime() - start) / 1e9);
		}
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

			// for (int i = 0; i < times; i++) {
			int n = 6;
			while (true) {
				n += n / 4;
				int max = (n * (n - 1)) / 2;
				int m = r.nextInt(max * 4 / 10) + max / 10;
				// int n = 100;
				// int m = 2413;
				System.out.println(n + "," + m);
				dg = GraphReader.ErdosRenyi(n, m);
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
				// writer.print("\t");
				// writer.println(stop/(double)(System.nanoTime()-start));
			}

		}catch (NegativeCountException e) {
			dg.save("data/brokengraph.txt");
			e.printStackTrace();
			// writer.close();
		}
	}

	public static void test(String naam, int order) throws Exception {
		long start;

		// g.print();
		DanglingGraph g = GraphReader.readGraph(naam);
		System.out.println(g.order());
		// System.out.println(g.size());
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
		count(result, 6);
		System.out.print((System.nanoTime() - start) * 1e-9 + "\t");
		long[] result1 = result[0];
		start = System.nanoTime();
		tree = new OrbitTree(order);
		CountingInterpreter ci = new CountingInterpreter(g, order, tree);
		System.out.print((System.nanoTime() - start) * 1e-9 + "\t");
		start = System.nanoTime();
		result = ci.run();
		System.out.println((System.nanoTime() - start) * 1e-9 + "\t");
		// tree.print();
		long[] result2 = result[0];
		if (!Arrays.equals(result1, result2)) {
			for (int i = 0; i < result1.length; i++) {
				if (result1[i] != result2[i]) {
					System.out.println(i);
				}
			}

		}
			System.out.println(Arrays.toString(result1));
			System.out.println(Arrays.toString(result2));
		// System.out.println(OrbitIdentification.getNOrbitsTotal(order));
	}
	// tree.getRoot().printTree("");

	public static void speeddifference(int order, int times) {
		Random r = new Random();
		OrbitIdentification.readGraphlets("data/Przulj.txt", order);
		DanglingGraph dg=null;
		FileWriter writer = null ;
		try {
			writer = new FileWriter("data/speedDifference"+order+".txt",true);
		

			// for (int i = 0; i < times; i++) {
			while (true) {
				int n = r.nextInt(250) + 50;
				int max = (n * (n - 1)) / 2;
				int m = r.nextInt(max * 19 / 100) + max / 100;
				// int n = 100;
				// int m = 2413;
				System.out.println(n + "," + m);
				dg = GraphReader.ErdosRenyi(n, m);
				long start = System.nanoTime();
				CountingInterpreter ci = new CountingInterpreter(dg, order, new OrbitTree(order));
				long[][] result = ci.run();
				long stop = System.nanoTime() - start;
				start = System.nanoTime();
				dg.calculateCommons(order - 2);
				DanglingInterpreter di = new DanglingInterpreter(dg, new OrbitTree(order - 1),
						new EquationManager(order));
				result = di.run();

				// writer.print(count(result, order));
				// writer.print("\t");
				writer.write("" + (count(result, order) / (double) count(result, order - 1)));
				writer.write("\t");
				writer.write("" + (stop / (double) (System.nanoTime() - start)));
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}catch (NegativeCountException e) {
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

	public static void compare(int order, int graphorder, int graphsize)
			throws FileNotFoundException, UnsupportedEncodingException {
		long start;

		// g.print();

		PrintWriter writer = new PrintWriter("data/compare"+ graphorder+"-"+graphsize+".txt", "UTF-8");
		for (int j = 0; j < 20; j++) {
			// {
			DanglingGraph g = GraphReader.ErdosRenyi(graphorder, graphsize);
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
