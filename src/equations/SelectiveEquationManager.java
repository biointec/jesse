package equations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import orbits.OrbitIdentification;
import orbits.OrbitRepresentative;

/**
 * EquationManager subclass that selects equations according to certain
 * selection criteria.
 * 
 * @author imelcken
 * @see EquationManager
 *
 */
public class SelectiveEquationManager extends EquationManager {

	private List<Comparator<Equation>> criteria;
	private boolean[] direction;
	private Random r;

	/**
	 * Constructor for a SelectiveEquationManager with a single selection criterion.
	 * For each orbit, the equation which is either smallest or largest, according
	 * to the given Comparator, is used. If multiple equations are largest or smallest, a random equation is chosen from these.
	 * 
	 * @param order
	 *            The order of graphlets which need to be counted using the
	 *            equations.
	 * @param criterion
	 *            A Comparator for equations according to which either the smallest
	 *            or largest equations are chosen.
	 * @param direction
	 *            If true, the smallest equations, according to the used comparator,
	 *            are used. If false, the largest equations are used.
	 */
	public SelectiveEquationManager(int order, Comparator<Equation> criterion, boolean direction) {
		super(order);
		this.criteria = new ArrayList<Comparator<Equation>>();
		criteria.add(criterion);
		this.direction = new boolean[1];
		this.direction[0] = direction;
		r = new Random();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor for a SelectiveEquationManager with multiple selection criteria.
	 * For each orbit, the equation which is either smallest or largest, according
	 * to the given Comparators, is used. The Comparators are applied in the order they are given, that is, equations will only be compared by a comparator if these equations are equal according to all previous comparators.
	 * If multiple equations fit the criteria best, a random equation is chosen.
	 * 
	 * @param order
	 *            The order of graphlets which need to be counted using the
	 *            equations.
	 * @param criteria
	 *            A list of comparators according to which either the smallest or
	 *            largest equations are chosen.
	 * @param direction
	 *            The directions of the comparators. This array should contain
	 *            exactly as many elements as the criteria parameter.
	 */
	public SelectiveEquationManager(int order, List<Comparator<Equation>> criteria, boolean[] direction) {
		super(order);
		this.criteria = criteria;
		this.direction = direction;
		r = new Random();
	}

	@Override
	public void finalise() {
		finalEquations = new Equation[OrbitIdentification.getNOrbitsForOrder(order) - 1];
		for (int j = 0; j < equ.size(); j++) {
			List<Equation> currentset = new ArrayList<>(equ.get(j));
			currentset.add(equ.get(j).get(0));
			boolean b = false;
//			for(int i=1;i<equ.get(j).size();i++) {
//				int comp = criteria.get(0).compare(equ.get(j).get(i),currentset.get(0));
//				int k=1;
//				while(comp == 0&&k<criteria.size()) {
//					comp = criteria.get(k).compare(equ.get(j).get(i), currentset.get(0));
//				}
//				if(comp ==0) {
//					currentset.add(equ.get(j).get(i));
//				}else if (comp<0==direction[k]) {
//					currentset = new ArrayList<>();
//					currentset.add(equ.get(j).get(i));
//				}
//			}
			
			
			
			List<Equation> nextset = new ArrayList<>();
			int i = 0;
			while (currentset.size() > 1 && i < criteria.size()) {
				Comparator<Equation> criterion = criteria.get(i);
				for (Equation e : currentset) {
					if (nextset.isEmpty() || (/* direction[i] == */(criterion.compare(e, nextset.get(0)) == 0))) {
						nextset.add(e);
					} else if (direction[i] == (criterion.compare(e, nextset.get(0)) < 0)) {
						nextset = new ArrayList<>();
						nextset.add(e);
					}
				}
				i++;
				currentset = nextset;
				nextset = new ArrayList<>();
			}
			finalEquations[j] = (currentset.get(r.nextInt(currentset.size())));

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

	public static void main(String[] args) {

		OrbitIdentification.readGraphlets(null, 4);
		EquationManager em1 = new SelectiveEquationManager(4, new RHSLengthComparator(), false);
		em1.addAll(EquationGenerator.generateEquations(4));
		em1.finalise();
	}

}
