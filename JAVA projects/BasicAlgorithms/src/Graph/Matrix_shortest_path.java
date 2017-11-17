package Graph;

import java.util.Scanner;

public class Matrix_shortest_path {
	//http://practice.geeksforgeeks.org/problems/shortest-source-to-destination-path/0
	//find shortest path in a given [0,1] matrix, where 0 stands for no-route, 1 stands for route
	
	public static void main (String args[]){
				
		Scanner sc = new Scanner(System.in);
		int T = sc.nextInt();

		for (int iC=0; iC<T; iC++){
			
			// input stage
			int m = sc.nextInt();
			int n = sc.nextInt();
			
			int[][] G = new int[m][n];
			int[][] S = new int[m][n];
			boolean[][] Visited  = new boolean[m][n];
			int d_max = m*n;
			
			//input graph
			for (int j=0;j<m;j++){
				for (int k=0;k<n;k++){
					G[j][k] = sc.nextInt();
					//System.out.print(G[j][k]);
				}
			}
			//System.out.println("G[4][4]:" + G[4][4]);
			
			//input destination
			int[] dest = new int[2];
			for (int j=0;j<2;j++){
				dest[j] = sc.nextInt();
			}
			
			if(G[0][0] == 0) {System.out.println(-1); continue;}  //special case

			
			//initialization
			int nD_all=4;
			int[][] deltas = new int[nD_all][2];
			deltas[0][0] = 1;
			deltas[0][1] = 0;
			deltas[1][0] = 0;
			deltas[1][1] = 1;
			deltas[2][0] = -1;
			deltas[2][1] = 0;
			deltas[3][0] = 0;
			deltas[3][1] = -1;
			
			//initialize S to max path length
			for (int j=0;j<m;j++){
				for (int k=0;k<n;k++){
					S[j][k] = d_max;
				}
			}
			S[0][0] = 0;
			
			//initialize queue F for processing Nodes
			Queue F = new Queue();
			//push 0,0 into F
			int x=0; int y=0;
			F.push_with_no_replicant(x,y);
			
			//calculation
			int p,q;
			while(!F.isempty()){
				
				//pop top from F
				Node temp = F.pop();
				int i = temp.pair[0];
				int j = temp.pair[1];
				//System.out.println("("+i+","+j+")"); //debug
				//System.out.println("----("+i+","+j+"): "+ G[i][j]+"----"); //debug
	
				//search neighbors
				for ( int nD=0; nD<nD_all; nD++ ){  //moving directions
					p=i+deltas[nD][0]; q=j+deltas[nD][1];
					if(p>m-1 || q>n-1 || p<0 || q<0 || G[p][q]==0 || Visited[p][q]==true ){continue;}
					else{ 
						S[p][q] = S[p][q]<=(S[i][j]+1)? S[p][q]: (S[i][j]+1) ;
						//System.out.println("("+p+","+q+"): "+ S[p][q]); //debug

						F.push_with_no_replicant(p,q); 
						}
				}

				Visited[i][j] = true;  //this point has searched 
			}//calculation end
			
			for(int i=0;i<m;i++){
				for(int j=0;j<n;j++){
					if (S[i][j] == d_max){
						S[i][j] = -1;   //unreachable
					}
				}
			}
			System.out.println(S[dest[0]][dest[1]]);

		}//end Case
		
		sc.close();
	}//end main
	
	

	
	
	static class Queue{
		public Node head,tail;
		public int len;
		
		Queue(){
			head=null;
			tail=null;
			len=0;
		}
		
		public void push_with_no_replicant(int x,int y){
			Node p = head;
			while(p!=null){
				if ((p.pair[0] == x) && (p.pair[1] == y)) {return;}  //not push if it is already in
				p = p.next;
			}
			
			if (isempty()){
				head = new Node(x,y,null);
				tail = head;
				len++;
			}
			else{
				tail.next = new Node(x,y,null);
				tail = tail.next;
				len++;
			}
		}
		
		public Node pop(){
			if(!isempty()){
				Node tempNode = head;
				head = head.next;
				len--;
				return tempNode;
			}
			else{return null;}
		}
		
		public boolean isempty(){
			return len==0;
		}
	}
	
	static class Node{
		public int[] pair = new int[2];
		public Node next;
		
		Node(int x, int y, Node pointer){
			pair[0]=x;
			pair[1]=y;
			next = pointer;
		}
	}
	
}




