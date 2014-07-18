package ADS;

import java.util.*;

/*
 * Stack implementation for DFS
 */
public class MyQu {
		
		LinkedList<Integer> q; 
		
		public MyQu () {
			q = new LinkedList<Integer>();
		}
		
		public void add(int NodeId) {
			q.add(NodeId);
		}
		
		public int delete() {
			return q.removeLast();
		}
		
		public int isEmpty() {
			try { 
				q.getFirst();
			} catch (NoSuchElementException exception) {
				return 1;
			}
			
			return 0;
		}
	}