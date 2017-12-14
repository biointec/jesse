package equations;

import java.util.ArrayList;
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
		for (int i=0;i<equ.size();i++) {
			finalEquations[i]=(equ.get(i).get(r.nextInt(equ.get(i).size())));
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
