package codegenerating;

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
