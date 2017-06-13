package codegenerating;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import equations.Equation;
import equations.EquationGenerator;
import equations.EquationManager;
import graph.DanglingElement;
import graph.DanglingGraph;
import graph.DanglingList;
import orbits.OrbitIdentification;
import orbits.OrbitRepresentative;
import tree.AddEdgeNode;
import tree.AddNodeNode;
import tree.ConditionNode;
import tree.OrbitTree;
import tree.TreeNode;

/**
 * TreeInterpreter which counts the orbits in a graph using both an orbit tree and equations. 
 * @author Ine Melckenbeeck
 *
 */
public class DanglingInterpreter implements TreeInterpreter {

	private Deque<TreeNode> nodes;
	private DanglingGraph g;
	private int[] graphlet;
	private long[] counts;
	private int order;
	private OrbitTree ot;
	private EquationManager em;
	private List<List<List<Integer>>> connections;
	private List<List<Integer>> mingraphlet;
	private List<List<Integer>> minus;

	/**
	 * Creates a new DanglingInterpreter for the given graph, graphlet size and OrbitTree.
	 * @param g The graph in which the orbits are counted.
	 * @param size The size of graphlets whose orbits are counted.
	 * @param ot The orbit tree for counting.
	 */
	public DanglingInterpreter(DanglingGraph g, OrbitTree ot) {
		this.g = g;
		int size = ot.getOrder();
		graphlet = new int[size];
		counts = new long[OrbitIdentification.getNOrbitsTotal(size + 1)];
		order = size;
		this.ot = ot;
		em = EquationGenerator.generateEquations(size + 1, ot.getLeaves());
		nodes = new LinkedList<TreeNode>();
		preprocessEquations();
	}

	
	private void reset() {
		graphlet = new int[order];
		counts = new long[OrbitIdentification.getNOrbitsTotal(order + 1)];
	}

	private void completeGraphlet() {
		DanglingList<Integer> dl = g.getNeighbors(graphlet[0]);
		int max = 0;
		for (int i = 1; i < order; i++) {
			if (graphlet[i] > max) {
				max = graphlet[i];
			}
		}
		int i = 1;
		Stack<DanglingElement<Integer>> elements = new Stack<>();
		while (i < order && !dl.isEmpty()) {
			DanglingList<Integer> other = g.getNeighbors(graphlet[i]);
			DanglingElement<Integer> e1 = dl.getHead();
			while (e1 != null && e1.getValue() <= max) {
				dl.remove(e1);
				elements.add(e1);
				e1 = e1.getNext();
			}
			DanglingElement<Integer> e2 = other.getHead();
			while (e1 != null && e2 != null) {
				if (e1.getValue() > e2.getValue()) {
					e2 = e2.getNext();
				} else if (e2.getValue() > e1.getValue()) {
					dl.remove(e1);
					elements.add(e1);
					e1 = e1.getNext();
				} else {
					e1 = e1.getNext();
					e2 = e2.getNext();
				}
			}
			while (e1 != null) {
				dl.remove(e1);
				elements.add(e1);
				e1 = e1.getNext();
			}
			i++;
		}
		counts[counts.length - 1] += dl.getSize();
		while (!elements.isEmpty()) {
			dl.restore(elements.pop());
		}
	}

	@Override
	public void addNodeAction(AddNodeNode ann) {
		if (ann.getRepID() >= 0) {
			counts[ann.getRepID()] += 1;
		}
		if (ann.isLeaf()) {
			if (ann.getOrbitRepresentative().isComplete()) {
				completeGraphlet();
			}
			DanglingList<Integer> dl = new DanglingList<Integer>();
			int index = ann.getRepID() - OrbitIdentification.getNOrbitsTotal(order - 1);
			for (int j = 0; j < connections.get(index).size(); j++) {
				List<Integer> l = connections.get(index).get(j);
				dl.clear();
				for (int i : l) {
					dl.addInOrder(graphlet[i]);
				}
				int a = mingraphlet.get(index).get(j);
				counts[a] += g.getNCommon(dl);
				assert (counts[a] >= 0);
				counts[a] -= minus.get(index).get(j);
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
					n = n.getNext();
					child.walkTree();
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
		if (graphlet[cn.getFirst()] > graphlet[cn.getSecond()]) {
			cn.getChild().walkTree();
		}
	}

	@Override
	public long[][] run() {
		ot.setInterpreter(this);
		long[][] result = new long[g.order()][];
		for (int i = 0; i < g.order(); i++) {
			reset();
			g.getNeighbors(i);
			graphlet[0] = i;
			nodes.addLast(ot.getRoot());
			while (!nodes.isEmpty()) {
				TreeNode tn = nodes.removeFirst();
				tn.walkTree();
			}
			for (int j = 0; j < OrbitIdentification.getNOrbitsTotal(order - 1); j++) {
				counts[j] /= OrbitIdentification.getOrbit(j).symmetry();
			}
			for (int j = OrbitIdentification.getNOrbitsTotal(order + 1) - 2; j >= OrbitIdentification
					.getNOrbitsTotal(order); j--) {
				Equation e = em.getEqu().get(j - OrbitIdentification.getNOrbitsTotal(order));
				Iterator<OrbitRepresentative> it = e.getLhs().keySet().iterator();
				it.next();
				while (it.hasNext()) {
					OrbitRepresentative or = it.next();
					counts[j] -= counts[or.identify()] * e.getLhs().get(or);
				}
				counts[j] /= e.getLhs().get(OrbitIdentification.getOrbit(e.getLowestOrbit()));
			}
			result[i] = counts;
		}
		return result;
	}

	private void preprocessEquations() {
		connections = new ArrayList<>(OrbitIdentification.getNOrbitsForOrder(order));
		mingraphlet = new ArrayList<>(OrbitIdentification.getNOrbitsForOrder(order));
		minus = new ArrayList<>(OrbitIdentification.getNOrbitsForOrder(order));
		int offset = OrbitIdentification.getNOrbitsTotal(order - 1);
		int max = OrbitIdentification.getNOrbitsForOrder(order);
		for (int i = 0; i < max; i++) {
			connections.add(new ArrayList<List<Integer>>());
			mingraphlet.add(new ArrayList<Integer>());
			minus.add(new ArrayList<Integer>());
			List<Equation> eq = em.getEquationsByRHS(OrbitIdentification.getOrbit(offset + i));
			if (eq != null) {
				for (Equation e : eq) {
					for (List<Integer> l : e.getRhsConnected()) {
						connections.get(i).add(l);
						mingraphlet.get(i).add(e.getLowestOrbit());
						minus.get(i).add(e.getMinus() / e.getRhsConnected().size());
					}
				}
			}
		}
	}

	
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

}
