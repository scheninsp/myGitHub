package temp;

import java.util.Scanner;
//Problem : 
// find all possible equations satisifying a^b = c^d for 1=< a,b,c,d <=n
// f(1)=1, f(2)=6, f(3)=15, f(4)=30 ...
// exp.n=2
// 1^1=1^1, 1^2=1^2, 1^2=2^1, 2^1=1^2, 2^1=2^1, 2^2=2^2

public class SolvePossibleEquations {
	
	static final long p = 1000000007;
	
	
	public static long fm(int k){
		//search k in list
		int result = 0;
		if(k>=16){
			result = 1;
		}
		return result*2;
	}
	
	public static long fn(int k){
		if (k==1){
			return 1;
		}
		long an = fn(k-1) + 2*fm(k) + 4*k-3;
		return an%p;
	}
	
	public static void main(String args[]){
		Scanner in = new Scanner(System.in);
		int n = in.nextInt();
		
		long an = fn(n);
		System.out.println(an);
	}
	
}
