package Basic;

public class TestInnerClass {
	public static void main (String args[]){
		Inner1 testInner= new Inner1();
		int x=1; int y=1;
		testInner.push(x, y);
		System.out.print("("+testInner.node.pair[0]+","+testInner.node.pair[1]+")");
	}
	
	static class Inner1{
		Node node;
		int gg;
		
		Inner1(){
			node = null;
			gg=1;
		}
		
		public void push(int x, int y){
			node = new Node(x,y);
			gg=99;
		}
		

	}
	
	static class Node{
		int[] pair = new int[2];   //STATIC INNER CLASS MUST BE FIRST INITIALIZED THEN GIVE VALUE
		
		public Node(int x, int y){
			pair[0] = x;
			pair[1] = y;
		}
		
	}
	
	//WRONG CODE : 
//	static class Node{
//		int[] pair;   
//		
//		public Node(int x, int y){
//			int[] pair = new int[2];   //THIS IS USELESS !
//			pair[0] = x;
//			pair[1] = y;
//		}
//		
//	}
}






