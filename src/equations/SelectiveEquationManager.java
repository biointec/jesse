package equations;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

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

	public void finalise() {
		finalEquations = new ArrayList<Equation>();
		// System.out.println("ping");
		for (List<Equation> es : equ) {
			// Equation e =(es.get(0));
			// for(Equation ee:es){
			//// if(ee.getLowestOrbit()==42)
			//// System.out.println(ee);
			// if(direction ==( criterion.compare(e, ee)>0)){
			// e = ee;
			// }
			// }
			// finalEquations.add(e);
			List<Equation> currentset = new ArrayList<>(es);
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
			finalEquations.add(currentset.get(0));
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
