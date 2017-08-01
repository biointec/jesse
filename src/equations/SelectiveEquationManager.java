package equations;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import orbits.OrbitRepresentative;

public class SelectiveEquationManager extends EquationManager{
	
	private Comparator<Equation> criterion;
	private boolean direction;

	public SelectiveEquationManager(int order, Comparator<Equation> criterion, boolean direction) {
		super(order);
		this.criterion=criterion;
		this.direction=direction;
		// TODO Auto-generated constructor stub
	}

	protected void finalise(){
		finalEquations = new ArrayList<Equation>();
//		System.out.println("ping");
		for (List<Equation> es : equ) {
			Equation e =(es.get(0));
			for(Equation ee:es){
//				if(ee.getLowestOrbit()==42)
//				System.out.println(ee);
				if(direction ==( criterion.compare(e, ee)>0)){
					e = ee;
				}
			}
			finalEquations.add(e);
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
	}

}
