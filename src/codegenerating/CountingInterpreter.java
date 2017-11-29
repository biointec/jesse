package codegenerating;

import graph.DanglingElement;
import graph.DanglingGraph;
import orbits.OrbitIdentification;
import tree.AddEdgeNode;
import tree.AddNodeNode;
import tree.ConditionNode;
import tree.OrbitTree;
import tree.TreeNode;

/**
 * This class counts the orbits of all graphlets up to a given order touching each node in a DanglingGraph directly,
 * using an OrbitTree of the same order.
 * 
 * @author Ine Melckenbeeck
 *
 */
public class CountingInterpreter implements TreeInterpreter {

	private DanglingGraph g;
	private int[] graphlet;
	private long[] counts;
	private int order;
	private OrbitTree ot;

	/**
	 * Creates a new CountingInterpreter which will count the orbits of
	 * graphlets of the given order in the given DanglingGraph, using the given
	 * OrbitTree.
	 * 
	 * @param g
	 *            The DanglingGraph in which the orbits will be counted.
	 * @param order
	 *            The order of graphlets for which the orbits will be counted.
	 * @param ot
	 *            The used OrbitTree.
	 */
	public CountingInterpreter(DanglingGraph g, int order, OrbitTree ot) {
		this.g = g;
		graphlet = new int[order];
		counts = new long[OrbitIdentification.getNOrbitsTotal(order)];
		this.order = order;
		this.ot = ot;
	}

	/**
	 * Resets this CountingInterpreter's internal variables.
	 */
	private void reset() {
		graphlet = new int[order];
		counts = new long[OrbitIdentification.getNOrbitsTotal(order)];
	}

	@Override
	/**
	 * Counts the current tree node's orbit, then activates the walkTree method of all its
	 * children with all possible nodes in the DanglingGraph.
	 */
	public void addNodeAction(AddNodeNode ann) {
		if (ann.getRepID() >= 0) {
			counts[ann.getRepID()] += 1;
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
	/**
	 * Checks whether the node that was last added to the graphlet is connected
	 * to the end node in the current AddEdgeNode, then activates the walkTree method of
	 * the appropriate child of the AddEdgeNode.
	 */
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
	/**
	 * @inheritDoc Activates the ConditionNode's child's walkTree method if the
	 *             graphlet's nodes confirm to its condition.
	 */
	public void conditionAction(ConditionNode cn) {
		if (graphlet[cn.getFirst()] > graphlet[cn.getSecond()]) {
			cn.getChild().walkTree();
		}
	}

	@Override
	public long[][] run() {
		ot.setInterpreter(this);
		long[][]result = new long[g.order()][];
		for (int i = 0; i < g.order(); i++) {
//		int i=0;
			reset();
			graphlet[0] = i;
			ot.getRoot().walkTree();
			for (int j = 0; j < OrbitIdentification.getNOrbitsTotal(order - 1); j++) {
				counts[j] /= OrbitIdentification.getOrbit(j).symmetry();
			}
			result[i]=counts;
		}
		return result;
	}
}
