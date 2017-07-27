package equations;

import java.util.Comparator;

public class RHSLengthComparator implements Comparator<Equation> {

	@Override
	public int compare(Equation arg0, Equation arg1) {
		return arg0.getRhsConnected().size()-arg1.getRhsConnected().size();
		
	}

}
