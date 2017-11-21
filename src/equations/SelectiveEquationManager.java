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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import orbits.OrbitIdentification;
import orbits.OrbitRepresentative;

public class SelectiveEquationManager extends EquationManager {

	private List<Comparator<Equation>> criteria;
	private boolean direction;

	public SelectiveEquationManager(int order, Comparator<Equation> criterion, boolean direction) {
		super(order);
		this.criteria = new ArrayList<Comparator<Equation>>();
		criteria.add(criterion);
		this.direction = direction;
		// TODO Auto-generated constructor stub
	}
	
	public SelectiveEquationManager(int order, List<Comparator<Equation>>criteria, boolean direction) {
		super(order);
		this.criteria = criteria;
		this.direction = direction;
	}

	@Override
	public void finalise() {
		finalEquations = new Equation[OrbitIdentification.getNOrbitsForOrder(order)-1];
		// System.out.println("ping");
		for (int j = 0;j<equ.size();j++) {
			// Equation e =(es.get(0));
			// for(Equation ee:es){
			//// if(ee.getLowestOrbit()==42)
			//// System.out.println(ee);
			// if(direction ==( criterion.compare(e, ee)>0)){
			// e = ee;
			// }
			// }
			// finalEquations.add(e);
			List<Equation> currentset = new ArrayList<>(equ.get(j));
			List<Equation> nextset = new ArrayList<>();
			int i = 0;
			while (currentset.size() > 1 && i < criteria.size()) {
				Comparator<Equation> criterion = criteria.get(i);
				for (Equation e : currentset) {
					if (nextset.isEmpty() || direction == (criterion.compare(e, nextset.get(0)) == 0)) {
						nextset.add(e);
					} else if (direction == (criterion.compare(e, nextset.get(0)) < 0)) {
						nextset = new ArrayList<>();
						nextset.add(e);
					}
				}
				i++;
				currentset = nextset;
				nextset = new ArrayList<>();
			}
			finalEquations[j]=(currentset.get(0));
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
//		System.out.println(finalEquations);
	}
	
	
	

}
