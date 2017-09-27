package basic;

import java.util.LinkedList;
import java.util.Scanner;

public class KthDoublePrime {
//定义双素数是那些自身是素数，十进制形式取反与自身不同且仍是素数的值，求第k个双素数
	
	private static final int MAX= 1000000;
	
	public static int revertDec(int x){
	//revert a Decimal number
		int xr = 0;
		while(true){
			int n = x%10;
			xr = xr*10 + n;
			x = x/10;
			if(x == 0) break;
		}
		return xr;
	}
	
	public static LinkedList genPrime(int n){
		LinkedList<Integer> primes = new LinkedList();
		//generate all primes less than n 
		for(int i= 3; i<n; i+=2){  //omit 1, 2
			boolean bool = true;

			for(int j=3; j<=Math.sqrt(i);j++){
				if(i % j == 0){
					bool = false;
					break;
				}
			}
			if (bool)
			primes.add(i);
		}
		return primes;
	}
	
	public static boolean isPrime(int n){
		//judge n is a prime 
		boolean bool = true;
		for(int j=2; j<=Math.sqrt(n);j++){
			if(n % j == 0){
				bool = false;
				break;
			}
		}
		return bool;
	}
	
	public static int genDoubleNum(int k){
		LinkedList<Integer> primes = genPrime(MAX);
		int cntD=0;
		int cntP=0;
		int kthDoubleNum = -1;
		while(cntD<k){
			cntP++;
			int temp = primes.get(cntP);
			int tempR = revertDec(temp);
			if( (tempR != temp) && (isPrime(tempR)) ){
				kthDoubleNum = temp;
				cntD++;
			}
		}
		return kthDoubleNum;
	}
	
	public static void main(String args[]){
		Scanner sc = new Scanner(System.in);
		int k = sc.nextInt();
		
		//LinkedList<Integer> primes = genPrime(k);
		//System.out.println(primes);
		
		System.out.println(genDoubleNum(k));
	}
}
