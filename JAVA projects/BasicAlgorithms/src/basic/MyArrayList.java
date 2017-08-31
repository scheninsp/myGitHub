package basic;

import java.util.ArrayList;
import java.util.List;

public class MyArrayList{
	
	private List<Integer> mylist= new ArrayList<Integer>();
		
	public int binarySearch(Integer val){   //binary search in an ordered sequence
		int pfront = 0;
		int pend = mylist.size();
		
		while (pfront<=pend){
			int pmid = (pfront+pend)/2;
			if (mylist.get(pmid) == val){
				return pmid;
			}
			else if (mylist.get(pmid) < val){
				pfront = pmid+1;
			}
			else {
				pend = pmid-1;
			}
		}
		return -1;
			
	}
	
	public void quickSort(int plow, int phigh) {  //recursively sorting binary divisions
		//result from small to big ones
		System.out.println("range:("+ plow +","+ phigh + ")"); //output current sorting index range

		int mid = getMid(plow,phigh);
		if (plow < phigh){
			quickSort(plow,mid-1);
			quickSort(mid+1,phigh); 
		}
	}
	
	public int getMid(int plow,int phigh) {   //sort one sequence, return its pivot's position
		int pivot = mylist.get(plow);
		while (plow<phigh){
			while (plow<phigh && mylist.get(phigh) > pivot){
				phigh--;
			}
			while(plow<phigh && mylist.get(plow) < pivot){
				plow++;
			}
			//mylist.set(plow,mylist.get(phigh));
			//mylist.set(phigh,pivot);
			swap(plow,phigh);
			System.out.println("running: "+mylist);
		}
		return plow;  //end : plow = phigh
	}
	
	
	public void shellSort(){   //implement shellSort, a special form of insert sort
		//result from small to big ones
		int inner,outer;
		int temp;
		
		int h=1;
		while(h<=mylist.size()/3){
			h=3*h+1;
		}
		
		while (h>0){
			
			for (outer=h; outer<mylist.size(); outer++){
				inner = outer;
				temp = mylist.get(outer);

				while(inner-h>=0 && mylist.get(inner-h)>=temp){
					mylist.set(inner, mylist.get(inner-h));	//shift right
					inner = inner-h;
				}

				mylist.set(inner, temp);
			} //end for
			
			System.out.println( h + " - sorted:" + mylist);	//output h-increament sorted result
			h=(h-1)/3;
		}//end while(h>0)
	}
	
	
	public void minHeap(int st, int heapSize){
		int left = st*2;
		int right = st*2+1;
		int largest = st;
		
		if(left<=heapSize && mylist.get(left) < mylist.get(largest)){
			largest = left;
		}
		
		if(right<=heapSize && mylist.get(right) < mylist.get(largest)){
			largest = right;
		}
		
		if(largest != st){  //heap has changed
			swap(largest,st);
			minHeap(largest,heapSize);
		}
	}
	
	public void buildMinHeap(){
		for(int i = (mylist.size()-1)/2; i>=0; i--){
			minHeap(i,mylist.size()-1);
		}
	}
	
	public void heapSort(){
		//using minHeap output from big to small
		//using maxHeap output from small to big
		buildMinHeap();
		for(int i=0;i<mylist.size();i++){ 
			swap(0,mylist.size()-i-1);
			System.out.println("ORDERED:" + mylist);
			
			minHeap(0,mylist.size()-i-2);  //build from ith element
		}
	}
	
	public void swap(int p1, int p2){  
		int temp = mylist.get(p1);
		mylist.set(p1,mylist.get(p2));
		mylist.set(p2,temp);
	}
	
	public static void main(String[] args){
		MyArrayList myArrayList = new MyArrayList();
		
		Integer[] temp = {5,7,3,4,2,6,8,9,1};
		for(int i=0;i<temp.length;i++){
			myArrayList.mylist.add(temp[i]);
		}
		System.out.println(myArrayList.mylist);
		
		//myArrayList.quickSort(0,myArrayList.mylist.size()-1);  //quicksort the whole sequence
		
		//myArrayList.shellSort();
		
		myArrayList.heapSort();
		
		System.out.println("ORDERED:" + myArrayList.mylist);
		
	}
	
}
