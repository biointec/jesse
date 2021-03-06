package tree;

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

import java.util.ArrayList;
import java.util.List;
import orbits.OrbitRepresentative;

/**
 * Class for TreeNodes which represent adding an edge to an existing
 * OrbitRepresentative. The edge in question is specified by an int and will be
 * between the node at that number and the last node of the OrbitRepresentative.
 * This type of node can have 2 children: one where this edge is present and one
 * where it is not present.
 * 
 * @author Ine Melckenbeeck
 *
 */
public class AddEdgeNode extends AddNode implements Comparable<AddEdgeNode> {
	private TreeNode truechild;
	private TreeNode falsechild;
	private int edge;

	/**
	 * Creates a new AddEdgeNode with the specified fields.
	 * 
	 * @param rep
	 *            This AddEdgeNode's OrbitRepresentative.
	 * @param parent
	 *            This AddEdgeNode's parent TreeNode.
	 * @param edge
	 *            The number of the end node of the edge that may be added with
	 *            this AddEdgeNode. The start node is always the last node of
	 *            the OrbitRepresentative.
	 * @param tree
	 *            This AddEdgeNode's tree.
	 */
	AddEdgeNode(OrbitRepresentative rep, TreeNode parent, int edge) {
		super(parent, rep);
		this.edge = edge;
	}

	@Override
	void printTree(String s) {
		System.out.println(s + this);
		if (truechild != null)
			truechild.printTree(s + " ");
		if (falsechild != null)
			falsechild.printTree(s + " ");
	}

	/**
	 * Returns the number of the new edge's end node.
	 * 
	 * @return The number of the new edge's end node.
	 */
	public int getEdge() {
		return edge;
	}

	/**
	 * Adds a new child to this AddEdgeNode, either as the child when this
	 * node's new edge is present or when it is not.
	 * 
	 * @param n
	 *            The new child.
	 * @param edge
	 *            true if the added node is this AddEdgeNode's child with the
	 *            edge present, false with the edge absent.
	 */
	void addChild(TreeNode n, boolean edge) {
		if (edge) {
			truechild = n;
		} else {
			falsechild = n;
		}
	}

	@Override
	public String toString() {
		return "AddEdgeNode " + super.toString() + " " + edge;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + edge;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		AddEdgeNode other = (AddEdgeNode) obj;
		if (edge != other.edge)
			return false;
		return true;
	}

	void setEdge(int edge) {
		this.edge = edge;
	}

	@Override
	void removeChild(TreeNode t) {
		if (t.equals(truechild))
			truechild = null;
		if (t.equals(falsechild))
			falsechild = null;

	}

	@Override
	public boolean isLeaf() {
		return (truechild == null && falsechild == null);
	}

	@Override
	public List<TreeNode> getChildren() {
		List<TreeNode> s = new ArrayList<TreeNode>();
		if (truechild != null)
			s.add(truechild);
		if (falsechild != null)
			s.add(falsechild);
		return s;
	}

	@Override
	void replaceChild(TreeNode tn, TreeNode newNode) {
		if (tn.equals(truechild)) {
			truechild = newNode;
			newNode.setParent(this);
		} else if (tn.equals(falsechild)) {
			falsechild = newNode;
			newNode.setParent(this);
		}
	}

	@Override
	public int compareTo(AddEdgeNode arg0) {
		return arg0.getOrbitRepresentative().compareTo(rep);
	}

	/**
	 * Returns a child of this AddEdgeNode.
	 * 
	 * @param which
	 *            Determines whether the child with edge (true) or the child
	 *            without edge (false) is returned.
	 * @return The specified child of this AddEdgeNode.
	 */
	public TreeNode getChild(boolean which) {
		if (which)
			return truechild;
		return falsechild;
	}

	/**
	 * Calls the TreeInterpreter's addEdgeAction.
	 */
	@Override
	public void walkTree() {
		tree.getInterpreter().addEdgeAction(this);
	}

	@Override
	StringBuffer write(){
		StringBuffer result = new StringBuffer();
		
		result.append("e ");
		result.append(edge);
		result.append("\n");
		if(truechild!=null){
			result.append(true);
			result.append(" ");
			result.append(truechild.write());
			result.append('\n');
		}if(falsechild!=null){
			result.append(false);
			result.append(" ");
			result.append(falsechild.write());
			result.append('\n');
		}
		result.append("/e");
//		result.append('\n');
		return result;
	}
	
	@Override
	void prune(){
		if(truechild == null&&falsechild == null){
			remove();
			parent.prune();
		}
	}
}
