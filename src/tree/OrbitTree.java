package tree;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import codegenerating.TreeInterpreter;
import orbits.Edge;
import orbits.OrbitIdentification;
import orbits.OrbitRepresentative;

/**
 * A tree in which the construction of orbit representatives is shown, starting
 * from a two-node orbit representative.
 * 
 * @author Ine Melckenbeeck
 *
 */
public class OrbitTree {

	private AddNodeNode root;
	private int order;
	private TreeInterpreter interpreter;
	private Set<OrbitRepresentative> leaves;

	/**
	 * Builds a new OrbitTree containing OrbitRepresentatives up to the given
	 * order.
	 * 
	 * @param order
	 *            The maximal order of the OrbitRepresentatives in the tree.
	 */
	public OrbitTree(int order) {
		this.order = order;
		buildTreeBFS();
	}

	private void buildTreeBFS() {
		OrbitRepresentative o = new OrbitRepresentative();
		root = new AddNodeNode(o,this);
		OrbitRepresentative o2 = new OrbitRepresentative(o);
		boolean[] z = { true };
		o2.addNode(z);
//		o2.calculateSymmetry();

		AddNodeNode node = new AddNodeNode(o2, root);
		root.addChild(0, node);
		Deque<AddNodeNode> nodes1 = new LinkedList<AddNodeNode>();
		nodes1.add(node);
		for (int k = 2; k < order; k++) {
			Deque<AddEdgeNode> nextEdges = new LinkedList<AddEdgeNode>();
			Set<OrbitRepresentative> used = new HashSet<>();
//			System.out.println(nodes1);
			for (AddNodeNode node1 : nodes1) {
				/* Add a first new layer of AddEdgeNode. */
				OrbitRepresentative or = node1.getOrbitRepresentative();
				if (used.contains(or)) {
					/* Make sure each branch ends in an AddNodeNode. */
					node1.remove();
					node1.getParent().prune();
				} else {
					used.add(or);
					/*
					 * Only connect to one node of each suborbit, the other
					 * situations will get the same orbit representative anyway.
					 */
					OrbitRepresentative copy = new OrbitRepresentative(or);
					copy.addNode(0);
					AddEdgeNode child = new AddEdgeNode(copy, node1, 1);
					nextEdges.add(child);
					node1.addChild(0, child);
					
					for (int i = 1; i < or.getOrbits().size(); i++) {
						int index = or.getOrbits().get(i).first();
						copy = new OrbitRepresentative(or);
						copy.addNode(index);
						AddEdgeNode parent = new AddEdgeNode(copy,node1,0);
						node1.addChild(index, parent);
						for(int j = 1;j<index;j++){
							AddEdgeNode previousParent = parent;
							parent = new AddEdgeNode(copy,previousParent,j);
							previousParent.addChild(parent, false);
						}
						copy = new OrbitRepresentative(copy);
						if(index<or.order()-1){
						 child = new AddEdgeNode(copy, parent, index+1);}
						else{
							 child = new AddEdgeNode(copy, parent, index);
						}
						nextEdges.add(child);
						parent.addChild(child,false);
//						node1.addChild(index, child);
					}
				}
			}
			nodes1 = new LinkedList<AddNodeNode>();
//			System.out.println(nextEdges);
			while (!nextEdges.isEmpty()) {
				AddEdgeNode node2 = nextEdges.pollFirst();
				int edge = node2.getEdge();
				OrbitRepresentative or = node2.getOrbitRepresentative();
				OrbitRepresentative copy = new OrbitRepresentative(or);
				if (or.getEdges().contains(new Edge(edge, or.order() - 1))) {
//					System.out.println(node2);
					if (edge >= copy.order() - 2) {
						AddNodeNode child = new AddNodeNode(or, node2.getParent());
						nodes1.add(child);
						node2.getParent().replaceChild(node2, child);
//						node2.addChild(child, false);
					} else {
						node2.setEdge(edge + 1);
						nextEdges.addFirst(node2);
					}
				} else if (edge == copy.order() - 2) {
					/* Add the next layer of AddNodeNodes */
					AddNodeNode falsechild = new AddNodeNode(or, node2);
					nodes1.add(falsechild);
					node2.addChild(falsechild, false);
					copy.addEdge(edge, or.order() - 1);
//					copy.calculateSymmetry();
					AddNodeNode truechild = new AddNodeNode(copy, node2);
					nodes1.add(truechild);
					node2.addChild(truechild, true);
				} else {
					/* Add another layer of AddEdgeNodes */
					AddEdgeNode falsechild = new AddEdgeNode(or, node2, edge + 1);
					copy.addEdge(edge, or.order() - 1);
//					copy.calculateSymmetry();
					AddEdgeNode truechild = new AddEdgeNode(copy, node2, edge + 1);
					node2.addChild(falsechild, false);
					nextEdges.addLast(falsechild);
					node2.addChild(truechild, true);
					nextEdges.addLast(truechild);
				}
//				node2.prune();
			}
		}
		leaves = new HashSet<>();
		for (AddNodeNode n : nodes1) {
			OrbitRepresentative or = n.getOrbitRepresentative();
			if (leaves.add(or)) {
				/*
				 * Break the OrbitRepresentative's symmetry and add the needed
				 * ConditionNodes for the restraints.
				 */
				List<Set<Integer>> cosetreps = or.getCosetreps();
				List<ConditionNode> conditionNodes = new ArrayList<ConditionNode>();
				for (int i = 0; i < cosetreps.size(); i++) {
					for (int j : cosetreps.get(i)) {
						ConditionNode cn = new ConditionNode(n, i + 1, j);
//						cn.insert(n);
						conditionNodes.add(cn);
					}
				}
//				ConditionNode.simplify(conditionNodes);
				for(ConditionNode cn: conditionNodes){
					cn.insert(n);
				}

			} else {
				/* Remove duplicate leaves. */
				n.remove();
				n.prune();
			}

		}
		/* Calculate the nodes' depths */
		root.updateDepth();
	}

	/**
	 * Returns this tree's registered TreeInterpreter.
	 * 
	 * @return this tree's TreeInterpreter.
	 */
	public TreeInterpreter getInterpreter() {
		return interpreter;
	}

	/**
	 * Registers a TreeInterpreter.
	 * 
	 * @param interpreter
	 *            The TreeInterpreter to be registered.
	 */
	public void setInterpreter(TreeInterpreter interpreter) {
		this.interpreter = interpreter;
	}

	/**
	 * Gets this tree's root node.
	 * 
	 * @return The root node of this tree.
	 */
	public TreeNode getRoot() {
		return root;
	}

	/**
	 * Get a set containing all OrbitRepresentatives in leaves of this tree.
	 * 
	 * @return A Set containing this tree's leaves.
	 */
	public Set<OrbitRepresentative> getLeaves() {
		return leaves;
	}

	/**
	 * Writes this tree to file, in a form that can be read by the appropriate constructor of OrbitTree.
	 * @see OrbitTree#OrbitTree(String)
	 * @param filename The name of the file that will be saved.
	 */
	public void write(String filename) {
		try {
			PrintWriter ps = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
			ps.println(root.write());
			ps.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Reads an OrbitTree in from file. Files read by the write method can be read.
	 * @see OrbitTree#write(String)
	 * @param filename The name of the file that will be read.
	 */
	public OrbitTree(String filename) {
		File file = new File(filename);
		order = 0;
		try {
			Scanner scanner = new Scanner(file);
			String s = scanner.nextLine();
			OrbitRepresentative o = new OrbitRepresentative();
			root = new AddNodeNode(o,this );
			TreeNode tn = root;
			while (scanner.hasNextLine()) {
				s = scanner.nextLine();
				String[] pieces = s.split(" ");
				if(pieces[0].charAt(0)=='/'){
					tn = tn.parent;
				}else if (tn instanceof AddNodeNode) {
					AddNodeNode ann = (AddNodeNode) tn;
					int edge =Integer.parseInt(pieces[0]);
					o = new OrbitRepresentative(ann.getOrbitRepresentative());
					o.addNode(edge);
					if(o.order()>order)order=o.order();
					switch(pieces[1].charAt(0)){
					case 'n':
						tn=new AddNodeNode(o,ann);
						break;
					case 'e':
						tn = new AddEdgeNode(o,ann,Integer.parseInt(pieces[2]));
						break;
					case 'c':
						tn = new ConditionNode(tn, Integer.parseInt(pieces[2]), Integer.parseInt(pieces[3]));
						break;
					}
					ann.addChild(edge, tn);
				}else if(tn instanceof AddEdgeNode){
					AddEdgeNode ann = (AddEdgeNode) tn;
					boolean edge = Boolean.parseBoolean(pieces[0]);
					o = new OrbitRepresentative(ann.getOrbitRepresentative());
					if(edge){
						o.addEdge(o.order()-1, ann.getEdge());
					}
					switch(pieces[1].charAt(0)){
					case 'n':
						tn=new AddNodeNode(o,ann);
						break;
					case 'e':
						tn = new AddEdgeNode(o,ann,Integer.parseInt(pieces[2]));
						break;
					case 'c':
						tn = new ConditionNode(tn, Integer.parseInt(pieces[2]), Integer.parseInt(pieces[3]));
						break;
					}
					ann.addChild(tn, edge);
				}else if(tn instanceof ConditionNode){
					ConditionNode ann = (ConditionNode) tn;
					switch(pieces[0].charAt(0)){
					case 'n':
						tn=new AddNodeNode(o,ann);
						break;
					case 'e':
						tn = new AddEdgeNode(o,ann,Integer.parseInt(pieces[1]));
						break;
					case 'c':
						tn = new ConditionNode(tn, Integer.parseInt(pieces[1]), Integer.parseInt(pieces[2]));
						break;
					}
					ann.setChild(tn);
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			System.out.println("Ongeldige bestandsnaam");
		}
	}

	public static void main(String[] args) {
		OrbitIdentification.readGraphlets("Przulj.txt", 6);
		OrbitTree ot = new OrbitTree(4);
		ot.root.printTree("");
//		ot.write("treeText.txt");
//		OrbitTree read = new OrbitTree("treeText.txt");
//		System.out.println();
//		read.root.printTree("");
	}
	
	/**
	 * Prints the tree in human-readable form to the console.
	 */
	public void print(){
		root.printTree("");
	}
	
	/**
	 * Returns the largest graphlet order of the orbit representatives in this tree.
	 * @return The largest graphlet order in this tree.
	 */
	public int getOrder(){
		return order;
	}

}
