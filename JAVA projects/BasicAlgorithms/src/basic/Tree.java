package basic;

public class Tree {
	Node root;
	
	public Node find(int key){
		Node cur = root;
		
		while(cur.iData != key){    
			if (cur.iData > key)
				cur = cur.leftchild;
			else
				cur = cur.rightchild;
			
			if (cur == null)
				return null;
		}
		return cur;
	}

	public void insert(int id, double dd){
		if (root == null){   //tree is empty
			root = new Node();
			root.iData = id;
			root.fData = dd;
		}	

		Node cur = root;
		while(true){ 
			if (id <= cur.iData){
				if (cur.leftchild != null)
					cur = cur.leftchild;
				else{
					cur.leftchild = new Node();
					cur.leftchild.iData = id;
					cur.leftchild.fData = dd;
					return;
				}
			}
			else{
				if (cur.rightchild != null)
					cur = cur.rightchild;
				else{
					cur.rightchild = new Node();
					cur.rightchild.iData = id;
					cur.rightchild.fData = dd;
					return;
				}
			}
		}
	}
	
	
	public Node cutSuccessor(Node node){  
		//move successor's rightchild to its parent's leftchild
		
		Node parent = node;
		Node successor = node;
		Node cur = node.rightchild;
		while( cur != null){
			
			parent = successor;
			successor = cur;
			cur = cur.leftchild;
		}
		
		if (successor != node.rightchild){
			parent.leftchild = successor.rightchild;   //(1)
		}
		return successor;
	}
	
	public Node delete(int key){
		
		//find the node to delete
		Node cur = root;
		Node parent = cur;
		boolean isLeftChild = true;
		
		while(cur.iData != key){   
			parent = cur;
			
			if (cur.iData > key){
				isLeftChild = true;
				cur = cur.leftchild;}
			else{
				isLeftChild = false;
				cur = cur.rightchild;}
			
			if (cur == null)
				return null;  //not found
		} //end while
		
		Node node_pop = cur;   //for return the result
		cur = null;  //release cur
		
		//get right/left child info of node_pop
		boolean hasLeftChild = false;
		boolean hasRightChild = false;
		if(node_pop.leftchild != null)
			 hasLeftChild = true;
		if(node_pop.rightchild != null)
			 hasRightChild = true;
		
		//switch case
		if (!hasLeftChild && !hasRightChild ){ //no left and right child
			if(node_pop == root)
				root = null;
			else if(isLeftChild)
				parent.leftchild = null;
			else
				parent.rightchild = null;
		}
		else if( hasLeftChild && !hasRightChild){   //only left child
			if(node_pop == root)
				root =  node_pop.leftchild;
			else if(isLeftChild)
				parent.leftchild = node_pop.leftchild;
			else
				parent.rightchild = node_pop.leftchild;
			
		}
		else if ( !hasLeftChild && hasRightChild){   //only right child
			if(node_pop == root)
				root =  node_pop.rightchild;
			else if(isLeftChild)
				parent.leftchild = node_pop.rightchild;
			else
				parent.rightchild = node_pop.rightchild;
		}
		else{   // both left and right child
			Node successor = cutSuccessor(node_pop);
			if(node_pop == root)
				root =  successor;
			else if(isLeftChild)
				parent.leftchild = successor;    //(2)
			else
				parent.rightchild = successor; 
			
			successor.leftchild = node_pop.leftchild;    //(3)
			successor.rightchild = node_pop.rightchild;  //(4)
		}
			
		return node_pop;   //return the node deleted
		
	}//end delete()
	
}
