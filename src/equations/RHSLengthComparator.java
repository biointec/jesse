package equations;

import java.util.Comparator;

public class RHSLengthComparator implements Comparator<Equation> {

	/**
	 * Compares two equations based on the number of terms in their right-hand size.
	 */
	@Override
	public int compare(Equation arg0, Equation arg1) {
		return arg0.getRhsConnected().size()-arg1.getRhsConnected().size();
		
	}

}
