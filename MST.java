package ADS;
import java.util.*;

import java.io.*;


/*
 * A graph instance
 */
class Graph {

	int 	NumberOfNodes;
	int 	density;
	int 	dest;
	int [] 	marker;
	boolean remap = true;
	Hashtable<Integer,Hashtable<Integer, Integer>> AdjacencyList = new Hashtable<Integer,Hashtable<Integer,Integer>>(); 
	Hashtable<Integer,Hashtable<Integer, Integer>> MSTAdjacencyList, fiboAdjacencyList;
	Hashtable<Integer,Hashtable<Integer, Integer>> MSTAdjacencyListUndirected, fiboAdjacencyListUndirected;
	
	
	int min_weight = 1;
	int max_weight = 1000 - min_weight, weight;
	
	
	public Graph (int n, int d) {
		
		NumberOfNodes = n;
		density = d;
		marker = new int[n];
		
		markerClear();
		
	}
	
	/*
	 * Marker used to check if a node has been visited
	 */
	public void markerClear () {
		int i = 0;
		while (i < NumberOfNodes) {
			marker[i++] = 0;
		}
	}
	
	/*
	 * Generate a graph either randomly or by reading from input file
	 */
	public void GenerateGraph (boolean random, int number, int density, String file) {
		
		int edges, i = 0, j = 0, count = 0;
		int remap = 1;
		
		edges = (int) (density*(NumberOfNodes)*(NumberOfNodes-1)/200);
		
		
		Hashtable<Integer,Integer> Node1, Node2, Node, New;
		MyQu q = new MyQu();
		
		if (random) {
			/* 
			 * For random, logic is straight forward. Generate i and j and apply constraints of i != j and make
			 * sure it is a connected graph. Else remap.
			 */
			while (remap != 0) {
				
				count = 0;
				AdjacencyList.clear();
				markerClear();
				while (count < edges) {
					i = 0; j = 0;
					
					while (i == j) {
						i = (int)(Math.random()*NumberOfNodes);
						j = (int)(Math.random()*NumberOfNodes);
					}
					
					if (!AdjacencyList.containsKey(i)) {
						AdjacencyList.put(i, new Hashtable<Integer,Integer>());
					}
					
					if (!AdjacencyList.containsKey(j)) {
						AdjacencyList.put(j, new Hashtable<Integer,Integer>());
					}
					
					Node1 = AdjacencyList.get(i);
					Node2 = AdjacencyList.get(j);
					
					if (!Node1.containsKey(j)) {
					
						weight = min_weight + (int)(Math.random()*max_weight);
								
						Node1.put(j, weight);
					}
					if (!Node2.containsKey(i)) {
						
						Node2.put(i, weight);
						count++;
					}
					
				}
				
				Node = AdjacencyList.get(0);
				
				Enumeration<Integer> e;
				q.add(dest);
				marker[dest] = 1;
				
				/*
				 * Make sure graph is connected by adding nodes into stack. The name of the stack is queue. So please don't get
				 * confused :). It is a depth first search and neighbours are marked repeatedly. ALl neighbours should 
				 * be marked in the end
				 */
				while (q.isEmpty() == 0) {
					dest = q.delete();
					marker[dest] = 1;
					Node = AdjacencyList.get(dest);
					if (Node == null) {
						break;
					}
					e = Node.keys();
					
					while (e.hasMoreElements()) {
						dest = e.nextElement();
						if (0 == marker[dest]) {
							q.add(dest);
							marker[dest] = 1;
						}
					}
				}
				
				remap = 0;
				for (i = 0; i < NumberOfNodes; i++) {
					if (0 == marker[i]) {
						remap = 1;
						break;
					}
				}
			} 
		} else {
			
			/*
			 * We scan from file here. Get edges and vertices and store into adjacency list. Logic is straightforward
			 * and routine. 
			 */
			try {
				Scanner scanner = new Scanner(new File(file));
				int vertices = scanner.nextInt();
				NumberOfNodes = vertices;
				scanner.nextInt();
					
				for(int iter = 0; iter < vertices; iter++){
					New = new Hashtable<Integer,Integer>();
					AdjacencyList.put(iter, New);
					 
				}
					
				while(scanner.hasNextInt()){
					int ver1=scanner.nextInt();
					if(scanner.hasNext()){
						int ver2=scanner.nextInt();
						if(scanner.hasNext()){
							int cost=scanner.nextInt();
							   
							Hashtable<Integer, Integer> h1=AdjacencyList.get(ver1);
							h1.put(ver2,cost);
							AdjacencyList.put(ver1, h1);
							 
							Hashtable<Integer, Integer> h2 = AdjacencyList.get(ver2);
							h2.put(ver1, cost);
							AdjacencyList.put(ver2, h2);
						}
					}
				}
			} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
			
		}
		
		
	}
	
	/*
	 * Prim's algorithm for simple array
	 */
	public int primSimple() {
		
		
		Hashtable<Integer, Integer> Node, Node1, Node2, Node3;
		int 						NodeId1, NodeId2, minNode1 = -1, minNode2 = -1;
		int 						minimumWeight, noEdgesAdded = 0;
		int 						cost = 0;
		
		MSTAdjacencyList = new Hashtable<Integer,Hashtable<Integer,Integer>> (); 
		MSTAdjacencyListUndirected = new Hashtable<Integer,Hashtable<Integer,Integer>> (); 
		
		MSTAdjacencyList.put(0, new Hashtable<Integer,Integer>());
		MSTAdjacencyListUndirected.put(0, new Hashtable<Integer,Integer>());
		
		/*
		 * Repeat till we have n-1 nodes. Standard prims algrithm is implemented. We scan through all nodes giving O(n^3) 
		 * complexity. We have a separate Undirected graph that lets us print it easily in the end.
		 */
		while (noEdgesAdded != NumberOfNodes-1) {
			Enumeration<Integer> nodes = MSTAdjacencyList.keys();
			minimumWeight = max_weight + 1;
			minNode1 = -1; minNode2 = -1;
			
			/* 
			 * Iteration over all nodes in MST graph
			 */
			while (nodes.hasMoreElements()) {
				NodeId1 = nodes.nextElement();
			
				Node = AdjacencyList.get(NodeId1);
				Enumeration<Integer> edges = Node.keys();
				/*
				 * Iteration over all neighbours of node
				 */
				while (edges.hasMoreElements()) {
					NodeId2 = edges.nextElement();
					if (MSTAdjacencyList.containsKey(NodeId2)) {
						continue;
					}
					
					/*
					 * Gets node that has minimum edge length from the existing MST graph
					 */
					if (Node.get(NodeId2) < minimumWeight) {
						minimumWeight = Node.get(NodeId2);
						minNode1 = NodeId1;
						minNode2 = NodeId2;
					}
				}
			}

			/*
			 * We got an edge. Put it into our MST Adjacency List
			 */
			if (minNode2 != -1 && !MSTAdjacencyList.containsKey(minNode2)) {
				MSTAdjacencyList.put(minNode2, new Hashtable<Integer,Integer>());
				MSTAdjacencyListUndirected.put(minNode2, new Hashtable<Integer,Integer>());
			}
			
			if (minNode1 >= 0 && minNode2 >= 0) {
				
				Node1 = MSTAdjacencyList.get(minNode1);
				Node1.put(minNode2, minimumWeight);
				Node2 = MSTAdjacencyList.get(minNode2);
				Node2.put(minNode1, minimumWeight);
				Node3 = MSTAdjacencyListUndirected.get(minNode1);
				Node3.put(minNode2, minimumWeight);
				cost += minimumWeight;
				noEdgesAdded++;
			}
		}
		
		return cost;
	
	}
	
	/*
	 * Function gets minimum edge from node with nodeId to Minimum Spanning Tree 
	 */
	private int getEdgeEnd(int nodeId) {
		Hashtable<Integer,Integer> 	node;
		Enumeration<Integer> 		e;
		int 						otherEdge, minEdgeValue = Integer.MAX_VALUE, minEdge = -1;
		
		node = AdjacencyList.get(nodeId);
		e = node.keys();
		
		/*
		 * Iteration over neighbours of node to get minimum edge. But we don't consider those nodes that the given node is connected
		 * to that are not present in the MST. 
		 */
		while (e.hasMoreElements()) {
			otherEdge = e.nextElement();
			
			/*
			 * If not in MST skip
			 */
			if (!fiboAdjacencyList.containsKey(otherEdge)) {
				continue;
			}
			
			if (node.get(otherEdge) < minEdgeValue) {
				minEdgeValue = node.get(otherEdge);
				minEdge = otherEdge;
			}
			
		}
		
		return minEdge;
				
	}
	
	/*
	 * Method runs prim using fibonacci sequence.
	 */
	public long primFibo() {
		
		int 						noEdgesAdded = 0, min, newNode, edgeEnd;
		Hashtable<Integer,Integer> 	node, addNew, addOld,addUnd;
		Enumeration<Integer> 		e;
		long 						cost = 0;
		
		fiboAdjacencyList = new Hashtable<Integer,Hashtable<Integer,Integer>> ();
		fiboAdjacencyListUndirected = new Hashtable<Integer,Hashtable<Integer,Integer>> ();
		FibonacciHeap heap = new FibonacciHeap ();
		
		/*
		 * Insert node 0 with edge length 0 to kick start the process.
		 */
		heap.insertNode(0, 0);
		
		/* 
		 * Iterate n-1 times, where n is number of nodes
		 */
		while (noEdgesAdded != NumberOfNodes) {
			/*
			 * Get minimum element in heap
			 */
			min = heap.removeMin();
			noEdgesAdded++;

			if (min == -1) {
				/*
				 * This shouldn't happen.
				 */
			}
			/*
			 * Get minimum edge from node not in MST to some node in MST
			 */
			edgeEnd = getEdgeEnd(min);
			
			/*
			 * Add new found node to MST
			 */
			addNew = new Hashtable<Integer, Integer> ();
			fiboAdjacencyList.put(min, addNew);
			addUnd = new Hashtable<Integer, Integer> ();
			fiboAdjacencyListUndirected.put(min, addUnd);
			
			if (edgeEnd != -1) {
				
				/*
				 * Add edge to MST.
				 */
				int weightAdd = (AdjacencyList.get(min).get(edgeEnd));
				addNew.put(edgeEnd, weightAdd );
				addOld = fiboAdjacencyList.get(edgeEnd);
				addOld.put(min, weightAdd);
				addUnd = fiboAdjacencyListUndirected.get(min);
				addUnd.put(edgeEnd, weightAdd);
				cost += weightAdd;
				
			}
			
			/*
			 * Get neighbours of node popped out of minimum heap
			 */
			node = AdjacencyList.get(min);
			
			e = node.keys();
			
			/*
			 * Iterate through neighbours of min and decrease keys or insert into heap if not already there
			 */
			while (e.hasMoreElements()) {
				
				newNode = e.nextElement();
				if (fiboAdjacencyList.containsKey(newNode)) {
					continue;
				}
				
				if (heap.isPresent(newNode)) {
					/*
					 * Decrease returns if new edge weight is higher
					 */
					heap.decreaseKey(newNode, node.get(newNode));
				} else {
					heap.insertNode(newNode, node.get(newNode));
				}
			}
			
		}
		return cost;
	}
	
	/*
	 * Function to print graph. Takes in boolean to indicate which of the 2 graphs
	 */
	public void printGraphMST (long fiboCost, long simpleCost, boolean fibo) {
		
		Enumeration<Integer> 	nodes = MSTAdjacencyList.keys();
		Enumeration<Integer> 	nodes1 = fiboAdjacencyList.keys();
		int 					NodeId1, NodeId2;
		Hashtable<Integer, Integer> Node;
	
		
		if (!fibo) {
			System.out.println(simpleCost);
			while (nodes.hasMoreElements()) {
				NodeId1 = nodes.nextElement();
				Node = MSTAdjacencyListUndirected.get(NodeId1);
				Enumeration<Integer> edges = Node.keys();
				while (edges.hasMoreElements()) {
					NodeId2 = edges.nextElement();
					System.out.println(NodeId1 + " " + NodeId2);
				}
			}
		} else {
			
			System.out.println();
			System.out.println(fiboCost);
			
			while (nodes1.hasMoreElements()) {
				NodeId1 = nodes1.nextElement();
				Node = fiboAdjacencyListUndirected.get(NodeId1);
				Enumeration<Integer> edges = Node.keys();
				while (edges.hasMoreElements()) {
					NodeId2 = edges.nextElement();
					System.out.println(NodeId1 + " " + NodeId2);
				}
			}
		}
		
	}
}
			
			
public class MST {
	
	public static void main (String args[]) {
		
		int 	nodesNumber = 0;
		int 	density = 0;
		long 	startFib, endFib, startSim, endSim;
		String 	file = null;
		long 	fiboCost = 0, simpleCost = 0;
		boolean random = false, fibo = false;;
		
		/* Get mode */
		if (args[0].equals("-f") || args[0].equals("-s")) {
			file = args[1];
			if (args[1].equals("-f")) {
				fibo = true;
			}
		} else {
			if (args[0].equals("-r")) {
				random = true;
				nodesNumber = Integer.parseInt(args[1]);
				density = Integer.parseInt(args[2]);
			}
		}
		
		/* Create new graph with given nodesNumber and density */
		Graph g = new Graph(nodesNumber,density);
		
		/*
		 * Generate graph either randomly or from file input.
		 */
		g.GenerateGraph(random, nodesNumber, density, file);
		
		/*
		 * Run prim Simple
		 */
		startSim = System.currentTimeMillis();
		simpleCost = g.primSimple();
		endSim = System.currentTimeMillis();
		
		/*
		 * Run prim Fibonacci
		 */
		startFib = System.currentTimeMillis();
		fiboCost = g.primFibo();
		endFib = System.currentTimeMillis();
		
		if (random) {
			System.out.println("Simple Prime Runtime: " + (endSim-startSim));
			System.out.println("Fibonacci Prim Runtime: " + (endFib-startFib));
		} else {  
			g.printGraphMST(simpleCost, fiboCost, fibo);
		}
		
	}
}

	
