package equations;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import orbits.OrbitIdentification;
import orbits.OrbitRepresentative;

public class EquationManager {

//	private Equation[] equ;
	private List<List<Equation>> equ;
	private SortedSet<OrbitRepresentative> rhsOrbits = new TreeSet<OrbitRepresentative>();
	private SortedMap<OrbitRepresentative, List<Equation>> equationsByRhs;
	private int size;

	/**
	 * Creates a new equation manager, which holds equations to count orbits of
	 * the given order.
	 * 
	 * @param order
	 *            The order of the orbits to be counted with the equations in
	 *            this equation manager.
	 */
	public EquationManager(int order) {
//		equ = new Equation[OrbitIdentification.getNOrbitsForOrder(order) - 1];
		equ = new ArrayList<List<Equation>>(OrbitIdentification.getNOrbitsForOrder(order) - 1);
		for(int i=0;i<OrbitIdentification.getNOrbitsForOrder(order) - 1;i++){
			equ.add(new ArrayList<Equation>());
		}
		this.size = order;
		equationsByRhs = new TreeMap<OrbitRepresentative, List<Equation>>();
	}

	/**
	 * Tries to add an equation to the manager. If there is no equation present
	 * with the same lowest-number orbit in the left-hand side, the equation
	 * will be added to the manager. If there is an equation with identical
	 * graphlets in the left-hand side, the new equation will be merged with it.
	 * If neither applies, the equation will not be added.
	 * 
	 * @param e
	 */
	void addEquation(Equation e) {
		int i = e.getLowestOrbit()
				- OrbitIdentification.getNOrbitsTotal(size - 1);
//		if (equ[i] == null) {
//			equ[i] = e;
			rhsOrbits.add(e.getRhsOrbit());
//		} else if (equ[i].isCompatible(e)){
//			equ[i].merge(e);
//		
//			rhsOrbits.add(e.getRhsOrbit());
//		}
			boolean flag = false;
		for(Equation ee:equ.get(i)){
			if(ee.isCompatible(e)){
				ee.merge(e);
				rhsOrbits.add(e.getRhsOrbit());
				flag = true;
			}
		}
		if(!flag){
			equ.get(i).add(e);
			rhsOrbits.add(e.getRhsOrbit());
		}
		
////		else if(e.getLhs().size()>equ[i].getLhs().size()){
////		else if (e.getRhsOrbit().compareTo(equ[i].getRhsOrbit())>0){
////		else if (e.getRhsConnected().size()>equ[i].getRhsConnected().size()){
//		else if(e.getRhsConnected().get(0).size()<equ[i].getRhsConnected().get(0).size()){
//			equ[i] = e;
//			rhsOrbits.add(e.getRhsOrbit());
//		}
//		else if(e.getRhsConnected().get(0).size()==equ[i].getRhsConnected().get(0).size()&&
//				e.getRhsOrbit().compareTo(equ[i].getRhsOrbit())<0){
////				e.getRhsConnected().size()<equ[i].getRhsConnected().size()){
////				e.getLhs().size()>equ[i].getLhs().size()){
//				
//			equ[i] = e;
//			rhsOrbits.add(e.getRhsOrbit());
//		}

	}

	/**
	 * Returns an array containing the equations in this equation manager.
	 * 
	 * @return An array containing this equation manager's equations.
	 */
//	public Equation[] getEqu() {
	public List<List<Equation>> getEqu(){
		return equ;
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
			for(Equation e:a)
			result += e;
		}
		return result;
	}
	
	void sortEquations(){
//		for(int i=0;i<equ.length;i++){
		for(List<Equation> a:equ){
			for(Equation e: a){
//			Equation e = equ[i];
			OrbitRepresentative n = e.getRhsOrbit();
			if(!equationsByRhs.containsKey(n)){
				equationsByRhs.put(n,new ArrayList<Equation>());
				
			}
			equationsByRhs.get(n).add(e);
			}
		}
	}

	public SortedMap<OrbitRepresentative, List<Equation>> getEquationsByRhs() {
		return equationsByRhs;
	}
	
	public List<Equation> getEquationsByRHS(OrbitRepresentative or){
		return equationsByRhs.get(or);
	}
	
	public void save(String filename){
		try {
			PrintWriter ps = new PrintWriter(new BufferedWriter(new FileWriter(filename)));

//			for (Equation e:equ) {
			for(List<Equation> a: equ){
				for(Equation e:a)
				ps.println(e);
				
			}
			ps.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveStats(String filename){
		try {
			PrintWriter ps = new PrintWriter(new BufferedWriter(new FileWriter(filename)));

//			for (Equation e:equ) {
			for(List<Equation> a: equ){
				for(Equation e:a){
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
	
	public static void main(String[]args){
		OrbitIdentification.readGraphlets("Przulj.txt", 5);
		EquationGenerator.generateEquations(5).save("allequations.txt");
	}
}
