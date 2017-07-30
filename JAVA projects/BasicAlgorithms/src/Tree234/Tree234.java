package Tree234;

public class Tree234 {
	private Node root = new Node();
	
	public int find(long key){
		Node cur = root;
		int childNumber;
		while(true){
			childNumber = cur.findItem(key) ;
			if (childNumber != -1)
				return childNumber;
			else if(cur.isLeaf())
				return -1;
			else
				cur = getNextChild(cur,key);
		}
	}
	
	public void insert(long dValue){
		Node cur = root;
		DataItem tempItem = new DataItem(dValue);	
		
		while(true){  //search downward, split whenever met a full node
			if(cur.isFull()){
				split(cur);
				cur = cur.getParent();  //back up
				cur = getNextChild(cur,dValue);
			}// end if (node is not full)
			else if(cur.isLeaf()){
				break;
			}
			else{
				cur = getNextChild(cur,dValue);
			}
		}//end while
		cur.insertItem(tempItem);
		
		System.out.println("Successfully inserted:" + dValue);
	}//end insert
	
	public void split(Node thisNode){  //split one Node for insertion
		
		Node parent;
		int itemIndex;
		
		//separate all elements from the node
		DataItem ItemC = thisNode.removeItem();
		DataItem ItemB = thisNode.removeItem();
		Node child2 = thisNode.disconnectChild(2);
		Node child3 = thisNode.disconnectChild(3);
		Node newRight = new Node();
		
		//get parent  
		if(thisNode == root){
			root = new Node();
			parent = root;
			root.connectChild(0, thisNode);
		}
		else{
			parent = thisNode.getParent();
		}
		
		//process parent  (combined same processes for root case and other cases)
		itemIndex = parent.insertItem(ItemB);
		for (int i=parent.getNumItems()-1; i>itemIndex; i--){
			Node temp = parent.disconnectChild(i);
			parent.connectChild(i+1, temp);   //shift right
		}
		parent.connectChild(itemIndex+1,newRight);
		
		newRight.insertItem(ItemC);
		newRight.connectChild(0, child2);
		newRight.connectChild(1, child3);
	}//end split()
	
	public Node getNextChild(Node theNode, long theValue){ //get the appropriate child for key:theValue
		
		int i;
		for (i=0;i<theNode.getNumItems();i++){ 
			//System.out.println("trying to get itemNum:" + i);
			if(theValue < theNode.getItem(i).dData)  
				//System.out.println("trying to get childNum:" + i);
				//no key in theNode is equal to theValue, because find() has checked
				return theNode.getChild(i);
		}
		return theNode.getChild(i);
	} //end getNextChild
	
	public void displayTree(){
		recDisplayTree(root,0,0);
	}
	
	public void recDisplayTree(Node thisNode, int level, int childNumber){
		
		System.out.println("level=" + level + " child=" + childNumber + " ");
		thisNode.displayNode();
		
		int numItems = thisNode.getNumItems();
		for (int i=0; i<numItems+1; i++ ){
			Node nextNode = thisNode.getChild(i);
			if (nextNode != null)
				recDisplayTree(nextNode,level+1,i);
			else
				return;
		}
	}//end recDisplayTree
}
