package basic;

public class TreeApp {
	public static void main(String[] args){
		Tree theTree = new Tree();
		
		theTree.insert(50, 1.5);
		theTree.insert(25, 1.7);
		theTree.insert(75, 1.9);
		theTree.insert(60, 1);
		theTree.insert(90, 1);
		theTree.insert(85, 1);
		theTree.insert(95, 1);
		theTree.insert(80, 1);
		theTree.insert(82, 1);
		
		Node found = theTree.find(50);
			System.out.println("root's rightchild before deletion:" 
		+ found.rightchild.iData);
		
		Node deleted = theTree.delete(75);
		System.out.println("root's rightchild after deletion:" 
				+ found.rightchild.iData);
	}
}
