
import java.util.*;
import java.lang.*;
import java.io.*;

class GFG {
    // possible number for each character
    //private final int INF = 999;
    //private final int NA = INF; 
    private final static int NB = 1;
    private final static int NC = 2;
    
    private static int factorial(int a){
        int tmp=1;
        for(int i=1;i<=a;i++){
            tmp*=i;
        }
        return tmp;
    }
    
    private static int nchoosek(int n, int k){
        return factorial(n)/(factorial(n-k) * factorial(k));
    }
    
	public static void main (String[] args) {
		Scanner sc = new Scanner(System.in);
		int nCase = sc.nextInt();
		while (nCase > 0){
		    int nChar = sc.nextInt();
		    int nStr = 0;
		    for (int i=0;i<=NB;i++){
		        for (int j=0;j<=NC;j++){
		            nStr += nchoosek(nChar,i)*nchoosek(nChar-i,j);
		        }
		    }//end for i
		    System.out.println(nStr);
		    nCase--;
	    }// end while
	}// end main
}
