
package equations;

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
