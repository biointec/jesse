package graphletgenerating;
import java.util.*;

public class Graph {
	private boolean[][] matrix = new boolean[Program.order][Program.order];
	private Map<Integer, SortedSet<Integer>> orbits = new HashMap<Integer, SortedSet<Integer>>();

	/**
	 * Creates a graph from its string representation.
	 * 
	 * @param rep
	 *            An array of booleans containing the graph's string
	 *            representation.
	 */
	public Graph(boolean[] rep) {
		for (int i = 0; i < matrix.length; i++)
			matrix[i][i] = false;

		int index = 0;
		for (int i = 1; i < matrix.length; i++) {
			for (int j = 0; j < i; j++) {
				matrix[i][j] = rep[index];
				matrix[j][i] = rep[index];
				index++;
			}
		}
	}

	/**
	 * Checks whether this graph qualifies as a graphlet, i.e. whether it is a
	 * connected graph.
	 * 
	 * @return true if the graph is connected, false if it is not.
	 */
	public boolean isGraphlet() {
		Set<Integer> done = new HashSet<Integer>();
		SortedSet<Integer> todo = new TreeSet<Integer>();
		todo.add(0);
		while (!todo.isEmpty()) {
			int i = todo.first();
			todo.remove(i);
			done.add(i);
			for (int j = 0; j < matrix.length; j++) {
				if (i == j)
					continue;
				if (matrix[i][j]) {
					if (!done.contains(j))
						todo.add(j);
				}
			}
		}
		return done.size() == matrix.length;
	}

	/**
	 * Permutes the nodes of this graphlet in order to calculate its orbits.
	 * 
	 * @param reps
	 * @return A sorted set of Strings containing the orbits of this graphlet,
	 *         sorted by node with lowest number.
	 */
	public SortedSet<String> permute(Set<String> reps) {
		for (int i = 0; i < matrix.length; i++) {
			TreeSet<Integer> a = new TreeSet<Integer>();
			a.add(i);
			orbits.put(i, a);
			// orbits.put(i, new TreeSet<Integer>());
			// orbits.get(i).add(i);
		}

		int[] permutatie = new int[matrix.length];
		boolean[][] kopie = new boolean[matrix.length][matrix.length];
		for (int i = 0; i < matrix.length; i++) {
			permutatie[i] = i;
			for (int j = 0; j < matrix.length; j++) {
				kopie[i][j] = matrix[i][j];
			}
		}

		Permutator p = new Permutator(matrix.length);
		do {
			if (reps.contains(toString()))
				return null;

			boolean gelijk = true;
			outer: for (int i = 0; i < matrix.length - 1; i++) {
				for (int j = i + 1; j < matrix.length; j++) {
					if (matrix[i][j] != kopie[i][j]) {
						gelijk = false;
						break outer;
					}
				}
			}
			if (gelijk) {
				for (int i = 0; i < matrix.length; i++) {
					orbits.get(i).add(permutatie[i]);
				}
			}

			int i = p.next();
			if (i == -1)
				break;
			for (int j = 0; j < matrix.length; j++) {
				boolean tmp = matrix[i][j];
				matrix[i][j] = matrix[i + 1][j];
				matrix[i + 1][j] = tmp;
			}
			for (int j = 0; j < matrix.length; j++) {
				boolean tmp = matrix[j][i];
				matrix[j][i] = matrix[j][i + 1];
				matrix[j][i + 1] = tmp;
			}
			int j = permutatie[i];
			permutatie[i] = permutatie[i + 1];
			permutatie[i + 1] = j;
		} while (true);

		matrix = kopie;

		SortedSet<String> ret = new TreeSet<String>();
		for (SortedSet<Integer> orbit : orbits.values()) {
			ret.add(orbit.toString());
		}

		return ret;
	}

	/**
	 * Returns the string representation of this graphlet.
	 */
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		for (int i = 1; i < matrix.length; i++) {
			for (int j = 0; j < i; j++) {
				ret.append(matrix[i][j] ? 1 : 0);
			}
		}
		return ret.toString();
	}

	/**
	 * Converts the graphlet to postscript code, so it can be visualised.
	 * 
	 * @return The graphlet as postscript code.
	 */
	public String toPS() {
		int r = 100;
		int size = 20;
		int width = 595;
		int height = 841;

		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < matrix.length; i++) {
			builder.append("newpath\n");
			builder.append((width / 2 + (int) Math.round(r * Math.cos(i * 2 * Math.PI / matrix.length))) + " "
					+ (height / 2 + (int) Math.round(r * Math.sin(i * 2 * Math.PI / matrix.length))) + " " + 5 + " " + 0
					+ " " + 360 + " arc\n");
			builder.append("fill\n");
			builder.append("stroke\n");

			builder.append((width / 2 + (int) Math.round((r + size) * Math.cos(i * 2 * Math.PI / matrix.length))) + " "
					+ (height / 2 + (int) Math.round((r + size) * Math.sin(i * 2 * Math.PI / matrix.length)))
					+ " moveto\n");
			builder.append("(" + i + ") show\n");
		}
		for (int i = 0; i < matrix.length - 1; i++) {
			for (int j = i + 1; j < matrix.length; j++) {
				if (matrix[i][j]) {
					builder.append("newpath\n");
					builder.append((width / 2 + (int) Math.round(r * Math.cos(i * 2 * Math.PI / matrix.length))) + " "
							+ (height / 2 + (int) Math.round(r * Math.sin(i * 2 * Math.PI / matrix.length)))
							+ " moveto\n");
					builder.append((width / 2 + (int) Math.round(r * Math.cos(j * 2 * Math.PI / matrix.length))) + " "
							+ (height / 2 + (int) Math.round(r * Math.sin(j * 2 * Math.PI / matrix.length)))
							+ " lineto\n");
					builder.append("stroke\n");
				}
			}
		}

		SortedSet<String> orbitSpec = new TreeSet<String>();
		for (SortedSet<Integer> orbit : orbits.values()) {
			orbitSpec.add(orbit.toString());
		}
		builder.append(10 + " " + 10 + " moveto\n");
		builder.append("(" + orbitSpec.toString() + ") show\n");
		builder.append("showpage\n");

		return builder.toString();
	}

	/**
	 * Converts this graph to a form which can be read by the
	 * OrbitIdentification class of the equation generator.
	 * 
	 * @return This graph, readable by OrbitIdentification.
	 */
	public String toGraphlet() {
		StringBuilder sb = new StringBuilder();
		Set<Integer> usedNodes = new HashSet<Integer>();
		for (int k = 0; k < orbits.size(); k++) {
			if (!usedNodes.contains(k)) {
				int wissel = orbits.get(k).first();
				usedNodes.addAll(orbits.get(k));
				for (int i = 0; i < matrix.length; i++) {
					for (int j = i + 1; j < matrix[i].length; j++) {
						int iwissel, jwissel;
						if (i == 0)
							iwissel = wissel;
						else if (i == wissel)
							iwissel = 0;
						else
							iwissel = i;
						if (j == 0)
							jwissel = wissel;
						else if (j == wissel)
							jwissel = 0;
						else
							jwissel = j;
						if (matrix[i][j])
							sb.append(iwissel + " " + jwissel + ",");
					}
				}
				sb.append("\n");

			}
		}
		return sb.toString();
	}
}
