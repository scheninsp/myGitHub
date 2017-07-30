package c08;
import java.util.List;
import java.util.ArrayList;

public class BinarySearchTree {
	
	private TreeNode root = null;
	private List<TreeNode> nodelist = new ArrayList<TreeNode>();
	
	private class TreeNode{
		private TreeNode leftchild;
		private TreeNode rightchild;
		private int key;
		private TreeNode parent;
	
		public TreeNode (TreeNode leftchild, TreeNode rightchild,
				int key, TreeNode parent){
			this.leftchild = leftchild;
			this.rightchild = rightchild;
			this.key = key;
			this.parent = parent;
		}

		public int getKey(){ return key;}

		public String toString(){
			String leftkey = (leftchild == null?
					"" : String.valueOf(leftchild.key));
			String rightkey = (rightchild ==null?
					"" : String.valueOf(rightchild.key));
			return "("+ leftkey + "," + key + "," + rightkey + ")";
		}
	}
	
	public boolean isEmpty(){ return root == null; }
	public void TreeEmpty() throws Exception{
		if (isEmpty()){
			throw new Exception ("Tree Empty.");
		} 
	}
	
	public TreeNode getRoot(){
		return root;
	}
	
	//search key
	public TreeNode search(int key){
		TreeNode cur = root;
		while(cur!=null && cur.key != key){
			if(cur.key > key){
				cur = cur.leftchild;
			}
			else{
				cur = cur.rightchild;
			}
		}
		return cur;
	}
	
	//insert new Node
	public void insert(int key){
		TreeNode newNode = new TreeNode(null,null,key,null);
		if(root == null){ root = newNode; return; }
		
		TreeNode cur = root;
		TreeNode parent = null;
		
		while(cur!=null){
			if (cur.key == key){return;}

			if(cur.key < key){
				if(cur.rightchild == null){
					cur.rightchild = newNode; 
					newNode.parent = cur;
					return;}
				else {parent = cur; cur = cur.rightchild;}
			}
			else{
				if(cur.leftchild == null){
					cur.leftchild = newNode;
					newNode.parent = cur;
					return;}
				else {parent = cur; cur = cur.leftchild;}
			}
		}
	}
	
	//find the minimum/maximum Node in a Tree whose root is 'node'
	public TreeNode minElemNode(TreeNode node) throws Exception{
		if (node == null){
			throw new Exception("Empty Node.");
		}		
		TreeNode cur = node;
		while (cur.leftchild != null){cur = cur.leftchild;}
		return cur;
	}	
	
	public TreeNode maxElemNode(TreeNode node) throws Exception{
		if (node == null){
			throw new Exception("Empty Node.");
		}		
		TreeNode cur = node;
		while (cur.rightchild != null){cur = cur.rightchild;}
		return cur;
	}
	
	
	
	
	// mid-order traverse
	public TreeNode successor(TreeNode node) throws Exception{
		if(node == null) {return null;}
		if (node.rightchild != null) {
			return minElemNode(node.rightchild);
			//proved:the minimum element in right child is the successor. 
		}
		else{ //this is a little hard to think.
			//case 1:A is the leftchild -> A.parent is the successor
			//case 2:A is the rightchild -> upward search for the 
			//first element > A.
			TreeNode parentNode = node.parent;
			while (parentNode != null && node == parentNode.rightchild){
				node = parentNode;
				parentNode = parentNode.parent;
			}
			return parentNode;
			
		}
	}
	
	public TreeNode precessor(TreeNode node) throws Exception{
		if(node == null) {return null;}
		if(node.leftchild != null) { return maxElemNode(node.leftchild); }
		else{
			TreeNode parentNode = node.parent;
			while(parentNode != null && node == parentNode.leftchild){
				node = parentNode;
				parentNode = parentNode.parent;
			}
			return parentNode;
		}
	}
	
	/*
	public List<Treenode> inOrderTraverseList(){}
	public void inOrderTraverse(TreeNode root){}
	*/
	
	//return Tree as a String
	//public String toStringOfOrderList(){}
	
    public static void testNode(BinarySearchTree bst, TreeNode pNode)  
            throws Exception {  
        System.out.println("current Node: " + pNode);  
        System.out.println("precessor Node: " + bst.precessor(pNode));  
        System.out.println("successor Node: " + bst.successor(pNode));  
    }  
  
    /*
    public static void testTraverse(BinarySearchTree bst) {  
        System.out.println("Traverse: " + bst + "...");  
        System.out.println("Mid-order Traverse Result: " + bst.toStringOfOrderList());  
    }  
	*/
	public static void main(String[] args){
		try{
			int[] keys = new int[] {3,5,6,1,2,4,7};
			BinarySearchTree bst = new BinarySearchTree();

			for(int i=0;i<keys.length;i++){
				bst.insert(keys[i]);
			}
			
			TreeNode maxKeyNode = bst.maxElemNode(bst.getRoot());
			System.out.println("max val in tree = "+ maxKeyNode.getKey());
			testNode(bst,maxKeyNode);
			if (maxKeyNode.parent != null)
				{System.out.print("\n");
				testNode(bst,maxKeyNode.parent);}
		}
		
		catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
	}
	
}
