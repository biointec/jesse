package codegenerating;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import graph.DanglingGraph;
import graph.GraphReader;
import graphletgenerating.Program;
import orbits.OrbitIdentification;
import tree.OrbitTree;

public class UI {

	public static void main(String[] args){

		Scanner s = new Scanner(System.in);

		System.out.println("Welcome to the Jesse orbit counter.");
		System.out.println("Which order of graphlets would you like to count?");
		int order = s.nextInt();
		System.out.println("Do you want to generate the orbits? (y/n)");

		String orbitname = "Przulj.txt";
		boolean generate = s.next().toLowerCase().charAt(0) == 'y';
		boolean saveorbits = false;
		if (generate) {
			System.out.println("Do you want to save the orbits? (y/n)");
			saveorbits = s.next().toLowerCase().charAt(0) == 'y';
			if (saveorbits) {
				System.out.println("Enter the file name for the orbit numbering:");
				orbitname = s.next();
			} else
				orbitname = "temp.txt";

		} else {
			if (order <= 7) {
				System.out.println("Do you want to use the standard file for the orbit numbering? (y/n)");
				if (s.next().toLowerCase().charAt(0) != 'y') {
					System.out.println("Enter the file name for the orbit numbering:");
					orbitname = s.next();
				}
			} else {
				System.out.println("Enter the file name for the orbit numbering:");
				orbitname = s.next();
			}
		}

		System.out.println("Do you want to generate a new orbit tree? (y/n)");
		boolean generateTree = s.next().toLowerCase().charAt(0) == 'y';
		boolean saveTree = false;
		String treeName = "";
		if (generateTree) {
			System.out.println("Do you want to save the tree? (y/n)");
			saveTree = s.next().toLowerCase().charAt(0) == 'y';
			if (saveTree) {
				System.out.println("Enter the file name for the orbit tree:");
				treeName = s.next();
			}
		} else {
			System.out.println("Enter the file name for the orbit tree:");
			treeName = s.next();
		}

		System.out.println("Enter the file name for the graph:");
		String graphname = s.next();
		System.out.println("Enter the file name for the results:");
		String resultname = s.next();
		s.close();
			run(generate, order, orbitname, generateTree, saveTree, treeName, graphname, resultname);
		
	}
	
	public static void run(boolean generate,int order,String orbitname, boolean generateTree, boolean saveTree, String treeName, String graphname, String resultname){
		long start;
		if (generate) {
			System.out.println("Generating graphlets...");
			start = System.nanoTime();
			try {
				Program.generateGraphlets(order, orbitname);
			} catch (IOException e) {
				System.out.println("Could not save graphlets.");
				return;
			}
			System.out.println("Generated graphlets in " + (System.nanoTime() - start) * 1e-9 + "s");
		}
		System.out.println("Reading graphlets...");
		start = System.nanoTime();
		OrbitIdentification.readGraphlets(orbitname, order);
		System.out.println("Read graphlets in " + (System.nanoTime() - start) * 1e-9 + "s");
		OrbitTree tree;
		if (generateTree) {
			System.out.println("Generating tree...");
			start = System.nanoTime();
			tree = new OrbitTree(order - 1);
			System.out.println("Generated tree in " + (System.nanoTime() - start) * 1e-9 + "s");
			if (saveTree) {
				tree.write(treeName);
			}
		} else {
			tree = new OrbitTree(treeName);
		}
		System.out.println("Reading graph...");
		start = System.nanoTime();
		DanglingGraph g = GraphReader.readGraph(graphname);
		System.out.println("Read graph in " + (System.nanoTime() - start) * 1e-9 + "s");
		System.out.println("Calculating common neighbours...");
		start = System.nanoTime();
		g.calculateCommons(order - 2);
		System.out.println("Calculated common neighbours in " + +(System.nanoTime() - start) * 1e-9 + "s");
		DanglingInterpreter di = new DanglingInterpreter(g,  tree);
		System.out.println("Counting orbits...");
		start = System.nanoTime();
		long[][] result = di.run();
		System.out.println("Counted orbits in " + +(System.nanoTime() - start) * 1e-9 + "s");
		System.out.println("Saving results...");
		start = System.nanoTime();
		di.write(resultname, result);
		System.out.println("Saved results in " + +(System.nanoTime() - start) * 1e-9 + "s");
	}

}