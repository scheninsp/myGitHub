package basic;

public class testSwap2 {
	// the Implement of C++ swap without using a wrapper class	
	public static void swap(int[] data, int a, int b){
		int t = data[a];
		data[a] = data[b];
		data[b] = t;
	}
	
	public static void display(int[] data){
		for(int i=0; i<data.length; i++){
			System.out.print(data[i]+" ");
		}
	}
	
	public static void main(String args[]){
		
		int[] mydata = {1,2,3,4};
		
		testSwap2.swap(mydata,0,2);
		testSwap2.display(mydata);
	}
}
