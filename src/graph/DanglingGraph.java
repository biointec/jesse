package graph;

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

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Class that represents a graph using DanglingLists. For each node, a sorted
 * DanglingList of integers is kept, which correspond to the neighbours of that
 * node.
 * 
 * @author Ine Melckenbeeck
 *
 */
public class DanglingGraph {

	private List<DanglingList<Integer>> nodes;
	private List<String> nodeNames;
	private Map<String, Integer> inverseNodeNames;
	// private Map<DanglingList<Integer>, Integer> nCommon;

	private Map<Long, Integer> nCommon;
	private List<Map<DanglingList<Integer>, Integer>> commonsBySize;
	private List<Map<DanglingList<Integer>, DanglingList<Integer>>> commonListsBySize;
	private long[] sizes;
	private boolean[][] matrix;
	private static boolean useLists = false;
	// private int size;

	/**
	 * Creates a new DanglingGraph, without any nodes or edges.
	 */
	public DanglingGraph() {
		sizes = new long[0];
		// size = 0;
		nodeNames = new ArrayList<String>(0);
		nodes = new ArrayList<DanglingList<Integer>>();
		inverseNodeNames = new HashMap<String, Integer>();
		matrix = null;
		nCommon = null;
		commonsBySize = null;
		commonListsBySize = null;
	}

	public DanglingGraph(boolean useLists) {
		sizes = new long[0];
		// size = 0;
		nodeNames = new ArrayList<String>(0);
		nodes = new ArrayList<DanglingList<Integer>>();
		inverseNodeNames = new HashMap<String, Integer>();
		matrix = null;
		nCommon = null;
		commonsBySize = null;
		commonListsBySize = null;
		DanglingGraph.useLists = useLists;
	}

	/**
	 * Adds a new node to the DanglingGraph.
	 * 
	 * @param name
	 *            The node's name.
	 */
	public void addNode(String name) {
		if (!nodeNames.contains(name)) {
			inverseNodeNames.put(name, nodes.size());
			nodes.add(new DanglingList<Integer>());
			nodeNames.add(name);
		}
	}

	/**
	 * Adds a new edge to the DanglingGraph between the two given nodes, specified
	 * by name. If either node is not in the graph, it is added to the graph as
	 * well.
	 * 
	 * @param node1
	 *            Name of first node
	 * @param node2
	 *            Name of second node
	 */
	public void addEdge(String node1, String node2) {
		int n1;
		try {
			n1 = inverseNodeNames.get(node1);
		} catch (NullPointerException n) {
			n1 = nodes.size();
			addNode(node1);
		}
		int n2;
		try {
			n2 = inverseNodeNames.get(node2);
		} catch (NullPointerException n) {
			n2 = nodes.size();
			addNode(node2);
		}
		addEdge(n1, n2);
	}

	/**
	 * Adds a new edge to the graph, between the two nodes specified by the given
	 * indices. Throws a NullPointerException if these indices are greater than the
	 * number of nodes in the graph.
	 * 
	 * @param n1
	 *            Index of first node.
	 * @param n2
	 *            Index of second node.
	 */
	public void addEdge(int n1, int n2) {

		nodes.get(n1).addInOrder(n2);
		nodes.get(n2).addInOrder(n1);
	}

	/**
	 * Creates the matrix representation of this graph, to allow for easier lookup
	 * of adjacency. Should be executed after every
	 */
	public void finalise() {
		// System.out.println(nodes);
		matrix = new boolean[nodes.size()][];
		for (int i = 0; i < nodes.size(); i++) {
			matrix[i] = new boolean[i];
			for (int j : nodes.get(i)) {
				// System.out.println(i+" "+j);
				if (j < i) {
					matrix[i][j] = true;
				} else {
					break;
				}
			}
		}
		DanglingList.setFactor(order());
	}

	public static void main(String[] args) {
		DanglingGraph g = new DanglingGraph();
		g.addNode("a");
		g.addNode("b");
		g.addEdge("b", "a");
		g.addNode("c");
		g.addEdge("c", "b");
		g.finalise();
		g.printMatrix();
	}

	/**
	 * Prints this graph's matrix representation to the console.
	 */
	public void printMatrix() {
		for (boolean[] a : matrix) {
			System.out.println(Arrays.toString(a));
		}
	}

	// public int size() {
	// return size;
	// }

	private static long combination(int n, int m) {
		long result = 1;
		for (int i = 0; i < m; i++) {
			result *= (n - i);
			result /= (i + 1);
		}
		return result;
	}

	/**
	 * Calculates the number of common neighbours of all sets of nodes of up to the
	 * given cardinality.
	 * 
	 * @param size
	 */
	public void calculateCommons(int size) {
		sizes = new long[size + 1];
		if (useLists) {
			commonListsBySize = new ArrayList<>();
			for (int i = 1; i <= size; i++) {
				sizes[i] += sizes[i - 1] + combination(nodes.size(), i);
				commonListsBySize.add(new HashMap<DanglingList<Integer>, DanglingList<Integer>>());
			}
		} else {

			nCommon = new HashMap<Long, Integer>();
			commonsBySize = new ArrayList<Map<DanglingList<Integer>, Integer>>();
			for (int i = 1; i <= size; i++) {
				sizes[i] += sizes[i - 1] + combination(nodes.size(), i);
				commonsBySize.add(new HashMap<DanglingList<Integer>, Integer>());
			}
		}

		int[] counters = new int[size];
		int index = 0;
		for (int i = 0; i < size; i++) {
			counters[i] = -1;
		}
		int[] numbers = new int[size];
		Stack<DanglingElement<Integer>> removed = new Stack<>();
		DanglingList<Integer> elements = new DanglingList<>();
		while (counters[0] < nodes.size() - 1 || index != 0) {
			counters[index]++;
			if (counters[index] >= nodes.size()) {
				for (int i = 0; i < numbers[index - 1]; i++) {
					elements.restore(removed.pop());
				}
				numbers[index - 1] = 0;
				counters[index] = 0;
				index--;
			} else {

				if (index == 0) {
					elements = nodes.get(counters[0]);
					DanglingList<Integer> l = new DanglingList<Integer>();
					l.add(counters[0]);
					if (useLists) {
						commonListsBySize.get(index).put(new DanglingList<>(l),
								new DanglingList<>(nodes.get(counters[0])));
					} else {
						nCommon.put(l.longHashCode(), nodes.get(counters[0]).size());
						commonsBySize.get(index).put(new DanglingList<Integer>(l), nodes.get(counters[0]).size());
					}
					if (size > 1) {
						counters[1] = counters[0];
						index = 1;
					}
				} else if (index == size - 1) {
					Stack<DanglingElement<Integer>> rest = elements.crossSection(nodes.get(counters[index]));
					if (!elements.isEmpty()) {
						DanglingList<Integer> l = new DanglingList<Integer>();
						for (int i = 0; i < size; i++) {
							l.add(counters[i]);
						}
						if (useLists) {
							commonListsBySize.get(index).put(new DanglingList<>(l), new DanglingList<>(elements));
						} else {
							nCommon.put(l.longHashCode(), elements.size());
							commonsBySize.get(index).put(new DanglingList<Integer>(l), elements.size());
						}
					}
					while (!rest.isEmpty()) {
						elements.restore(rest.pop());
					}
				} else {
					Stack<DanglingElement<Integer>> rest = elements.crossSection(nodes.get(counters[index]));
					int x = elements.size();
					if (x != 0) {
						numbers[index] = rest.size();
						removed.addAll(rest);
						DanglingList<Integer> l = new DanglingList<Integer>();
						for (int i = 0; i < index + 1; i++) {
							l.add(counters[i]);
						}
						if (useLists) {
							commonListsBySize.get(index).put(new DanglingList<>(l), new DanglingList<>(elements));
						} else {
							nCommon.put(l.longHashCode(), elements.size());
							commonsBySize.get(index).put(new DanglingList<Integer>(l), elements.size());
						}
						index++;
						counters[index] = counters[index - 1];
					} else {
						while (!rest.isEmpty()) {
							elements.restore(rest.pop());
						}
					}
				}
			}
		}
	}

	/**
	 * 
	 * @return The number of nodes in this graph.
	 */
	public int order() {
		return nodes.size();
	}

	/**
	 * 
	 * @param node
	 *            Index of the node.
	 * @return The neighbors of the given node.
	 */
	public DanglingList<Integer> getNeighbors(int node) {
		return nodes.get(node);
	}

	/**
	 * 
	 * @param a
	 *            Index of the first node.
	 * @param b
	 *            Index of the second node.
	 * @return True if the given nodes are neighbors.
	 */
	public boolean areConnected(int a, int b) {
		if (a == b || a < 0 || b < 0 || a >= order() || b >= order())
			return false;
		if (a > b)
			return matrix[a][b];
		else
			return matrix[b][a];

	}

	/**
	 * 
	 * @param l
	 *            A DanglingList of nodes' indices.
	 * @return The number of common neighbors of the nodes.
	 */
	public int getNCommon(DanglingList<Integer> l) {
		// Integer a = nCommon.get(l.longHashCode());
		if (l.size() == 0)
			return 0;
		if (l.size() == 1)
			return nodes.get(l.getHead().getValue()).size();
		Integer a = commonsBySize.get(l.size() - 1).get(l);
		return a == null ? 0 : a;
		// return 0;
	}

	public DanglingList<Integer> getListCommon(DanglingList<Integer> l) {
		// Integer a = nCommon.get(l.longHashCode());
		if (l.size() == 0)
			return new DanglingList<>();
		if (l.size() == 1)
			return nodes.get(l.getHead().getValue());
		DanglingList<Integer> a = commonListsBySize.get(l.size() - 1).get(l);
		return a == null ? new DanglingList<>() : a;
		// return 0;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (DanglingList<Integer> d : nodes) {
			sb.append(d);
			sb.append("\n");
		}
		return sb.toString();
	}

	/**
	 * Calculates the distance between all nodes in the graph using the
	 * Floyd-Warshall algorithm.
	 * 
	 * @return A 2-dimensional array containing the distances.
	 */
	public int[][] floydWarshall() {
		int order = order();
		int[][] result = new int[order][order];
		for (int i = 0; i < order; i++) {
			for (int j = 0; j < order; j++) {
				result[i][j] = order;
			}
			result[i][i] = 0;
			DanglingElement<Integer> a = nodes.get(i).getHead();
			while (a != null) {
				result[i][a.getValue()] = 1;
				a = a.getNext();
			}
		}
		for (int t = 0; t < order; t++) {
			for (int i = 0; i < order; i++) {
				for (int j = 0; j < order; j++) {
					int a = result[i][t] + result[t][j];
					if (result[i][j] > a) {
						result[i][j] = a;
					}
				}
			}
		}
		return result;
	}

	public String getName(int i) {
		return nodeNames.get(i);
	}

	public void print() {
		for (DanglingList<Integer> l : nodes) {
			System.out.println(l);
		}
	}

	public void save(String filename) {
		try {
			PrintWriter writer = new PrintWriter(filename, "UTF-8");
			for (int i = 0; i < nodes.size(); i++) {
				DanglingList<Integer> dl = nodes.get(i);
				DanglingElement<Integer> a = dl.getHead();
				while (a != null && a.getValue() < i) {
					writer.print(nodeNames.get(i));
					writer.print("\t");
					writer.println(nodeNames.get(a.getValue()));
					a = a.getNext();
				}
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getNodeNumber(String s) {
		return inverseNodeNames.get(s);
	}

	public static void setLists(boolean b) {
		useLists = b;
	}
	
	public Set<String> getNodeNames(){
		return inverseNodeNames.keySet();
	}
}
