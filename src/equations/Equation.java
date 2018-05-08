package equations;

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

import java.util.*;

import orbits.Edge;
import orbits.OrbitIdentification;
import orbits.OrbitRepresentative;


public class Equation {

	private SortedMap<OrbitRepresentative, Integer> lhs;
	private OrbitRepresentative rhsOrbit;
	private List<List<Integer>> rhsConnected;
	private int minus;
	
	/**
	 * Creates a new, empty equation. All factors left-hand and right-hand side
	 * are 0, all graphlets are null;
	 */
	Equation() {
		lhs = new TreeMap<OrbitRepresentative, Integer>();
		rhsOrbit = null;
		rhsConnected = new ArrayList<List<Integer>>();
		minus = 0;
	}
	

	Equation(SortedMap<OrbitRepresentative, Integer> lhs, OrbitRepresentative rhsOrbit,
			List<List<Integer>> rhsConnected, int minus) {
		super();
		this.lhs = lhs;
		this.rhsOrbit = rhsOrbit;
		this.rhsConnected = rhsConnected;
		this.minus = minus;
	}


	/**
	 * Creates a new equation with specified orbits and factors in the left-hand
	 * side and a specific orbit and one set of common nodes in the right-hand
	 * side.
	 * 
	 * @param lhsGraphlets
	 *            List of OrbitGraphlets in the left-hand side of the equation.
	 *            The order of these orbits must correspond to the order of
	 *            their factors in the next parameter!
	 * @param lhsCounts
	 *            List of factors corresponding to the orbits. The order of
	 *            these factors must correspond to the order of the orbits in
	 *            the previous parameter!
	 * @param rhsGraphlet
	 *            The OrbitGraphlet over which the sum of common factors is
	 *            calculated.
	 * @param connected
	 *            One List of Integers, representing the nodes from the previous
	 *            OrbitGraphlet of which the common neighbours must be
	 *            calculated.
	 */
	Equation(List<OrbitRepresentative> lhsGraphlets,
			List<Integer> lhsCounts, OrbitRepresentative rhsGraphlet,
			List<Integer> connected) {
		this();
		assert (lhsGraphlets.size() == lhsCounts.size());
		for (int i = 0; i < lhsGraphlets.size(); i++) {
			lhs.put(lhsGraphlets.get(i), lhsCounts.get(i));
		}
		this.rhsOrbit = rhsGraphlet;
		rhsConnected.add(connected);
		minus += minus(connected, rhsGraphlet);
	}

	/**
	 * Checks whether two equations are able to be merged, i.e. whether all
	 * orbits in the left-hand side and the orbit in the right-hand side of one
	 * graphlet are equal to those of the other.
	 * 
	 * @param e
	 *            The Equation to be checked for compatibility against this
	 *            equation.
	 * @return True if the equations can be merged, false if they cannot.
	 */
	boolean isCompatible(Equation e) {
		return (lhs.keySet().equals(e.lhs.keySet()) && rhsOrbit
				.equals(e.rhsOrbit));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lhs == null) ? 0 : lhs.hashCode());
		result = prime * result + minus;
		result = prime * result + ((rhsConnected == null) ? 0 : rhsConnected.hashCode());
		result = prime * result + ((rhsOrbit == null) ? 0 : rhsOrbit.hashCode());
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
		Equation other = (Equation) obj;
		if (lhs == null) {
			if (other.lhs != null)
				return false;
		} else if (!lhs.equals(other.lhs))
			return false;
		if (minus != other.minus)
			return false;
		if (rhsConnected == null) {
			if (other.rhsConnected != null)
				return false;
		} else if (!rhsConnected.equals(other.rhsConnected))
			return false;
		if (rhsOrbit == null) {
			if (other.rhsOrbit != null)
				return false;
		} else if (!rhsOrbit.equals(other.rhsOrbit))
			return false;
		return true;
	}


	/**
	 * Merges two equations. To do so, both equations' orbits must be equal. The
	 * terms describing the common nodes in the right-hand side are added. If
	 * the graphlets on the left-hand side are exactly equal, their factors are
	 * summed.
	 * 
	 * @param e
	 *            The equation to be merged into this one.
	 */
	void merge(Equation e) {
		assert (isCompatible(e));
		this.rhsConnected.addAll(e.rhsConnected);
		boolean b = false;
		for (OrbitRepresentative g : e.lhs.keySet()) {
			for (OrbitRepresentative g2 : lhs.keySet()) {
				if (g.getEdges().equals(g2.getEdges())) {
					lhs.put(g, lhs.get(g) + e.lhs.get(g));
					b = true;
				}
			}
		}
		if (b)
			minus += e.minus;
	}

	@Override
	public String toString() {

		if (EquationGenerator.latex) {
			String result = "\\[";
			for (OrbitRepresentative g : lhs.keySet()) {
				result += (lhs.get(g) == 1 ? "" : lhs.get(g)) + "o_{"
						+ OrbitIdentification.identifyOrbit(g) + "} + ";

			}
			result = result.substring(0, result.length() - 2);
			result += "= \\sum\\limits_{P_{";
			result += OrbitIdentification.identifyOrbit(rhsOrbit) + "} (x";
			for (int i = 0; i < rhsOrbit.order() - 1; i++) {
				result += "," + (char) ('a' + i);
			}
			result += ")} ";
			for (int i = 0; i < rhsConnected.size(); i++) {
				result += rhsFormula(rhsConnected.get(i), rhsOrbit);
				result += " + ";
			}
			result = result.substring(0, result.length() - 2);
			result += "\\]";
			result += "\n";

			return result;
		} else {

			String result = "";
			for (OrbitRepresentative g : lhs.keySet()) {
				result += (lhs.get(g) == 1 ? "" : lhs.get(g)) + "o_"
						+ OrbitIdentification.identifyOrbit(g) + " + ";

			}
			result = result.substring(0, result.length() - 2);
			result += "= S_P_";
			result += OrbitIdentification.identifyOrbit(rhsOrbit) + " (x";
			for (int i = 0; i < rhsOrbit.order() - 1; i++) {
				result += "," + (char) ('a' + i);
			}
			result += ") ";
			for (int i = 0; i < rhsConnected.size(); i++) {
				result += rhsFormula(rhsConnected.get(i), rhsOrbit);
				result += " + ";
			}
			result = result.substring(0, result.length() - 2);
			result += "\n";
			return result;
		}
	}

	/**
	 * Calculates the negative term that appears in an equation with given
	 * common nodes and right-hand side orbit representative. This term is equal to the
	 * number of nodes within the orbit representative that are common neighbors to the
	 * given nodes.
	 * 
	 * @param connect
	 *            The nodes of which the common neighbors are taken.
	 * @param g
	 *            The OrbitGraphlet in the right-hand side.
	 * @return The term that has to be subtracted from the number of common nodes in equations with the given terms in the right-hand side
	 */
	public static int minus(List<Integer> connect, OrbitRepresentative g) {
		int counter = 0;
		for (int i = 0; i < g.order(); i++) {
			boolean b = true;
			for (int j = 0; j < connect.size() && b; j++) {
				if (connect.get(j) != i)
					b = g.getEdges().contains(new Edge(i, connect.get(j)));
				else
					b = false;
			}
			if (b) {
				counter++;
			}
		}

		return counter;
	}

	/**
	 * Returns the string version of a common neighbor term of the equation.
	 * 
	 * @param connect
	 *            The nodes of which a common neighbor is sought
	 * @param g
	 *            The OrbitGraphlet in which those nodes are situated.
	 * @return A readable string version of the common neighbor term.
	 */
	public static String rhsFormula(List<Integer> connect, OrbitRepresentative g) {
		String result = "";
		int counter = 0;
		result += "c( ";
		for (int i = 0; i < connect.size(); i++) {
			result += connect.get(i) == 0 ? "x"
					: (char) (connect.get(i) + 'a' - 1);
			result += ", ";
		}
		for (int i = 0; i < g.order(); i++) {
			boolean b = true;
			for (int j = 0; j < connect.size() && b; j++) {
				if (connect.get(j) != i)
					b = g.getEdges().contains(new Edge(i, connect.get(j)));
				else
					b = false;
			}
			if (b) {
				counter++;
			}
		}
		result = result.substring(0, result.length() - 2);

		result += " )";
		if (counter != 0) {
			result = "( " + result + " - " + counter + " )";
		}
		return result;
	}

	/**
	 * Returns the left-hand side of the equation in map form.
	 * 
	 * @return A Map containing the left-hand side of the equation: the keys are
	 *         the orbits, their values are their factors.
	 */
	public Map<OrbitRepresentative, Integer> getLhs() {
		return lhs;
	}

	/**
	 * Returns the orbit in the right-hand side of the equation.
	 * 
	 * @return The orbit in the right-hand side of the equation.
	 */
	public OrbitRepresentative getRhsOrbit() {
		return rhsOrbit;
	}

	/**
	 * Returns the total negative term in the right-hand side.
	 * 
	 * @return The total negative term in the right-hand side.
	 */
	public int getMinus() {
		return minus;
	}

	/**
	 * Returns the number of the lowest orbit in this equation's left-hand side.
	 * 
	 * @return The number of the lowest orbit in this equation's left-hand side.
	 */
	public int getLowestOrbit() {
		return OrbitIdentification.identifyOrbit(lhs.firstKey());
	}

	public List<List<Integer>> getRhsConnected() {
		return rhsConnected;
	}
	

}
