package basic;

import java.util.Scanner;
//Problem: seq like 1,2,2,3,3,3,4,4,4,4..., give an
public class SeqkRep {
	
	public static int fn(long n){
		double kp = (1+Math.sqrt(n*8+1))/2;
		int k = (int)kp;
		return k;
	}
	
	public static void main(String args[]){
		Scanner in = new Scanner(System.in);
		long n = in.nextLong();
		
		int an = fn(n-1);
		System.out.println(an);
	}
	
}
