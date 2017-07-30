package Tree234;

public class Node {
	
	private static final int ORDER = 4;
	private int numItems;
	private DataItem itemArray[] = new DataItem[ORDER-1];
	private Node parent;   //defined 
	private Node childArray[] = new Node[ORDER];
	
	public void connectChild(int childNum, Node child){
			//numItems++;   why not?
			childArray[childNum] = child;
			if (child != null){   //under which case child is null?
				child.parent = this;
			}
	}
	public Node disconnectChild(int childNum){
		Node tempNode = childArray[childNum];
		childArray[childNum] = null;
		return tempNode;
	}
	
	public Node getChild(int index){
		return childArray[index];
	}
	
	public Node getParent(){
		return parent;
	}
	public boolean isLeaf(){
		return childArray[0]==null ? true:false;
	}
	public int getNumItems(){
		return numItems;
	}
	public DataItem getItem(int index){
			return itemArray[index];
	}
	
	public boolean isFull(){
		return numItems == ORDER-1 ? true:false;
	}
	public int findItem(long key){
		for(int i=0; i<numItems; i++){
			if(itemArray[i].dData == key){
				return i;
			}
		}
		return -1; //not found
	}
	
	public int insertItem(DataItem dataItem) {
		//assume the node is not full
		for(int i=ORDER-2; i>=0; i--){
			if (itemArray[i] == null){
				continue;
			}
			else if(itemArray[i].dData > dataItem.dData){
				itemArray[i+1] = itemArray[i];
			}
			else{
				itemArray[i+1] = dataItem;
				numItems++;
				return i+1;
			}
		}
		itemArray[0] = dataItem;
		numItems++;
		return 0;
	}//end insertItem()
	
	public DataItem removeItem(){  //remove largest item
		DataItem temp = itemArray[numItems-1];
		itemArray[numItems-1] = null;
		numItems--;
		return temp;
	}
	
	public void displayNode(){
		for(int i=0;i<numItems;i++){
			itemArray[i].displayItem();
			System.out.print("/");
		}
		System.out.print("\n");
	}
	
}
