package graphletgenerating;

import java.io.*;
import java.util.*;

public class Program {

	public static int order = 0;

	public static void main(String[] args) throws IOException {
		int neworder=0;
		Scanner s = new Scanner(System.in);
		if (args.length != 0) {
			try {
				neworder = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.out.println("Invalid argument.");
			}
		}
		while (neworder == 0) {
			System.out.println("Please enter the order of the graphlets.");
			try {
				neworder = s.nextInt();
			} catch (InputMismatchException e) {
				System.out.println("Invalid entry.");
			}
		}
		s.close();
		PrintWriter ps2 = new PrintWriter(new BufferedWriter(new FileWriter("graphlets-" + order + ".txt")));
		PrintWriter ps = new PrintWriter(new BufferedWriter(new FileWriter("graphlets-" + order + ".ps")));
		ps.append("%!PS\n/Times-Roman findfont\n10 scalefont\nsetfont\n");

		int numberGraphlets = 0;
		int numberOrbits = 0;

		for (int j = 2; j <= neworder; j++) {
			order = j;
			boolean[] array = new boolean[order * (order - 1) / 2];
			for (int i = 0; i < array.length; i++)
				array[i] = false;

			SortedSet<String> reps = new TreeSet<String>();
			for (int i = 0; i < Math.pow(2, array.length); i++) {
				if (i % 1000 == 0)
					System.out.println("Iteration " + i + "/" + (int) Math.pow(2, array.length));
				Graph graph = new Graph(array);
				if (graph.isGraphlet()) {
					SortedSet<String> orbits = graph.permute(reps);
					if (orbits != null) {
						reps.add(graph.toString());
						numberGraphlets++;
						numberOrbits = numberOrbits + orbits.size();
						ps.append(graph.toPS());
						ps2.append(graph.toGraphlet());
						System.out.println(graph);
					}
				}
				incArray(array);
			}
		}

		ps.close();
		ps2.close();
		System.out.println("Number of graphlets: " + numberGraphlets);
		System.out.println("Number of orbits: " + numberOrbits);
	}

	public static void generateGraphlets(int xx, String filename) throws IOException {

		PrintWriter ps2 = new PrintWriter(new BufferedWriter(new FileWriter(filename + ".txt")));
		PrintWriter ps = new PrintWriter(new BufferedWriter(new FileWriter(filename + ".ps")));
		ps.append("%!PS\n/Times-Roman findfont\n10 scalefont\nsetfont\n");

		int numberOrbits = 0;
		for (int j = 2; j <= xx; j++) {
			order = j;
			boolean[] array = new boolean[order * (order - 1) / 2];
			for (int i = 0; i < array.length; i++)
				array[i] = false;

			SortedSet<String> reps = new TreeSet<String>();
			for (int i = 0; i < Math.pow(2, array.length); i++) {
//				if (i % 1000 == 0)
//					System.out.println("Iteration " + i + "/" + (int) Math.pow(2, array.length));
				Graph graph = new Graph(array);
				if (graph.isGraphlet()) {
					SortedSet<String> orbits = graph.permute(reps);
					if (orbits != null) {
						reps.add(graph.toString());
						numberOrbits = numberOrbits + orbits.size();
						ps.append(graph.toPS());
						ps2.append(graph.toGraphlet());
//						System.out.println(graph);
					}
				}
				incArray(array);
			}
		}

		ps.close();
		ps2.close();
//		System.out.println("Number of graphlets: " + numberGraphlets);
//		System.out.println("Number of orbits: " + numberOrbits);
	}

	/**
	 * Increments the binary number given.
	 * 
	 * @param array
	 *            The binary number to be incremented.
	 */
	public static void incArray(boolean[] array) {
		int i = array.length - 1;
		while (i >= 0 && array[i]) {
			array[i] = false;
			i--;
		}
		if (i >= 0) {
			array[i] = true;
		}
	}

}
