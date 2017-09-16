package Tree;

import java.util.LinkedList;

public class TraverseLevelTree {
// This function traverse tree by level
	private TNode root;
	private LinkedList<TNode> headNodeList;  //support variable, .left move to next level, .next move in this level
	private TNode tmpNode; 
	
	public TraverseLevelTree(TNode rt){
		root = rt;
		headNodeList = new LinkedList<TNode>();
		headNodeList.add(rt);
	}
	
	public void fillNextPointerAtLv(TNode rt,int lvl){
		if(rt == null || lvl<0){
			return;
		}
		if(lvl==0){
			System.out.println("linking " + tmpNode.val + "-> " + rt.val);
			tmpNode.next = rt;
			tmpNode = tmpNode.next;
			return;
		}
		
		fillNextPointerAtLv(rt.left,lvl-1);
		fillNextPointerAtLv(rt.right,lvl-1);
	}//end fillNextPointerAtLv()
		
	
	public void fillNextPointerFromRoot(){		
		for(int i=1; i<this.depthFromRoot(); i++){
			tmpNode = new TNode(0);       //head for each level, just a global support variable
			headNodeList.add(tmpNode);    //save head into headNodeList
			fillNextPointerAtLv(root,i);   //will modify tmpNode
		}
	}//end fillNextPointerFromRoot()
	
	public void printNodeAtLv(int lvl){
		TNode p = (TNode)headNodeList.get(lvl);
		
		while(p != null){
			System.out.print(p.val + " ");
			p = p.next;
		}
	}//end printNodeAtLv()
	
	public int depthFromRoot(){
		return depth(root);
	}
	
	private int depth(TNode rt){
		if(rt == null){
			return 0;
		}
		int dl = depth(rt.left);
		int dr = depth(rt.right);
		return dl>dr ? 1+dl:1+dr;
	}//end depth()
	
	public static void main(String args[]){
		
		TNode root = new TNode(1);
		root.left = new TNode(2);
		root.right = new TNode(3);
		TNode p = root.left;
		p.left = new TNode(4);
		p.right = new TNode(5);
		p = root.right;
		p.right = new TNode(6);
		
		TraverseLevelTree tree = new TraverseLevelTree(root);
		
		//int d = tree.depthFromRoot();
		//System.out.println("Depth of Tree: " + d);
		
		tree.fillNextPointerFromRoot();
		
		int lvl = 2;
		tree.printNodeAtLv(lvl);
		
	}//end main	
	
}//end TraverseLevelTree

class TNode{  
	//class Node has been defined in other codes, 
	//exclude other classes including class 'Node' 
	//or change current class name.
	public TNode left, right, next;
	public int val;
	
	public TNode(int x){
		val =x;
	}
}//end TNode



