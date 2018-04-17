package equations;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.*;

import orbits.OrbitIdentification;
import orbits.OrbitRepresentative;

public class EquationGenerator {

	public static boolean latex = false;

	/**
	 * Generates all orbit representatives in graphlets of the given order.
	 * 
	 * @param order
	 *            The order of which the orbit representatives must be calculated.
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

	public static /* EquationManager */ List<Equation> generateEquations(int order,
			Collection<OrbitRepresentative> reps) {
		// EquationManager result = new EquationManager(order);
		List<Equation> result = new ArrayList<>();
		for (OrbitRepresentative g : reps) {
			assert (g.order() == order - 1);
			for (List<Integer> connections : commons(g.order())) {
				Set<OrbitRepresentative> og = g.generateNext(connections);
				List<Integer> lhs = new ArrayList<Integer>();
				List<OrbitRepresentative> lhsGraphlets = new ArrayList<OrbitRepresentative>();
				for (OrbitRepresentative o : og) {
					lhs.add(o.orbitSize(o.order() - 1));
					lhsGraphlets.add(o);
				}
				Equation e = new Equation(lhsGraphlets, lhs, g, connections);
				result.add(e);
			}
		}
		// result.sortEquations();
		// result.save("equations.txt");
		// System.out.println(result);
		return result;
	}

	// private void save(String name)

	/**
	 * Generates all equations for counting graphlets of the given order.
	 * 
	 * @param order
	 *            The order of the graphlets that can be counted with the resulting
	 *            equations.
	 * @return An EquationManager containing all equations.
	 */
	public static List<Equation> generateEquations(int order) {
		return generateEquations(order, generateOrbits(order - 1));
	}

	/**
	 * Generates all possible combinations from a collection of a certain size of
	 * any number 0<n<=size elements
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

	public static void main(String[] args) throws FileNotFoundException {

		// OrbitIdentification.readGraphlets(null,5);
		// EquationManager em = new EquationManager(5);
		// em.addAll(generateEquations(5));
		// latex=true;
		// System.out.println(em);
		for (int i = 4; i < 8; i++) {
//			PrintWriter pw = new PrintWriter("corr" + i + ".txt");
			 PrintStream pw = System.out;
			System.out.println(i);
			OrbitIdentification.readGraphlets(null, i);
			for (int j = 0; j < 20; j++) {
				EquationManager em1 = new SelectiveEquationManager(i, new RHSTermComparator(), true);
				em1.addAll(generateEquations(i));
				EquationManager em2 = new SelectiveEquationManager(i, new RHSLengthComparator(), false);
				em2.addAll(generateEquations(i));
				EquationManager em3 = new RandomEquationManager(i);
				em3.addAll(generateEquations(i));
				EquationManager em4 = new SelectiveEquationManager(i, new RHSLengthComparator(), true);
				em4.addAll(generateEquations(i));
				EquationManager em5 = new SelectiveEquationManager(i, new RHSTermComparator(), false);
				em5.addAll(generateEquations(i));
				Equation[] a = em1.getEqu();
				Equation[] b = em2.getEqu();
				Equation[] c = em3.getEqu();
				Equation[] d = em4.getEqu();
				Equation[] e = em5.getEqu();
				double averaga=0,averagb=0,averagc=0,averagd=0,average=0;
				for (int k = 0; k < b.length; k++) {
//					System.out.println(a[k]);
//					System.out.println(b[k]);
//					System.out.println(c[k]);
//					System.out.println(d[k]);
//					System.out.println(e[k]);
//					System.out.println();
//					System.out.println();
//					averaga+=a[k].getRhsConnected().get(0).size();
//					averagb+=b[k].getRhsConnected().get(0).size();
//					averagc+=c[k].getRhsConnected().get(0).size();
//					averagd+=d[k].getRhsConnected().get(0).size();
//					average+=e[k].getRhsConnected().get(0).size();
					averaga+=a[k].getRhsConnected().size();
					averagb+=b[k].getRhsConnected().size();
					averagc+=c[k].getRhsConnected().size();
					averagd+=d[k].getRhsConnected().size();
					average+=e[k].getRhsConnected().size();
				}
				pw.println(averaga/b.length+ "\t" + averagb/b.length+ "\t" + averagc/b.length+ "\t" + averagd/b.length+ "\t" + average/b.length);

			}
//			pw.close();
			// double average = 0;
			// double average2 = 0;
			// for(int j=0;j<a.length;j++) {
			// average+=(a[j].getRhsConnected().get(0).size()-b[j].getRhsConnected().get(0).size());
			// average2+=(c[j].getRhsConnected().get(0).size()-d[j].getRhsConnected().get(0).size());
			// }
			// System.out.println(average/a.length);
			// System.out.println(average2/a.length);

		}

	}

	// private static BigInteger nrSets(int order) {
	// List<Equation> l =(generateEquations(order));
	// l.sort(new EquationComparator());
	// int huidig = 0;
	// int teller = 1;
	// BigInteger result = new BigInteger("1");
	// for(Equation e:l) {
	// if(huidig!=e.getLowestOrbit()) {
	// result=result.multiply(new BigInteger(""+teller));
	// teller=1;
	// huidig = e.getLowestOrbit();
	// }else {
	// teller+=1;
	// }
	// }
	// return result;
	//// System.out.println(l);
	// }

}
