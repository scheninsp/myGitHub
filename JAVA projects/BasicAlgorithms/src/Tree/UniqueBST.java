package Tree;

import java.util.Scanner;

public class UniqueBST {
	//http://practice.geeksforgeeks.org/problems/unique-bsts/0
	//find number of all possible structure of N integer formed binary search tree
	//Adapted from Internet, @author: priyanshi1996 
	public static void main (String[] args)
	 {
	 Scanner s=new Scanner(System.in);
	 int T=s.nextInt();
	 int[] A=new int[12];
	 A[0]=1;
	 A[1]=1;
	 for(int i=2;i<=11;i++)
	 {
	     for(int j=0;j<i;j++)
	     A[i]+=A[j]*A[i-j-1];   //1node tree * (n-1)node tree + 2node tree * (n-2)node tree +...
	 }
	 while(T>0)
	 {
	     int N=s.nextInt();
	     System.out.println(A[N]);
	     T--;
	 }
	 }
	
}


