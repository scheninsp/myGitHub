package basic;

public class testSwap {
	//test implement the C++ swap()
	public int[] data;
	
	public void swap(int[] data, int a, int b){
		int t = data[a];
		data[a] = data[b];
		data[b] = t;
	}
	
	public testSwap(int[] mydata){
		data = mydata;
	}
	
	public void display(){
		for(int i=0; i<data.length; i++){
			System.out.print(data[i]+" ");
		}
	}
	
	public static void main(String args[]){
		
		int[] mydata = {1,2,3,4};
		testSwap anobj = new testSwap(mydata);
		
		anobj.swap(mydata,0,2);
		anobj.display();
	}
}
