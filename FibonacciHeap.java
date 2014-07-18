package ADS;

import java.util.Hashtable;


public class FibonacciHeap {
	
	Node min;								/* Pointer that holds minimum node */
	
	Hashtable<Integer, Node> entryTable = new Hashtable<Integer, Node> (); /* entryTable is a hashtable that helps access node in heap */
	
	/* Node data structure containing edge, nodenumber and degree primarily */
	class Node {
		int nodeNumber;
		int edgeWeight;
		int degree;
		
		Node next;
		Node prev;
		Node child;
		Node parent;
		
		boolean isMarked;
		
		public Node(int n, int e) {
			nodeNumber = n;
			edgeWeight = e;
			degree = 0;
			next = this;
			prev = this;
			parent = null;
		}
	}
	
	public FibonacciHeap() {
		min = null;
	}
	
	/*
	 *  Function to get weight, given node in heap 
	 */
	public int getWeight(Node node) {
		if (node != null) {
			return node.edgeWeight;
		}
		
		return -1;
	}
	
	/* 
	 * Melds a circular linked list with the fibonacci heap
	 */
	public void meld (Node entry) {
		
		Node 	iter, minNode = null; 
		int 	minEdge = Integer.MAX_VALUE;
		
		if (min == null) {
			min = entry;
			return;
		}
		
		if (entry == null) {
			return;
		}
		
		iter = entry;
		
		do {
			if (iter.edgeWeight < minEdge) {
				minNode = iter;
				minEdge = iter.edgeWeight;
			}
			iter = iter.next;
		} while (iter != entry);

		iter = entry.prev;
		iter.next = min.next;
		entry.prev = min;
		
		iter.next.prev = iter;
		min.next = entry;
		
		if (getWeight(min) > minEdge) {
			min = minNode;
		}
	}
	
	/* 
	 * Insert a node into the fibonacci heap.
	 */
	public void insertNode (int n, int e) {
		Node entry = new Node(n, e);
		
		entryTable.put(n, entry);
		
		meld (entry);
			
	}
	
	/*
	 * Function to remove minimum element in heap.
	 */
	public int removeMin () {
		
		Node child, iter, returnNode;
		Hashtable<Integer, Node> table;
		
		if (min == null) {
			return -1;
		}
		
		returnNode = min;
		child = min.child;
		
		/* If heap contains one element only */
		if (min.next == min ) {
			min = null;
		} else {
			min.next.prev = min.prev;
			min.prev.next = min.next;
			min = min.next;
		}
				
		iter = child; 
		
		if (iter != null) {
			/* Make all children of minimum element orphans by marking their parents null */
			do {
				iter.parent = null;
				iter = iter.next;
			} while (iter != null && iter != child);
		}
				
		if (child != null) {
			/* Meld children with the heap */
			meld(child);
		}
		
		table = new Hashtable<Integer, Node> ();
		
		Node toBeChild, toBeParent, temp, start;
		iter = min;
		start = min;
		
		/* We consolidate here */
		if (iter != null) {
			do {
				
				if (getWeight(iter) < getWeight(min)) {
					min = iter;
				}
				
				/* 
				 * table is maintained to keep track of the degrees currently present. If table doesn't have the degree of the node
				 * in question we simply add the degree to the table and move to next. Else we need more processing.
				 */
				if (!table.containsKey(iter.degree)) {
					table.put(iter.degree, iter);
					iter = iter.next;
					continue;
				}
				
				int degree = iter.degree;
				
				Node next = iter.next;
				while (table.containsKey(degree)) {
					/*
					 * If table contains the degree then that node that is already present we get that node and compare it with our node
					 * The smaller one becomes the parent and the larger one becomes the child.
					  */
					toBeParent = table.remove(degree);
					toBeChild = iter;
					
					/*
					 * Decide who should be parent here
					 */
					if (getWeight(toBeChild) < getWeight(toBeParent)) {
						temp = toBeChild;
						toBeChild= toBeParent;
						toBeParent = temp;
					}
					
					/* 
					 * We have a start variable to stop the loop. The start variable was earlier the next element in the circular linked
					 * list of min element ater it was removed. Now if start becomes a child of someone else we have to update start
					 * to the next node in linked list. 
					 */
					if (toBeChild == start) {
						start = start.next;
					}
					/*
					 * If the next in iteration becomes a child, then update next to its next.
					 */
					if (toBeChild == next) {
						next = next.next;
					}
					/* 
					 * Similarly update min to parent
					 */
					if (toBeChild == min) {
						min = toBeParent;
					}
					
					/*
					 * This is to remove a node (toBeChild) and make it a child of toBeParent
					 */
					toBeChild.prev.next = toBeChild.next;
					toBeChild.next.prev = toBeChild.prev;
					
					if (toBeParent.child == null) {
						toBeChild.next = toBeChild;
						toBeChild.prev = toBeChild;
					} else {
						toBeChild.next = toBeParent.child;
						toBeParent.child.prev.next = toBeChild;
						toBeChild.prev = toBeParent.child.prev;
						toBeParent.child.prev = toBeChild;
					}
					toBeParent.child = toBeChild;
					toBeChild.parent = toBeParent;
					
					toBeParent.degree++;
					degree = toBeParent.degree;
					iter = toBeParent;
				}
				table.put(degree, iter);
				iter = next;
			} while (iter != null && iter != start);
		}
		
		/*
		 * Update the backdoor hashtable by removing the element
		 */
		entryTable.remove(returnNode.nodeNumber);
		
		return returnNode.nodeNumber;
		
	}
	
	/*
	 * Funtion to decrease key of a node. 
	 */
	public void decreaseKey (int nodeId, int newWeight) {
		int oldWeight;
		Node node, parent;
		
		node = entryTable.get(nodeId);
		
		if (node == null) {
			return;
		}
		
		/*
		 * Return if new key is greater than old key
		 */
		oldWeight = getWeight(node);
		if (newWeight >= oldWeight) {
			return;
		}
		
		node.edgeWeight = newWeight;
		parent = node.parent;
		if (parent != null && getWeight(node) <= getWeight(parent))
			cutNode(node);
		
		if (min.edgeWeight >= node.edgeWeight) 
			min = node;
		
	}
	
	/*
	 * Check if heap has a particular node
	 */
	public boolean isPresent(int Node) {
		return entryTable.containsKey(Node);
	}
	
	/* 
	 * Cut a node and merge it into circular linked list if its key has decreased to a value less than parent or if it has lost a 
	 * second child. This function is called by decrease key. It takes the node and simly merges it with the top level linked
	 * list.
	 */
	public void cutNode(Node node) {
		
		Node parent;
		
		parent = node.parent;
		node.isMarked = false;
		if (parent == null) {
			/*
			 * If we have no parent then we don't need to cut-cascade
			 */
			return;
		}
		
		/*
		 * Remove from parent and put it into top level list
		 */
		if (node.next == node) {
			parent.child = null;
		} else {
			if (parent.child == node) {
				parent.child = node.next;
			}
			node.next.prev = node.prev;
			node.prev.next = node.next;
			parent.child.parent = parent;
		}
		
		node.parent = null;
		node.next = node.prev = node;
		/*
		 * Put it into top level list
		 */
		meld(node);
		
		parent.degree--;
		
		/*
		 * If parent is marked cut cascade on parent. Else mark him.
		 */
		if (parent.isMarked) {
			cutNode(parent);
		} else {
			parent.isMarked = true;
		}
		
	}

}
