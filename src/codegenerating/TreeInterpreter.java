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

import tree.AddEdgeNode;
import tree.AddNodeNode;
import tree.ConditionNode;

/**
 * Interface for classes which interpret an OrbitTree. Normally, this is used to
 * count the number of OrbitRepresentatives in a graph.
 * 
 * @author Ine Melckenbeeck
 *
 */
public interface TreeInterpreter {

	/**
	 * The action which will be performed when an AddNodeNode calls walkTree().
	 * 
	 * @param ann
	 *            The calling AddNodeNode.
	 */
	public void addNodeAction(AddNodeNode ann);

	/**
	 * The action which will be performed when an AddEdgeNode calls walkTree().
	 * 
	 * @param aen
	 *            The calling AddEdgeNode.
	 */
	public void addEdgeAction(AddEdgeNode aen);

	/**
	 * The action which will be performed when a ConditionNode calls walkTree().
	 * 
	 * @param cn
	 *            The calling ConditionNode.
	 */
	public void conditionAction(ConditionNode cn);

	/**
	 * Starts this TreeInterpreter's interpretation of the OrbitTree.
	 * 
	 * @return
	 */
	public long[][] run();
}
