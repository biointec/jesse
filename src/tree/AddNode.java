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
	AddNode(TreeNode parent, OrbitRepresentative rep) {
		super(parent);

		this.rep = rep;
		repID = rep.identify();
	}
	
	AddNode(OrbitRepresentative rep, OrbitTree tree){
		super(tree);

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
