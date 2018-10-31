package equations;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
import java.util.Random;
import java.util.TreeMap;

import orbits.OrbitIdentification;
import orbits.OrbitRepresentative;

public class RandomEquationManager extends EquationManager {
	public RandomEquationManager(int order) {
		super(order);
	}
	

	public void finalise() {
		// System.out.println("ping1");
		finalEquations =new Equation[OrbitIdentification.getNOrbitsForOrder(order)-1];
		Random r = new Random();
		List<List<Equation>> newEqu = new ArrayList<>();
		for (int i=0;i<equ.size();i++) {
			finalEquations[i]=(equ.get(i).get(r.nextInt(equ.get(i).size())));
			newEqu.add(new ArrayList<>());
			newEqu.get(i).add(finalEquations[i]);
		}
		equ = newEqu;
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
	
	public static void main(String[]args) throws FileNotFoundException {
		OrbitIdentification.readGraphlets(null, 6);
		PrintWriter pw= new PrintWriter("data/equations-2.tex");
		EquationGenerator.latex=true;
		EquationManager em = new RandomEquationManager(5);
		em.addAll(EquationGenerator.generateEquations(5));
		em.finalise();
		pw.println(em);
		pw.close();
	}
}
