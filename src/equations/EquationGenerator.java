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

import orbits.OrbitRepresentative;


public class EquationGenerator {

	public static boolean latex = false;

	/**
	 * Generates all orbit representatives in graphlets of the given order.
	 * 
	 * @param order
	 *            The order of which the orbit representatives must be
	 *            calculated.
	 * @return All orbit representatives of the given order.
	 */
	public static Set<OrbitRepresentative> generateOrbits(int order) {
		if (order < 2)
			return new HashSet<OrbitRepresentative>();
		Set<OrbitRepresentative> orbits = new HashSet<OrbitRepresentative>();
		orbits.add(new OrbitRepresentative());
		for (int i = 1; i < order; i++) {
			Set<OrbitRepresentative> newOrbits = new HashSet<OrbitRepresentative>();
			for (OrbitRepresentative or : orbits) {
				newOrbits.addAll(or.generateNext(new LinkedList<Integer>()));
			}
			orbits = newOrbits;
		}
		return orbits;
	}

	
	public static /*EquationManager */ List<Equation >generateEquations(int order, Collection<OrbitRepresentative> reps) {
//		EquationManager result = new EquationManager(order);
		List <Equation> result = new ArrayList<>();
		for (OrbitRepresentative g : reps) {
			assert(g.order()==order-1);
			for (List<Integer> connections : commons(g.order())) {
				Set<OrbitRepresentative> og = g.generateNext(connections);
				List<Integer> lhs = new ArrayList<Integer>();
				List<OrbitRepresentative> lhsGraphlets = new ArrayList<OrbitRepresentative>();
				for (OrbitRepresentative o : og) {
					lhs.add(o.orbitSize(o.order() - 1));
					lhsGraphlets.add(o);
				}
				Equation e = new Equation(lhsGraphlets, lhs, g,
						connections);
				result.add(e);
			}
		}
//		result.sortEquations();
//		result.save("equations.txt");
//		System.out.println(result);
		return result;
	}
	
//	private void save(String name)
	
	/**
	 * Generates all equations for counting graphlets of the given order.
	 * 
	 * @param order
	 *            The order of the graphlets that can be counted with the
	 *            resulting equations.
	 * @return An EquationManager containing all equations.
	 */
	public static List<Equation> generateEquations(int order){
		return generateEquations(order,generateOrbits(order-1));
	}

	/**
	 * Generates all possible combinations from a collection of a certain size
	 * of any number 0<n<=size elements
	 * 
	 * @param size
	 *            The size of the collection of elements.
	 * @return All possible combinations.
	 */
	private static List<List<Integer>> commons(int size) {
		List<List<Integer>> result = new ArrayList<List<Integer>>();
		for (int i = 1; i < Math.pow(2, size) - 1; i++) {
			List<Integer> common = new ArrayList<Integer>();
			int icopy = i;
			for (int j = 0; j < size; j++) {
				int a = icopy % 2;
				if (a == 1)
					common.add(j);
				icopy /= 2;
			}
			result.add(common);
		}
		return result;
	}

}
