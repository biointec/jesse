package graphletgenerating;
import java.util.*;

public class Permutator {
	// Steinhaus�Johnson�Trotter algorithm with speedup by Shimon Even,
	// iterative
	private int[] values; // Values to permute
	private byte[] signs; // Corresponding signs

	public Permutator(int length) { // Init: 0 -1 -2 ... -(length-1)
		values = new int[length];
		signs = new byte[length];
		for (int i = 0; i < length; i++) {
			values[i] = i;
			signs[i] = -1;
		}
		signs[0] = 0;
	}
	
	public void reset(){
		for (int i = 0; i < signs.length; i++) {
			values[i] = i;
			signs[i] = -1;
		}
		signs[0] = 0;
	}

	public int next() { // Return index; swap element[index] and
						// element[index+1]
		int index = -1; // Compute the largest element which has a nonzero sign
		int value = -1;
		byte sign = 0;
		for (int i = 0; i < signs.length; i++) {
			if (values[i] > value && signs[i] != 0) {
				index = i;
				value = values[i];
				sign = signs[i];
			}
		}
		if (index == -1)
			return -1; // We're finished
		int result = (sign == 1) ? index : index - 1; // If going right keep
														// index to return,
														// otherwise left
														// neighbour

		int neighbour = index + sign; // Swap with its left or right neighbour
										// according to the sign
		values[index] = values[neighbour];
		signs[index] = signs[neighbour];
		values[neighbour] = value;
		signs[neighbour] = sign;
		index = neighbour;

		neighbour = index + signs[index]; // Set sign to zero if we're at the
											// ends or the new neighbour is
											// larger
		if (index == 0 || index == values.length - 1 || values[neighbour] > value)
			signs[index] = 0;

		for (int i = 0; i < values.length; i++) {
			if (i < index && values[i] > value)
				signs[i] = 1; // Set signs of larger elements to the left to +1
			if (index < i && values[i] > value)
				signs[i] = -1; // Set signs of larger elements to the right to
								// -1
		}
		return result;
	}

	public static void main(String[] args) {
		List<Integer> l = new ArrayList<Integer>();
		for (int i = 1; i <= 5; i++) {
			l.add(i);
		}
		Permutator p = new Permutator(5);
		int i = p.next();
		while (i >= 0) {
			int reserve = l.get(i);
			l.set(i, l.get(i + 1));
			l.set(i + 1, reserve);
			System.out.println(l);
			i=p.next();
		}
	}
}
