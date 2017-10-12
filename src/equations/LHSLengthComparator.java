package equations;

import java.util.Comparator;

public class LHSLengthComparator implements Comparator<Equation> {

	@Override
	public int compare(Equation o1, Equation o2) {
		return o1.getLhs().size()-o2.getLhs().size();
	}

}
