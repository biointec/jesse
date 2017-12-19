package codegenerating;

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
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import equations.Equation;
import equations.EquationGenerator;
import equations.EquationManager;
import equations.RHSTermComparator;
import equations.SelectiveEquationManager;
import graph.DanglingElement;
import graph.DanglingGraph;
import graph.DanglingList;
import orbits.OrbitIdentification;
import orbits.OrbitRepresentative;
import progress.TaskMonitor;
import tree.AddEdgeNode;
import tree.AddNodeNode;
import tree.ConditionNode;
import tree.OrbitTree;
import tree.TreeNode;

/**
 * TreeInterpreter which counts the orbits in a graph using both an orbit tree
 * and equations.
 * 
 * @author Ine Melckenbeeck
 *
 */
public class ListInterpreter implements TreeInterpreter {

	private Deque<TreeNode> nodes;
	private DanglingGraph g;
	private int[] graphlet;
	private List<DanglingList<DanglingList<Integer>>> graphlets;
	private int order;
	private OrbitTree ot;
	private List<List<List<Integer>>> connections;
	private List<List<Integer>> mingraphlet;
	private List<List<Integer>> minus;
	private String filename = null;
	private PrintWriter pw;

	private TaskMonitor taskMonitor;

	/**
	 * Creates a new DanglingInterpreter for the given graph, graphlet size and
	 * OrbitTree.
	 * 
	 * @param g
	 *            The graph in which the orbits are counted.
	 * @param order
	 *            The size of graphlets whose orbits are counted.
	 * @param ot
	 *            The orbit tree for counting.
	 */
	public ListInterpreter(DanglingGraph g, OrbitTree ot, String filename) {
		this.g = g;
		int size = ot.getOrder();
		graphlet = new int[size];
		graphlets = new ArrayList<>();
		for (int i = 0; i < OrbitIdentification.getNOrbitsTotal(size + 1); i++) {
			graphlets.add(new DanglingList<>());
		}
		order = size;
		this.ot = ot;
		ot.print();
		nodes = new LinkedList<TreeNode>();
		this.filename = filename;
		// preprocessEquations();
	}

	private void reset() {
		graphlet = new int[order];
		graphlets = new ArrayList<>();
		for (int i = 0; i < OrbitIdentification.getNOrbitsTotal(order + 1); i++) {
			graphlets.add(new DanglingList<>());
		}
	}

	@Override
	public void addNodeAction(AddNodeNode ann) {
		if (ann.getRepID() >= 0) {
			// counts[ann.getRepID()] += 1;

			List<Set<Integer>> sym = ann.getOrbitRepresentative().getCosetreps();

			boolean ok = true;
//			System.out.println();
			for (int j=0;j<sym.size();j++) {
				for(int i : sym.get(j)) {
					ok = ok && (graphlet[i] > graphlet[j+1]);
				}
			}
			if (ok) {
				// DanglingList<Integer> l = new DanglingList<Integer>();
				// for (int y = 0; y < ann.getDepth() + 1; y++) {
				// l.add(graphlet[y]);
				// }
				// graphlets.get(ann.getRepID()).addInOrder(l);
				pw.print(ann.getRepID());
				for (int y = 0; y < ann.getDepth() + 1; y++) {
					pw.print("\t"+g.getName(graphlet[y]));
				}
				pw.println();
			}
		}
		for (int i = 0; i < graphlet.length; i++) {
			TreeNode child = ann.getChild(i);
			if (child != null) {
				DanglingElement<Integer> n = g.getNeighbors(graphlet[i]).getHead();
				outer: while (n != null) {
					for (int x = 0; x < ann.getDepth() + 1; x++) {
						if (graphlet[x] == (n.getValue())) {
							n = n.getNext();
							continue outer;
						}
					}

					graphlet[ann.getDepth() + 1] = n.getValue();

					child.walkTree();
					n = n.getNext();
				}
			}
		}
	}

	@Override
	public void addEdgeAction(AddEdgeNode aen) {
		TreeNode truechild = aen.getChild(true);
		TreeNode falsechild = aen.getChild(false);
		if (truechild != null && g.areConnected(graphlet[aen.getDepth() + 1], graphlet[aen.getEdge()])) {
			truechild.walkTree();
		}
		if (falsechild != null && !g.areConnected(graphlet[aen.getDepth() + 1], graphlet[aen.getEdge()])) {
			falsechild.walkTree();
		}

	}

	@Override
	public void conditionAction(ConditionNode cn) {
		// if (graphlet[cn.getFirst()] > graphlet[cn.getSecond()]) {
		cn.getChild().walkTree();
		// }
	}

	@Override
	public long[][] run() {
		if (taskMonitor != null) {
			taskMonitor.setProgress(0);
			taskMonitor.setStatusMessage("Counting orbits");
		}
		try {
			pw = new PrintWriter(filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			pw = null;
		}
		ot.setInterpreter(this);
		List<List<DanglingList<DanglingList<Integer>>>> result = new ArrayList<>();
		for (int i = 0; i < g.order(); i++) {
			if (taskMonitor != null) {
				taskMonitor.setProgress((double) i / g.order());
				taskMonitor.setStatusMessage("Counting orbits for node " + i);
			}
			reset();
			g.getNeighbors(i);
			graphlet[0] = i;
			nodes.addLast(ot.getRoot());
			while (!nodes.isEmpty()) {
				TreeNode tn = nodes.removeFirst();
				tn.walkTree();
			}
			for (int j = 0; j < OrbitIdentification.getNOrbitsTotal(order - 1); j++) {
				if (taskMonitor != null && taskMonitor.isCancelled()) {
					return null;
				}
			}
			// result.add(graphlets);
			// System.out.println(graphlets);
		}
		pw.close();
		return null;
	}

	// public EquationManager getEquationManager() {
	// return em;
	// }

	public void write(String filename, long[][] result) {
		try {
			PrintWriter ps = new PrintWriter(new BufferedWriter(new FileWriter(filename)));

			for (int i = 0; i < result.length; i++) {
				ps.print(g.getName(i));
				for (int j = 0; j < result[i].length; j++) {
					ps.print("\t" + result[i][j]);
				}
				ps.println();
			}
			ps.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set a task monitor the run() method can report its progress to
	 * 
	 * @param tm
	 */
	public void setTaskMonitor(TaskMonitor tm) {
		this.taskMonitor = tm;
	}

	// private void preprocessEquations() {
	// connections = new ArrayList<>(OrbitIdentification.getNOrbitsForOrder(order));
	// mingraphlet = new ArrayList<>(OrbitIdentification.getNOrbitsForOrder(order));
	// minus = new ArrayList<>(OrbitIdentification.getNOrbitsForOrder(order));
	// int offset = OrbitIdentification.getNOrbitsTotal(order - 1);
	// int max = OrbitIdentification.getNOrbitsForOrder(order);
	// for (int i = 0; i < max; i++) {
	// connections.add(new ArrayList<List<Integer>>());
	// mingraphlet.add(new ArrayList<Integer>());
	// minus.add(new ArrayList<Integer>());
	// List<Equation> eq = em.getEquationsByRHS(OrbitIdentification.getOrbit(offset
	// + i));
	// if (eq != null) {
	// for (Equation e : eq) {
	// for (List<Integer> l : e.getRhsConnected()) {
	// connections.get(i).add(l);
	// mingraphlet.get(i).add(e.getLowestOrbit());
	// minus.get(i).add(e.getMinus() / e.getRhsConnected().size());
	// }
	// }
	// }
	// }
	// }
}
