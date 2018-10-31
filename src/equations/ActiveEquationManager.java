
package equations;

/*
 * #%L
 * Jesse
 * %%
 * Copyright (C) 2017 - 2018 Intec/UGent - Ine Melckenbeeck
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
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import orbits.OrbitIdentification;
import orbits.OrbitRepresentative;

public class ActiveEquationManager extends EquationManager {

	private long[] counts;
	// private boolean direction;

	public ActiveEquationManager(int order, long[] counts/* , boolean direction */) {
		super(order);
		this.counts = counts;
		// this.direction = direction;
		// TODO Auto-generated constructor stub
	}

	public ActiveEquationManager(int order, long[][] counts/* , boolean direction */) {
		super(order);
		this.counts = new long[counts[0].length];
		for (int i = 0; i < counts.length; i++) {
			for (int j = 0; j < counts[0].length; j++) {
				this.counts[j] += counts[i][j];
			}
		}
		// this.direction = direction;
	}

	@Override
	public void finalise() {
		finalEquations = new Equation[OrbitIdentification.getNOrbitsForOrder(order) - 1];
		for (int j = 0; j < equ.size(); j++) {
			List<Equation> l = equ.get(j);
			int minimum = 0;
			for (int i = 1; i < l.size(); i++) {
				if (counts[l.get(i).getRhsOrbit().identify()]
						* l.get(i).getRhsConnected().size() < counts[l.get(minimum).getRhsOrbit().identify()]
								* l.get(minimum).getRhsConnected().size()) {
					minimum = i;
				}
			}
			finalEquations[j] = l.get(minimum);
		}
		equationsByRhs = new TreeMap<>();
		for (Equation e : finalEquations) {
			// Equation e = equ[i];
			OrbitRepresentative n = e.getRhsOrbit();
			if (!equationsByRhs.containsKey(n)) {
				equationsByRhs.put(n, new ArrayList<Equation>());

			}
			equationsByRhs.get(n).add(e);
		}
		// System.out.println(finalEquations);
	}

}
