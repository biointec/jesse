package tree;

import java.util.List;

/**
 * Abstract class for all nodes in an OrbitTree.
 * @author Ine Melckenbeeck
 *
 */
public abstract class TreeNode {
	protected int depth;
	protected int nodeDepth;
	protected OrbitTree tree;
	protected TreeNode parent;

	/**
	 * Creates a new TreeNode with a given parent node, in a given tree.
	 * @param parent This node's parent node. This will be null for a root node.
	 * @param tree The tree in which this node is placed.
	 */
	TreeNode(TreeNode parent) {
		this.parent = parent;
		this.tree = parent.tree;
	}
	
	TreeNode(OrbitTree tree){
		this.tree=tree;
	}

	/**
	 * Removes a TreeNode from this node's children. If it is not a child of this node, nothing is changed.
	 * @param child The TreeNode to be removed.
	 */
	abstract void removeChild(TreeNode child);

	/**
	 * Removes this TreeNode from its parent's children.
	 */
	void remove() {
		if (parent != null)
			parent.removeChild(this);
	}

	/**
	 * Returns this TreeNode's parent node.
	 * @return This TreeNode's parent node.
	 */
	public TreeNode getParent() {
		return parent;
	}

	/**
	 * Changes this TreeNode's parent to the given TreeNode.
	 * @param parent This node's new parent.
	 */
	void setParent(TreeNode parent) {
		this.parent = parent;
	}

	/**
	 * Returns this TreeNode's depth in the tree, only counting AddNodeNodes. This is the number of AddNodeNodes in the path from the root to this node.
	 * @return This TreeNode's depth.
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * Returns true if this TreeNode has no children, false if it does.
	 * @return true if this TreeNode has no children, false otherwise.
	 */
	public abstract boolean isLeaf();

	/**
	 * Returns a List containing this TreeNode's children.
	 * @return a List containing this TreeNode's children.
	 */
	public abstract List<TreeNode> getChildren();

	/**
	 * If originalNode is a child of this node, replace it with newNode.
	 * @param originalNode The node to be replaced.
	 * @param newNode The node to replace it with.
	 */
	abstract void replaceChild(TreeNode originalNode, TreeNode newNode);

	/**
	 * Updates the node's depth, then recursively calls this method for all its children. This method sets each node's depth as equal to its parent's, so it must be overridden in AddNodeNode.
	 */
	void updateDepth() {
		if (parent != null) {
			depth = parent.getDepth();
		}
		for (TreeNode tn : getChildren()) {
			tn.updateDepth();
		}
	}

	/**
	 * Recursively prints the tree.
	 * @param s A string containing spaces for readability, should be an empty string on first call.
	 */
	abstract void printTree(String s);

	/**
	 * Calls the appropriate action from the tree's TreeInterpreter.
	 */
	public abstract void walkTree();
	
	/**
	 * Translates this TreeNode to a form that can be reused to reload the tree.
	 * @return The readable form as a StringBuffer.
	 */
	abstract StringBuffer write();
	
	/**
	 * Prunes this branch of the tree
	 */
	void prune(){
		if(parent!=null)
		parent.prune();
	}
	
}
