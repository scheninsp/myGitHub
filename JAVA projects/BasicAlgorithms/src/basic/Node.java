package basic;

public class Node {
	
	int iData;  //key value
	double fData;
	Node rightchild;
	Node leftchild;
	
	public void displayNode(){
		System.out.println("node key:" + iData);
		System.out.println("node val:" + fData);
	}
}
