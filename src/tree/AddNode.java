package tree;

import orbits.OrbitRepresentative;

/**
 * Abstract class for nodes in an OrbitTree in which either an edge or a node is
 * added to an OrbitRepresentative. These two options both need to save an
 * OrbitRepresentative, which is why they have a common superclass.
 * 
 * @author Ine Melckenbeeck
 *
 */
public abstract class AddNode extends TreeNode {
	protected OrbitRepresentative rep;
	protected int repID;

	/**
	 * Creates a new AddNode with the given fields.
	 * 
	 * @param parent
	 *            This AddNode's parent TreeNode.
	 * @param rep
	 *            This AddNode's OrbitRepresentative.
	 * @param tree
	 *            This AddNode's tree.
	 */
	public AddNode(TreeNode parent, OrbitRepresentative rep, OrbitTree tree) {
		super(parent, tree);

		this.rep = rep;
		repID = rep.identify();
	}

	@Override
	public String toString() {
		return rep.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AddNode other = (AddNode) obj;
		if (rep == null) {
			if (other.rep != null)
				return false;
		} else if (!rep.equals(other.rep))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rep == null) ? 0 : rep.hashCode());
		return result;
	}

	/**
	 * Returns the ID number of this AddNode's OrbitRepresentative.
	 * 
	 * @return the ID number of this AddNode's OrbitRepresentative.
	 */
	public int getRepID() {
		return repID;
	}

	/**
	 * Returns this AddNode's OrbitRepresentative.
	 * 
	 * @return this AddNode's OrbitRepresentative.
	 */
	public OrbitRepresentative getOrbitRepresentative() {
		return rep;
	}

}
