package Tree;

import java.util.Scanner;

public class SegmentTree {
	
	Node root;
	int segmentLen;
	final int INF = 999;
	
	public SegmentTree(int[] array){
		segmentLen = array.length;
		root = createSegmentTree(0,array.length-1,array);
	}//end SegmentTree
	
	public Node createSegmentTree(int start,int end,int[] array){
	//create the segment tree from start to end in array	
		Node lChild,rChild;
		int theMin;   //VARIABLE DEFEINED IN {} CANNOT BE USED OUTSIDE 
		
		if (end-start > 0){  // >1 elements
			int mid = (start+end)/2;
			lChild = createSegmentTree(start,mid,array);
			rChild = createSegmentTree(mid+1,end,array);
			theMin = getMin(lChild.value,rChild.value);
		}
		else{ //1 element
			lChild = null;
			rChild = null;
			theMin = array[start];
		}
		Node rt = new Node(lChild,rChild,theMin,start,end);
		System.out.println("node ["+start +" "+end+"] created.");
		return rt;
	}
	

	public int findMin(Node node, int fstart, int fend){
		if(fstart<=node.start && fend>=node.end){
			//node's range is completely within range [fstart,fend]
			return node.value;
		}
		else if (fstart> node.end || fend<node.start){
			//node's range is completely outside range [fstart,fend]
			return INF;
		}
		else{
			return getMin(findMin(node.leftChild,fstart,fend), 
					findMin(node.rightChild,fstart,fend));
		}
	}//end findMin
	
	private static int getMin(int x,int y){
	//get minimum of two numbers
		return x<y?x:y;
	}
	private static int[] getNumLine (String str){

		String[] list = str.split(" ");
		int[] array = new int[list.length]; 
		for (int i=0;i<list.length;i++){
				array[i] = Integer.parseInt(
						String.valueOf(list[i]));
		}
		
		return array;
	}//end getNumLine
	
	private static void printArray(int[] array){
		for (int i=0;i<array.length;i++){
			System.out.print(array[i] + " ");
		}
		System.out.print('\n');
	}
	
	public static void main(String args[]){
		Scanner sc = new Scanner(System.in);
		int[] temparray = getNumLine(sc.nextLine());		
		int nCase = temparray[0];
		while(nCase >0){
			temparray = getNumLine(sc.nextLine());	
			int fstart = temparray[0];
			int fend = temparray[1];
			temparray = getNumLine(sc.nextLine());	//actual array
			printArray(temparray);
			
			SegmentTree st = new SegmentTree(temparray);
			System.out.println(st.findMin(st.root,fstart,fend));
			
			nCase--;
		}//end while
		sc.close();
	}//end main
	
}// end class SegmentTree

class Node{
	public Node leftChild,rightChild;
	public int value,start,end;
	
	public Node(){
		leftChild = null;
		rightChild = null;
		value = -1;
		start = -1;
		end = -1;
	}
	
	public Node(Node lChild, Node rChild, int val, int st, int ed){
		leftChild = lChild;
		rightChild = rChild;
		value = val;
		start = st;
		end = ed;
	}
}
