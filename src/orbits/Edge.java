package orbits;

import java.util.Arrays;

import equations.EquationGenerator;

public class Edge {
	private int[] vertices;

	/**
	 * Creates a new edge connecting two vertices.
	 * 
	 * @param n1
	 *            The number of the first vertex.
	 * @param n2
	 *            The number of the second vertex.
	 */
	public Edge(int n1, int n2) {
		if (n1 != n2) {
			vertices = new int[2];
			vertices[0] = Math.min(n1, n2);
			vertices[1] = Math.max(n1, n2);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 0;
		result = prime * result + vertices[0];
		result = prime * result + vertices[1];
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Edge other = (Edge) obj;
		if (Arrays.equals(vertices, other.vertices))
			return true;
		return false;
	}

	/**
	 * @return An array containing the vertices this edge connects.
	 */
	public int[] getNodes() {
		return vertices;
	}

	/**
	 * Gives a string form of this edge. Both vertices will be represented as a
	 * character: if a vertex has number 0, it will be called 'x', number 1 will
	 * result in 'a', number 2 in 'b' and so on.
	 */
	@Override
	public String toString() {
		if(EquationGenerator.latex)
		return "\\{"
				+ (vertices[0] == 0 ? 'x' : (char) ('a' + vertices[0] - 1))
				+ "," + (char) ('a' + vertices[1] - 1) + "\\}";
		else return "{"+(vertices[0] == 0 ? 'x' : (char) ('a' + vertices[0] - 1))
				+ "," + (char) ('a' + vertices[1] - 1)+"}";
	}

}
