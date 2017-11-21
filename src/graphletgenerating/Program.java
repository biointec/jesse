package graphletgenerating;

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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

import progress.TaskMonitor;

public class Program {

	public static int order = 0;
	
	private static TaskMonitor taskMonitor;

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
		generateGraphlets(neworder,"graphlets-"+neworder);
//		PrintWriter ps2 = new PrintWriter(new BufferedWriter(new FileWriter("graphlets-" + order + ".txt")));
//		PrintWriter ps = new PrintWriter(new BufferedWriter(new FileWriter("graphlets-" + order + ".ps")));
//		ps.append("%!PS\n/Times-Roman findfont\n10 scalefont\nsetfont\n");
//
//		int numberGraphlets = 0;
//		int numberOrbits = 0;
//
//		for (int j = 2; j <= neworder; j++) {
//			order = j;
//			boolean[] array = new boolean[order * (order - 1) / 2];
//			for (int i = 0; i < array.length; i++)
//				array[i] = false;
//
//			SortedSet<String> reps = new TreeSet<String>();
//			for (int i = 0; i < Math.pow(2, array.length); i++) {
//				if (i % 1000 == 0)
//					System.out.println("Iteration " + i + "/" + (int) Math.pow(2, array.length));
//				Graph graph = new Graph(array);
//				if (graph.isGraphlet()) {
//					SortedSet<String> orbits = graph.permute(reps);
//					if (orbits != null) {
//						reps.add(graph.toString());
//						numberGraphlets++;
//						numberOrbits = numberOrbits + orbits.size();
//						ps.append(graph.toPS());
//						ps2.append(graph.toGraphlet());
//					}
//				}
//				incArray(array);
//			}
//		}
//
//		ps.close();
//		ps2.close();
//		System.out.println("Number of graphlets: " + numberGraphlets);
//		System.out.println("Number of orbits: " + numberOrbits);
	}

	public static void generateGraphlets(int xx, String filename) throws IOException {		
		File txtFile = new File(filename + ".txt");
		File psFile = new File(filename + ".ps");
		PrintWriter psWriter = new PrintWriter(new BufferedWriter(new FileWriter(psFile)));
		PrintWriter txtWriter = new PrintWriter(new BufferedWriter(new FileWriter(txtFile)));
		psWriter.append("%!PS\n/Times-Roman findfont\n10 scalefont\nsetfont\n");

		int numberOrbits = 0;
		for (int j = 2; j <= xx; j++) {
			order = j;
			if (taskMonitor != null){
				taskMonitor.setStatusMessage("Saving orbits of order "+order);
			}
			boolean[] array = new boolean[order * (order - 1) / 2];
			for (int i = 0; i < array.length; i++)
				array[i] = false;

			SortedSet<String> reps = new TreeSet<String>();
			for (int i = 0; i < Math.pow(2, array.length); i++) {
				if (taskMonitor != null&& j==xx){
					taskMonitor.setProgress(i/Math.pow(2,array.length));
				}
//				if (i % 1000 == 0)
//					System.out.println("Iteration " + i + "/" + (int) Math.pow(2, array.length));
				Graph graph = new Graph(array);
				if (graph.isGraphlet()) {
					if (taskMonitor!=null && taskMonitor.isCancelled()) {
						//clean up before cancelling
						txtWriter.close();
						psWriter.close();
						txtFile.delete();
						psFile.delete();
						return;
					}
					SortedSet<String> orbits = graph.permute(reps);
					if (orbits != null) {
						reps.add(graph.toString());
						numberOrbits = numberOrbits + orbits.size();
						psWriter.append(graph.toPS());
						txtWriter.append(graph.toGraphlet());
					}
				}
				incArray(array);
			}
		}

		txtWriter.close();
		psWriter.close();
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

	/**
	 * 
	 * @param taskMonitor allow generateGraphlets() to report it progress and check for cancellation
	 */
	public static void setTaskMonitor(TaskMonitor taskMonitor) {
		Program.taskMonitor = taskMonitor;
	}
	
}
