package tree;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import orbits.OrbitRepresentative;

/**
 * Class for TreeNodes which represent adding a node to an existing
 * OrbitRepresentative. The newly added node will connected to one existing node
 * in the OrbitRepresentative. All different positions within the
 * OrbitRepresentative will give rise to a different child for this AddNodeNode.
 * 
 * @author Ine Melckenbeeck
 *
 */
public class AddNodeNode extends AddNode implements Comparable<AddNodeNode> {
	private SortedMap<Integer, TreeNode> children;

	/**
	 * Creates a new AddNodeNode with the specified parameters.
	 * 
	 * @param rep
	 *            This AddNodeNode's OrbitRepresentative.
	 * @param parent
	 *            This AddNodeNode's parent TreeNode.
	 * @param tree
	 *            This AddNodeNodes's OrbitTree.
	 */
	AddNodeNode(OrbitRepresentative rep, TreeNode parent) {
		super(parent, rep);
		children = new TreeMap<Integer, TreeNode>();
	}
	
	AddNodeNode(OrbitRepresentative rep, OrbitTree ot){
		super(rep, ot);
		children = new TreeMap<Integer,TreeNode>();
	}

	@Override
	void printTree(String s) {
		System.out.println(s + "AddNodeNode " + rep + (children.isEmpty() ? "!" : ""));
		for (TreeNode tn : children.values()) {
			tn.printTree(s + " ");
		}
	}

	/**
	 * Returns the child that corresponds to adding a new node connected to the
	 * specified node.
	 * 
	 * @param node
	 *            The node in the OrbitRepresentative to which the new node is
	 *            connected.
	 * @return
	 */
	public TreeNode getChild(int node) {
		return children.get(node);
	}

	/**
	 * Add a new child to this AddNodeNode
	 * 
	 * @param edge
	 *            The number of the node to which the newly added node is
	 *            connected when making the child's OrbitRepresentative.
	 * @param n
	 *            The new child TreeNode.
	 */
	void addChild(int edge, TreeNode n) {
		children.put(edge, n);
	}

	@Override
	public String toString() {
		return "AddNodeNode " + super.toString();
	}

	@Override
	public int compareTo(AddNodeNode arg0) {
		return arg0.rep.compareTo(rep);
	}

	@Override
	void removeChild(TreeNode t) {
		for (int i = 0; i < rep.order(); i++) {
			if (t.equals(children.get(i))) {
				children.remove(i);
			}
		}
	}

	@Override
	public boolean isLeaf() {
		return children.isEmpty();
	}

	@Override
	public List<TreeNode> getChildren() {
		return new ArrayList<TreeNode>(children.values());
	}

	@Override
	void replaceChild(TreeNode tn, TreeNode newNode) {
		for (int i = 0; i < rep.order(); i++) {
			if (tn.equals(children.get(i))) {
				children.put(i, newNode);
			}
		}
		newNode.setParent(this);
	}

	/**
	 * Calls the TreeInterpreter's addNodeAction.
	 */
	@Override
	public void walkTree() {
		tree.getInterpreter().addNodeAction(this);

	}

	@Override
	void updateDepth() {
		if (parent != null) {
			depth = parent.getDepth() + 1;
		}
		for (TreeNode tn : getChildren()) {
			tn.updateDepth();
		}
	}
	
	@Override
	StringBuffer write(){
		StringBuffer result = new StringBuffer();
		
		result.append("n");
		result.append("\n");
		for(int i : children.keySet()){
			result.append(i);
			result.append(" ");
			result.append(children.get(i).write());
			result.append("\n");
		}
		result.append("/n");
//		result.append("\n");
		return result;
	}

}
