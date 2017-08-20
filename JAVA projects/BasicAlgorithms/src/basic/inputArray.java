package basic;

import java.util.Scanner;

public class inputArray {
	
	private int[] numArray;

	public void in(){
		Scanner sc=new Scanner(System.in);
		System.out.println("input integers, seprate by ',' ");
		
		String inputString=sc.next().toString();
		String stringArray[]=inputString.split(",");
		
		int num[]=new int[stringArray.length];
		for(int i=0;i<stringArray.length;i++){
			num[i]=Integer.parseInt(stringArray[i]);
			}		
		numArray = num;		
	}
	
	public void out(){
		for(int i=0;i<numArray.length;i++)
		System.out.print(numArray[i]+" ");
	}
	
	public int[] getNumArray() {
		return numArray;
	}
	
	public static void main(String args[]){
		inputArray myArray = new inputArray();
		myArray.in();
		myArray.out();
	}


}
