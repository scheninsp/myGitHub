package advanced;

import java.util.Scanner;

public class ArraySeparate3 {
	//separate a cycle arrayList into 3 parts with equal sum
	// elements range from -1000000 to 1000000
	//if not separable , return -99999999, else return the maximum sum
	
	//input
//	4
//	9
//	1 2 3 4 0 2 -1 5 2
//	9
//	3 4 0 2 -1 5 2 1 2
//	7
//	8 2 -1 6 -10 17 -1
//	5
//	2 -1 2 1 1

	
	//output
//	6
//	6
//	7
//	-99999999
	
	public static void main(String[] args){
		
		//input
		Scanner sc = new Scanner(System.in);
		int T;
		T = sc.nextInt();
		int[] result = new int[T];
		for(int i=0; i<result.length; i++){ result[i] = -99999999;}  //initialize result as minimum 
		
		for(int tcase=0; tcase<T; tcase++){
			int N = sc.nextInt();
			int[] data = new int[N];
			for (int i=0; i<N; i++){
				data[i] = sc.nextInt();
			}
			
			
			for(int head=0; head<N; head++){
				//System.out.println("head:" + head);
				int tail=circle_mod(head+N-1,N);
				
				//build a ordered list
				int[] sumLeft = new int[N];
				sumLeft[0]=data[head];
				//System.out.print("sumLeft: " + sumLeft[0]+" ");
				for (int i=1;i<N;i++){
					sumLeft[i] = sumLeft[i-1] + data[circle_mod(head+i,N)];
					//System.out.print(sumLeft[i]+" ");
				}
				//System.out.print("\n");
				
				int[][] sumRight = new int[2][N];  //sumRight[0]:summation, sumRight[1]:index
				sumRight[0][0]=data[tail];
				sumRight[1][0]=tail;
				//System.out.print("sumRight: " + sumRight[0][0]+" ");
				for (int i=1;i<N;i++){
					int index = circle_mod(tail-i,N);
					sumRight[0][i] = sumRight[0][i-1] + data[index];
					sumRight[1][i] = index;
					//System.out.print(sumRight[0][i]+" ");
				}
   			    //System.out.print("\n");
				
				sumRight = quick_sort_ndarray(sumRight);
				//System.out.print("sumRight sorted: ");
				for(int i=0;i<N;i++){
					//System.out.print(sumRight[0][i]+"-"+sumRight[1][i]+" ");
					//System.out.print(sumRight[0][i]+" ");
				}
				//System.out.print("\n");
				
				int total = sumLeft[N-1];
				int nPart = 3;   //part number to divide array
				if( total%nPart != 0) {result[tcase] = -1; break;}  //cannot separated into equal parts
				int partMax = total/nPart;
				
				for(int indexL=0; indexL<N; indexL++){
					//System.out.println("Left:"+indexL);
					if(sumLeft[indexL]<=partMax){
						
						int indexR=binary_search(sumRight[0], sumLeft[indexL]); 
						//0 can be separated to either part, so it is not necessary to search precise 0-element position
						
						if (indexR != -1){
							indexR = sumRight[1][indexR];  //return to original index
							//indexL is shift to head, but indexR is absolute index
							//System.out.println("matched Right:"+indexR);
							int sumMid = 0;
							int nMid,next;
							int realIndexL = circle_mod(indexL+head,N);
							nMid = circle_mod(indexR-1-realIndexL,N);
							for(int i=1;i<=nMid;i++){
								next=circle_mod(realIndexL+i,N);
								sumMid = sumMid + data[next];
							}
							if(sumMid == sumLeft[indexL]) {
								if(result[tcase] < sumMid){
									result[tcase] = sumMid;
								}
							}
						}
					}
				}
				
			}//end this head 	
			
		}//end case
		
		for(int i=0;i<T;i++){
			System.out.println(result[i]);
		}
		sc.close();
	}//end main
	
	public static int binary_search(int[] array,int key){
		int left=0; 
		int right=array.length-1;
		while(right>=left){
			int mid = (left+right)/2;
			if(array[mid]> key){
				right = mid-1;
			}
			else if(array[mid] < key){
				left = mid+1;
			}
			else{
				return mid;
			}
		}
		return -1;
	}
	
	public static int[][] quick_sort_ndarray(int[][] array){
		// array[0]:values to sort, array[1] ... [nd], other data

		Stack stack = new Stack();
		stack.push(new Node(0,array[0].length-1));

		while(!stack.isempty()){
			
			int[] poped = stack.pop().val;
			int left = poped[0];
			int right = poped[1];
			if (left>=right) {continue;}
			
			int ip = (left+right)/2;		
			int pivot = array[0][ip];
			
			int pRight = right; 
			int pLeft = left;
			
			while(pRight>pLeft){
				int tempindx;
				
				while(array[0][pRight] >= pivot && pRight>ip){
					pRight--;
				}
				if(pRight>pLeft){
				swap(array,ip,pRight);
				ip = pRight;
				//System.out.print("sumRight sorted-mid: "); //debug
				//print_array_1d(array[0]);
				}
				
				while(array[0][pLeft] <= pivot && pLeft<ip){
					pLeft++;
				}
				if(pRight>pLeft){
				swap(array,ip,pLeft);
				ip = pLeft;
				}
	
			}
			
			stack.push(new Node(left,pLeft-1));
			stack.push(new Node(pLeft+1,right));	
		}
		return array;
	}

	public static int[][] swap(int[][] array, int p1, int p2){
		int temp;
		for (int i=0; i<array.length; i++){
			temp = array[i][p2];
			array[i][p2] = array[i][p1];
			array[i][p1] = temp;
		}
		return array;
	}
	
	public static int[] swap(int[] array, int p1, int p2){
		int temp = array[p2];
		array[p2] = array[p1];
		array[p1] = temp;
		return array;
	}
	
	public static int circle_mod(int x,int y){
		// mod(x,y)
		while(x<0){ x+= y; }
		return x%y;
	}
	
	public static void print_array_1d(int[] array){
		for(int i=0;i<array.length;i++){
			System.out.print(array[i]+" ");
		}
		System.out.print("\n");
	}
	
	
	public static class Stack{
		int len;
		Node top;
		
		Stack(){
			top = new Node();
			len = 0;
		}
		
		public boolean isempty(){
			return len==0;
		}
		
		public void push(Node node){
			node.next = top;
			top = node;
			len++;
		}
		
		public Node pop(){
			if(!this.isempty()){
				Node temp = top;
				top = top.next;
				len--;
				return temp;
			}
			else{
				return null;
			}
		}
	}
	
	public static class Node{
		int[] val;
		Node next;
		Node(){
			val = new int[2];
			next = null;
		}
		
		Node(int input1, int input2){
			val = new int[2];
			val[0] = input1; val[1]=input2;
			next = null;
		}
		
		Node(int[] input, Node nextNode){
			val = input;
			next = nextNode;
		}
	}

	
}
