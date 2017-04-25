package equations;

import java.util.*;

import orbits.OrbitIdentification;
import orbits.OrbitRepresentative;

public class EquationManager {

	private Equation[] equ;
	private SortedSet<OrbitRepresentative> rhsOrbits = new TreeSet<OrbitRepresentative>();
	private SortedMap<OrbitRepresentative, List<Equation>> equationsByRhs;
	private int size;

	/**
	 * Creates a new equation manager, which holds equations to count orbits of
	 * the given order.
	 * 
	 * @param order
	 *            The order of the orbits to be counted with the equations in
	 *            this equation manager.
	 */
	public EquationManager(int order) {
		equ = new Equation[OrbitIdentification.getNOrbitsForOrder(order) - 1];
		this.size = order;
		equationsByRhs = new TreeMap<OrbitRepresentative, List<Equation>>();
	}

	/**
	 * Tries to add an equation to the manager. If there is no equation present
	 * with the same lowest-number orbit in the left-hand side, the equation
	 * will be added to the manager. If there is an equation with identical
	 * graphlets in the left-hand side, the new equation will be merged with it.
	 * If neither applies, the equation will not be added.
	 * 
	 * @param e
	 */
	void addEquation(Equation e) {
		int i = e.getLowestOrbit()
				- OrbitIdentification.getNOrbitsTotal(size - 1);
		if (equ[i] == null) {
			equ[i] = e;
			rhsOrbits.add(e.getRhsOrbit());
		} else if (equ[i].isCompatible(e)){
			equ[i].merge(e);
		
			rhsOrbits.add(e.getRhsOrbit());
		}

	}

	/**
	 * Returns an array containing the equations in this equation manager.
	 * 
	 * @return An array containing this equation manager's equations.
	 */
	public Equation[] getEqu() {
		return equ;
	}

	/**
	 * Returns a set containing all orbits over which a sum is made in the
	 * right-hand side of any equation.
	 * 
	 * @return A set containing all orbits over which a sum is made in the
	 *         right-hand side of any equation.
	 */
	public Set<OrbitRepresentative> getRhsOrbits() {
		return rhsOrbits;
	}

	public String toString() {
		String result = "";
		for (OrbitRepresentative og : rhsOrbits) {
			result += og + "\n";
		}
		result += "\n";
		for (Equation e : equ) {
			result += e;
		}
		return result;
	}
	
	void sortEquations(){
		for(int i=0;i<equ.length;i++){
			Equation e = equ[i];
			OrbitRepresentative n = e.getRhsOrbit();
			if(!equationsByRhs.containsKey(n)){
				equationsByRhs.put(n,new ArrayList<Equation>());
				
			}
			equationsByRhs.get(n).add(e);
		}
	}

	public SortedMap<OrbitRepresentative, List<Equation>> getEquationsByRhs() {
		return equationsByRhs;
	}
	
	public List<Equation> getEquationsByRHS(OrbitRepresentative or){
		return equationsByRhs.get(or);
	}
	
	
}
