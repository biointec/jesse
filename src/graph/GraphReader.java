package graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class GraphReader {

	private static Random r = new Random();

	public static DanglingGraph readGraph(String filename) {
		File file = new File(filename);
		DanglingGraph result = new DanglingGraph();
		try {
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String s = scanner.nextLine();
				if (!s.startsWith("#")) {
					String[] namen = s.split("\\t");
					if (namen.length == 2) {
						result.addEdge(namen[0], namen[1]);
					}
				}
			}
			scanner.close();

			// System.out.println(nodeNames);
		} catch (FileNotFoundException e) {
			System.out.println("Ongeldige bestandsnaam");
		}
		result.finalise();
		return result;

	}

	public static void main(String[] args) {
		DanglingGraph dg = geometric(100,1, 0.1);
		dg.printMatrix();
		dg.print();
	}
	
	public static DanglingGraph ErdosRenyi(int nNodes, int nEdges) {
		DanglingGraph result = new DanglingGraph();
		for(int i=0;i<nNodes;i++){
			result.addNode(""+i);
		}
		
		int maxEdges = (nNodes * (nNodes - 1)) / 2;
		List<Integer> l = new ArrayList<>(maxEdges);
		for (int i = 0; i < maxEdges; i++) {
			l.add(i);
		}
		for (int i = 0; i < nEdges; i++) {
			int n = r.nextInt(l.size());
			int f = l.remove(n);
			int x = (int) Math.floor(.5 + Math.sqrt(1. + 8 * f) / 2.);
			int y = f - (x * (x - 1)) / 2;
			result.addEdge(x, y);
		}
		result.finalise();
		return result;
	}
	
	public static DanglingGraph barabasiAlbert(int nNodes, int edgesPerNode){
		DanglingGraph result = new DanglingGraph();
		List<Integer>degrees = new ArrayList<>();
		result.addNode("0");
		degrees.add(edgesPerNode);
		for(int i=1;i<edgesPerNode+1;i++){
			result.addNode(""+i);
			result.addEdge(i, 0);
			degrees.add(1);
		}
		for(int i=edgesPerNode+1;i<nNodes;i++){
			result.addNode(""+i);
			degrees.add(edgesPerNode);
			int totaldegrees =(i-edgesPerNode) * 2 * edgesPerNode;
			Set<Integer> used = new HashSet<>();
			outer: for(int j=0;j<edgesPerNode;j++){
				int gok = r.nextInt(totaldegrees);
//				System.out.println(gok+" "+totaldegrees+" "+ degrees);
				for(int k=0;k<i;k++){
					if(used.contains(k)){
						continue;
					}
					if(gok<degrees.get(k)){
						totaldegrees -= degrees.get(k);
						degrees.set(k, degrees.get(k)+1);
						used.add(k);
						result.addEdge(i, k);
						continue outer;
					}else{
						gok-=degrees.get(k);
					}
				}
//				System.out.println("fout");
			}
			
		}
		result.finalise();
		return result;
		
	}
	
	public static DanglingGraph geometric(int nNodes, int nDimensions, double radius){
		List<List<Double>> coordinates = new ArrayList<>();
		DanglingGraph result = new DanglingGraph();
		double radiusSquared = radius*radius;
		for(int i=0;i<nNodes;i++){
			result.addNode(""+i);
			List<Double>x = new ArrayList<Double>();
			for(int j = 0;j<nDimensions;j++){
				x.add(r.nextDouble());
			}
			coordinates.add(x);
			outer:for(int j=0;j<i;j++){
				double distance = 0.;
				for(int k = 0;k<nDimensions;k++){
					double coor = coordinates.get(j).get(k)- x.get(k);
					distance += coor*coor;
					if(distance>radiusSquared){
						continue outer;
					}
				}
				result.addEdge(i, j);
			}
		}
		result.finalise();
		return result;
	}
	
//	public static DanglingGraph hierarchical(int cluster, int generations, List<Integer> outside){
//		if(generations == 0){
//			DanglingGraph result = new DanglingGraph();
//			result.add()
//		}
//	}
//	
//	private static 
	
}