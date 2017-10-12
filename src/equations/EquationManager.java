package equations;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import orbits.OrbitIdentification;
import orbits.OrbitRepresentative;

public class EquationManager {

	// private Equation[] equ;
	protected List<List<Equation>> equ;
	protected List<Equation> finalEquations;
	protected SortedSet<OrbitRepresentative> rhsOrbits = new TreeSet<OrbitRepresentative>();
	protected SortedMap<OrbitRepresentative, List<Equation>> equationsByRhs;
	protected int order;

	/**
	 * Creates a new equation manager, which holds equations to count orbits of the
	 * given order.
	 * 
	 * @param order
	 *            The order of the orbits to be counted with the equations in this
	 *            equation manager.
	 */
	public EquationManager(int order/* , List<Equation> eqs */) {
		// equ = new Equation[OrbitIdentification.getNOrbitsForOrder(order) -
		// 1];
		equ = new ArrayList<List<Equation>>(OrbitIdentification.getNOrbitsForOrder(order) - 1);
		for (int i = 0; i < OrbitIdentification.getNOrbitsForOrder(order) - 1; i++) {
			equ.add(new ArrayList<Equation>());
		}
		this.order = order;
		/*
		 * for(Equation e:eqs){ addEquation(e); } finalise();
		 */
		// equationsByRhs = new TreeMap<OrbitRepresentative, List<Equation>>();
	}

	public void addAll(List<Equation> e) {
		for (Equation eq : e) {
			addEquation(eq);
		}
	}

	/**
	 * Tries to add an equation to the manager. If there is an equation with
	 * identical graphlets in the left-hand side, the new equation will be merged
	 * with it.
	 * 
	 * 
	 * @param e
	 */
	protected void addEquation(Equation e) {
		int i = e.getLowestOrbit() - OrbitIdentification.getNOrbitsTotal(order - 1);
		// if (equ[i] == null) {
		// equ[i] = e;
		rhsOrbits.add(e.getRhsOrbit());
		// } else if (equ[i].isCompatible(e)){
		// equ[i].merge(e);
		//
		// rhsOrbits.add(e.getRhsOrbit());
		// }
		boolean flag = false;
		for (Equation ee : equ.get(i)) {
			if (ee.isCompatible(e)) {
				ee.merge(e);
				rhsOrbits.add(e.getRhsOrbit());
				flag = true;
			}
		}
		if (!flag) {
			equ.get(i).add(e);
			rhsOrbits.add(e.getRhsOrbit());
		}

		//// else if(e.getLhs().size()>equ[i].getLhs().size()){
		//// else if (e.getRhsOrbit().compareTo(equ[i].getRhsOrbit())>0){
		//// else if
		//// (e.getRhsConnected().size()>equ[i].getRhsConnected().size()){
		// else
		//// if(e.getRhsConnected().get(0).size()<equ[i].getRhsConnected().get(0).size()){
		// equ[i] = e;
		// rhsOrbits.add(e.getRhsOrbit());
		// }
		// else
		//// if(e.getRhsConnected().get(0).size()==equ[i].getRhsConnected().get(0).size()&&
		// e.getRhsOrbit().compareTo(equ[i].getRhsOrbit())<0){
		//// e.getRhsConnected().size()<equ[i].getRhsConnected().size()){
		//// e.getLhs().size()>equ[i].getLhs().size()){
		//
		// equ[i] = e;
		// rhsOrbits.add(e.getRhsOrbit());
		// }

	}

	/**
	 * Returns an array containing the equations in this equation manager.
	 * 
	 * @return An array containing this equation manager's equations.
	 */
	public List<Equation> getEqu() {
		// public List<List<Equation>> getEqu(){
		// Equation [] result = new Equation[equ.size()];
		// for(int i=0;i<equ.size();i++){
		// result[i]= equ.get(i).get(0);
		// }
		// return result;
		if (finalEquations == null) {
			// finalEquations = new ArrayList<Equation>();
			// for (List<Equation> e : equ) {
			// finalEquations.add(e.get(0));
			// }
			finalise();
		}
		return finalEquations;
	}

	public void finalise() {
		// System.out.println("ping1");
		finalEquations = new ArrayList<Equation>();
		for (List<Equation> e : equ) {
			finalEquations.add(e.get(0));
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

	/**
	 * Returns a set containing all orbits over which a sum is made in the
	 * right-hand side of any equation.
	 * 
	 * @return A set containing all orbits over which a sum is made in the
	 *         right-hand side of any equation.
	 */
	public Set<OrbitRepresentative> getRhsOrbits() {
		return rhsOrbits;
	}

	public String toString() {
		String result = "";
		for (OrbitRepresentative og : rhsOrbits) {
			result += og + "\n";
		}
		result += "\n";
		for (List<Equation> a : equ) {
			for (Equation e : a)
				result += e;
		}
		return result;
	}

	void sortEquations() {
		// for(int i=0;i<equ.length;i++){
		// for(List<Equation> a:finalEquations){
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
	// }

	public SortedMap<OrbitRepresentative, List<Equation>> getEquationsByRhs() {
		return equationsByRhs;
	}

	public List<Equation> getEquationsByRHS(OrbitRepresentative or) {
		getEqu();
		if (equationsByRhs == null) {
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

		return equationsByRhs.get(or);
	}

	public void save(String filename) {
		try {
			PrintWriter ps = new PrintWriter(new BufferedWriter(new FileWriter(filename)));

			// for (Equation e:equ) {
			for (List<Equation> a : equ) {
				for (Equation e : a)
					ps.println(e);

			}
			ps.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveStats(String filename) {
		try {
			PrintWriter ps = new PrintWriter(new BufferedWriter(new FileWriter(filename)));

			// for (Equation e:equ) {
			for (List<Equation> a : equ) {
				for (Equation e : a) {
					ps.print(e.getLowestOrbit());
					ps.print(OrbitIdentification.getOrbit(e.getLowestOrbit()).symmetry());

					ps.print(e.getLhs().size());
				}

			}
			ps.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// public static void main(String[] args) {
	// OrbitIdentification.readGraphlets("Przulj.txt", 6
	// EquationGenerator.generateEquations(6);
	// }

	public void toOrcaCode() {
		for (int i = 4; i < 15; i++) {
			System.out.println(i);
			OrbitRepresentative or = OrbitIdentification.getOrbit(i);

			List<Equation> l = equationsByRhs.get(or);
			for (Equation e : l) {
				System.out.print("f_" + e.getLowestOrbit() + " += ");
				boolean done = false;
				for (List<Integer> list : e.getRhsConnected()) {
					if (done) {
						System.out.print(" + ");
					} else {
						done = true;
					}
					if (list.size() == 1) {
						int a = list.get(0);
						System.out.print("deg[" + (a == 0 ? 'x' : (char) ('a' + a - 1)) + "]");
					} else if (list.size() == 3) {
						char a = list.get(0) == 0 ? 'x' : (char) ('a' + list.get(0) - 1);
						char b = list.get(1) == 0 ? 'x' : (char) ('a' + list.get(1) - 1);
						char c = list.get(2) == 0 ? 'x' : (char) ('a' + list.get(2) - 1);
						System.out.print("common3_get(TRIPLE(" + a + "," + b + "," + c + "))");
					} else if (list.size() == 2) {
						char a = list.get(0) == 0 ? 'x' : (char) ('a' + list.get(0) - 1);
						char b = list.get(1) == 0 ? 'x' : (char) ('a' + list.get(1) - 1);
						System.out.print("common2_get(PAIR(" + a + "," + b + "))");
					}

				}
				if (e.getMinus() != 0) {
					System.out.print(" - " + e.getMinus());
				}
				System.out.println(";");
			}
			System.out.println();
		}
		for (int i=finalEquations.size()-1;i>=0;i--) {
			Equation e = finalEquations.get(i);
			System.out.print("orbit[x]["+e.getLowestOrbit()+"] = (f_"+e.getLowestOrbit());
			int division = 1;
			for(OrbitRepresentative or:e.getLhs().keySet()) {
				if(or.identify()!=e.getLowestOrbit()) {
					System.out.print("- " + e.getLhs().get(or) + " * orbit[x]["+or.identify()+"]");
				}else {
					division=e.getLhs().get(or);
				}
			}
			System.out.println(")/"+division+";");
		}
	}

}
