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

/**
 * Class for TreeNodes that are used for symmetry-breaking constraints when
 * counting the OrbitRepresentatives.
 * 
 * @author Ine Melckenbeeck
 *
 */
public class ConditionNode extends TreeNode {

	private int first, second;
	private TreeNode child;

	/**
	 * Creates a new ConditionNode with the specified parameters.
	 * 
	 * @param parent
	 * @param first
	 * @param second
	 * @param tree
	 */
	ConditionNode(TreeNode parent, int first, int second) {
		super(parent);
		this.first = first;
		this.second = second;
	}

	@Override
	void printTree(String s) {
		System.out.println(s + "ConditionNode " + first + "<" + second);
		if (child != null)
			child.printTree(s + " ");
	}

	@Override
	void removeChild(TreeNode t) {
		if (t.equals(child)) {
			child = null;
		}
	}

	@Override
	public
	boolean isLeaf() {
		return child == null;
	}

	@Override
	public List<TreeNode> getChildren() {
		List<TreeNode> result = new ArrayList<TreeNode>();
		if (child != null)
			result.add(child);
		return result;
	}

	@Override
	void replaceChild(TreeNode original, TreeNode newNode) {
		if (original.equals(child)) {
			child = newNode;
			newNode.setParent(this);
		}
	}

	/**
	 * Inserts this ConditionNode in the place of the specified TreeNode. It
	 * will become the child of the given TreeNode's parent, with the same
	 * parameters as the given TreeNode. The given TreeNode will become this
	 * ConditionNode's child.
	 * 
	 * @param tn
	 *            The TreeNode in whose place this ConditionNode will be
	 *            inserted.
	 */
	void insert(TreeNode tn) {
		if (tn.getParent() != null) {
			this.parent = tn.parent;
			tn.parent.replaceChild(tn, this);
			tn.setParent(this);
			this.child = tn;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + first;
		result = prime * result + second;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConditionNode other = (ConditionNode) obj;
		if (first != other.first)
			return false;
		if (second != other.second)
			return false;
		if (child == null)
			return other.child == null;
		if (!child.equals(other.child))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String result = "ConditionNode " + first + "<" + second;
		return result;
	}

	/**
	 * Calls the TreeInterpreter's conditionAction.
	 */
	@Override
	public void walkTree() {
		tree.getInterpreter().conditionAction(this);
	}

	public int getFirst() {
		return first;
	}

	public int getSecond() {
		return second;
	}

	/**
	 * Returns this ConditionNode's child TreeNode.
	 * 
	 * @return this ConditionNode's child TreeNode.
	 */
	public TreeNode getChild() {
		return child;
	}

	@Override
	StringBuffer write() {
		StringBuffer result = new StringBuffer();

		result.append("c");
		result.append(" ");
		result.append(first);
		result.append(" ");
		result.append(second);
		result.append("\n");
		result.append(child.write());
		result.append("\n");

		result.append("/c");
		// result.append("\n");
		return result;
	}

	void setChild(TreeNode child) {
		this.child = child;
	}

//	private static void simplify(List<ConditionNode> conditions) {
//		if (conditions.size() > 2)
//			for (int i = 0; i < conditions.size(); i++) {
//				for (int j = 0; j < conditions.size(); j++) {
//					for (int k = 0; k < conditions.size(); k++) {
//						if (conditions.get(i).getSecond() == conditions.get(j).getFirst()
//								&& conditions.get(i).getFirst() == conditions.get(k).getFirst()
//								&& conditions.get(j).getSecond() == conditions.get(k).getSecond()) {
//							conditions.remove(k);
//							k--;
//							if (i > k) {
//								i--;
//							}
//							if (j > k) {
//								j--;
//							}
//						}
//					}
//				}
//			}
//	}

}