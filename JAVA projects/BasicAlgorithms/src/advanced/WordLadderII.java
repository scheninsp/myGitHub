package advanced;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class WordLadderII {
	//from leetcode Word Ladder II
	//https://leetcode.com/problems/word-ladder-ii/description/
	// This is a naive version which costs O(N^k) in DFS period.
	
	public static void main(String args[]){
		
//		String beginWord = "hit";
//		String endWord = "cog";
//		
////		String cur = beginWord;
//		List<String> wordList = new ArrayList<String>();
//		wordList.add("hot");
//		wordList.add("dot");
//		wordList.add("dog");
//		wordList.add("lot");
//		wordList.add("log");
//		//wordList.add("cog");
		
		//-------------------------------
		
		String beginWord = "red";
		String endWord = "tax";
		
//		String cur = beginWord;
		List<String> wordList = new ArrayList<String>();
		wordList.add("ted");
		wordList.add("tex");
		wordList.add("red");
		wordList.add("tax");
		wordList.add("tad");
		wordList.add("den");
		wordList.add("rex");
		wordList.add("pee");
		
		//--------------------------------
		
		
		//test findNeighbours
//		LinkedList neighbours = findNeighbours(cur,wordList);
//		
//		Node p = neighbours.head;
//		for(int i=0;i<neighbours.len;i++){
//			System.out.println(p.content);
//			p = p.next;
//		}
		
		List<List<String>> solution = findLadders(beginWord,endWord,wordList); 
		System.out.println(solution);
	}
	
	
    public static List<List<String>> findLadders(String beginWord, String endWord, List<String> wordList) {
    	
    	int INF = 99999;
    	
    	List<List<String>> solution = new ArrayList<List<String>>();    	
    	
    	//create HashMap <String, Integer> for distance
    	//create HashMap <String, LinkedList> for nodeTrace , predecessor to this String 	
    	
    	int nWordList = wordList.size();
    	int hasEndWord=0;
    	int hasBeginWord=0;
    	if(wordList.indexOf(endWord)>=0){
    		hasEndWord = 1;
    	}
    	if(wordList.indexOf(beginWord)>=0){
    		hasBeginWord = 1;
    	}
    	int nAdd = 1-hasBeginWord;
    	
		NodeVal[] distance = new NodeVal[nWordList+nAdd];
		NodeList[] nodeTrace = new NodeList[nWordList+nAdd];
		boolean[] flagVisited = new boolean[nWordList+nAdd];
		
		if(hasEndWord==0){return solution;}  //return [] if endWord cannot be reached
//		if(hasEndWord==0){
//			wordList.add(endWord);
//			nWordList++;
//		}
//		
    	//initialization
    	for(int i=0; i<nWordList; i++){
    		nodeTrace[i+(1-hasBeginWord)] = new NodeList(wordList.get(i),new LinkedList());
    		distance[i+(1-hasBeginWord)] = new NodeVal(wordList.get(i),INF);
    		flagVisited[i+(1-hasBeginWord)] = false;
    	}
		if(hasBeginWord == 0){
			distance[0] = new NodeVal(beginWord,0);
			nodeTrace[0] = new NodeList(beginWord,new LinkedList());
			flagVisited[0] = false;
		}
		else{
			int temp = findIndex(beginWord,distance);
			distance[temp].val = 0;
		}

    	
		LinkedList queue = new LinkedList();
		queue.addLast(new Node(beginWord));
		
		//dijkstra (bfs)
		while(!queue.isempty()){
			//update all d_min(start -> cur -> cur's neighbours)
			String cur = queue.dequeue().content;				
			int curIndex = findIndex(cur,distance);
			int curDist = distance[curIndex].val;   //d_min(start -> cur)
			
			if(flagVisited[curIndex] == true) {continue;}
			
			flagVisited[curIndex] = true;
			LinkedList cur_neighbours = findNeighbours(cur,wordList);
			
			Node p = cur_neighbours.head;
			while(p!=null){
				int neighbourIndex = findIndex(p.content,distance);
				int neighbourDist = distance[neighbourIndex].val;  //d_min(start -> neighbour)
				
				if(flagVisited[neighbourIndex] == true) {p = p.next; continue;}
				else{
					queue.addLast(new Node(p.content));

					if(curDist + 1 < neighbourDist){ 
						distance[neighbourIndex].val = curDist + 1;
						nodeTrace[neighbourIndex].list = new LinkedList();
						nodeTrace[neighbourIndex].list.addLast(new Node(cur));  //modify the shortest route to neighbour
					}
					else if (curDist + 1 == neighbourDist){
						nodeTrace[neighbourIndex].list.addLast(new Node(cur));  //add a new shortest route to neighbour
					}
					p = p.next;
				}
			}// iterate all neighbours of cur, update d_min(start -> neighbour)
			
		}
		
		//trace back to find shortest path
		int endWordIndex = findIndex(endWord,distance); 
		LinkedList endList = nodeTrace[endWordIndex].list;
		Stack stack = new Stack();		   //Stack of NodeVals (String, depth)
		Stack oneSolution = new Stack();   //Stack of Nodes  (String)
		oneSolution.push(new Node(endWord));
		
		Node p = endList.head;
		while(p!=null){
			stack.push(new NodeVal(p.content,1));  //layer 1
			p = p.next;
		}
		
		//dfs backtracking
		int dep=0;
		NodeVal p3 = null;
		while(!stack.isempty()){
			p3 = (NodeVal)stack.pop();
			dep = p3.val;
			int oneSolutionLen = oneSolution.len;
			for(int i=0; i<(oneSolutionLen-dep); i++){  //if oneSolution.len - dep >= 1, needs to pop
				oneSolution.pop();
			}
			oneSolution.push(new Node(p3.content));
			
			while(!p3.content.equals(beginWord)){
				int pIndex = findIndex(p3.content,nodeTrace);
				LinkedList tempList = nodeTrace[pIndex].list;
				//System.out.print(nodeTrace[pIndex].content+":  ");
				//tempList.print();
				oneSolution.push(new Node(tempList.head.content));
				dep++;
				p3 = new NodeVal(tempList.head.content,dep);
				
				if(tempList.len > 1){
					Node p2 = tempList.head.next;  //second node to end
					while(p2!=null){
						NodeVal tempNode2 = new NodeVal(p2.content,dep);
						if(stack.hasNoNodeVal(tempNode2)){
							stack.push(tempNode2);
						}
						p2 = p2.next;
					}
				}
			}
			
			List<String> temp = new ArrayList<String>();
			Node temp2 = oneSolution.top;
			while(temp2!=null){
				temp.add(temp2.content);
				temp2 = temp2.next;
			}
			solution.add(temp);
			
		}
		
		return solution;
    }
    
    //search index in a table
    public static int findIndex(String cur, Node[] list){
    	for(int i=0;i<list.length;i++){
    		if(list[i].content.equals(cur)){
    			return i;
    		}
    	}
    	return -1;
    }
    
    //search neighbours for a given Node
    public static LinkedList findNeighbours(String cur, List<String> wordList){
    	
    	LinkedList neighbours = new LinkedList();
    	
    	ListIterator<String> e = wordList.listIterator();
    	while(e.hasNext()){
    		String strtemp = e.next();
    		if(strtemp.length() != cur.length()) {continue;}
    		
    		int cnt = 0;
    		for(int i=0; i<cur.length(); i++){
    			if(cur.charAt(i) != strtemp.charAt(i)) {cnt++;}
    		}
    		if(cnt == 1){
    			neighbours.addLast(new Node(strtemp));
    		}
    	}
    	
    	return neighbours;
    	
    }
    
    //LinkedList
    static class LinkedList{  //can also be used as queue
    	Node head;
    	Node tail;
    	int len;
    	
    	LinkedList(){
    		head = null;
    		tail = null;
    		len = 0;
    	}
    	
    	public boolean isempty(){
    		return (len==0);
    	}
    	
    	public void addLast(Node n){
    		if(len==0) {
    			head = n;
    			tail = head;
    			}
    		else{
    			tail.next = n;
    			tail = tail.next;
    		}
			len++;
    	}
    	
    	public Node dequeue(){
    		if(len==0){
    			return null;
    		}
    		else if(len==1){
    			Node temp = head;
    			head = null;
    			tail = null;
    			len--;
    			return temp;
    		}
    		else{
    			Node temp = head;
    			head = head.next;
    			len--;
    			return temp;
    		}
    	}
    	
    	public Node get(String key){
    		if (len == 0) {return null;}
    		else{
    			Node p = head;
    			while(p!= null){
    				if(key == p.content) {return p;}
    				p = p.next;
    			}
    			return null;
    		}
    	}
    	
    	public void print(){
    		Node p=head;
    		while(p!=null){
    			System.out.print(p.content + " -> ");
    			p = p.next;
    		}
    		System.out.print("\n");
    	}
    	
    }
    
    
    //Stack
	static class Stack{
		Node top;
		int len;
		
		Stack(){
			top = null;
			len = 0;
		}
		
		public void push(Node n){
			n.next = top;
			top = n;
			len++;
		}
		
		public Node pop(){
			if (!isempty()){
				Node temp = top;
				top = top.next;
				len--;
				return temp;
			}
			else{
				return null;
			}
		}
		
		public boolean isempty(){
			return (len == 0);
		}
		
		public boolean hasNoNodeVal(NodeVal n){
			NodeVal p = (NodeVal)top;
			while(p!=null){
				if(p.equals(n)){return false;}
				p = (NodeVal)p.next;	
			}
			return true;
		}
	}
    
	//Nodes
    static class Node{
    	String content;
    	Node next;
    	
    	Node(){
    		content = "";
    		next = null;
    	}
    	
    	Node(String c){
    		content = c;
    		next = null;
    	}
    }
    
    static class NodeVal extends Node{
    	int val;
    	
    	NodeVal(String c, int d){
    		super(c);
    		val = d;
    	}
    	
    	public boolean equals(NodeVal n){
    		return (this.val == n.val && this.content.equals(n.content)); 
    	}
    }
    
    static class NodeList extends Node{
    	LinkedList list;
    	
    	NodeList(String c, LinkedList l){
    		super(c);
    		list = l;
    	}
    }
    
}
