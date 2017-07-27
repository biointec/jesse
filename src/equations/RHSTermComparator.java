package equations;

import java.util.Comparator;

public class RHSTermComparator implements Comparator<Equation> {

	@Override
	public int compare(Equation arg0, Equation arg1) {
		return arg0.getRhsConnected().get(0).size()-arg1.getRhsConnected().get(0).size();
		
	}
}
