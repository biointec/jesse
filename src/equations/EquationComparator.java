package equations;

import java.util.Comparator;


public class EquationComparator implements Comparator<Equation>{

	/**
	 * Compares two equations based on the lowest orbit in their left-hand side.
	 * @return -1 if the first equation's lowest orbit is higher than the second, +1 if it is lower, 0 if they are equal.
	 */
	@Override
	public int compare(Equation arg0, Equation arg1) {
		int n0=arg0.getLowestOrbit();
		int n1=arg1.getLowestOrbit();
		if(n0>n1){
			return -1;
		}else if(n0==n1){
			return 0;
		}else{
			return 1;
		}
	}
}
