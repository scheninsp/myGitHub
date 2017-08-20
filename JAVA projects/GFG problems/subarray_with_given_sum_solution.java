import java.util.*;
import java.lang.*;
import java.io.*;

class GFG {
    private final static int MAX_CAP = 100;
    private static int[] a = new int[MAX_CAP];
    
    private static void solveSum(int[] array, int arraySize, int sum){
        	//calculate sum
		    for(int i=0; i<arraySize; i++){ //start from
		        int tempsum = 0;
		        int j=0;
		        while(tempsum < sum){
		            tempsum = tempsum + array[i+j];
		            if(tempsum == sum){
		                System.out.println((i+1) + " " + (i+j+1)); 
		                //CAUTION : the index required starts from 1
		                return;
		            }
		            else if(tempsum > sum) {
		                continue;
		            }
		            j++;   //DON'T FORGET THIS
		        }
		    }//end for
		    
		    //not found
		    System.out.println("-1");
		    return;
    }// end solveSum
    
    //REMEMBER THE DEFINITIONS
	public static void main (String[] args) {
		Scanner sc = new Scanner(System.in);
		int nCase = sc.nextInt();
		while(nCase>0){
		    //input
		    int arraySize = sc.nextInt();
		    int sum = sc.nextInt();
		    for(int i=0; i<arraySize; i++){
		        a[i] = sc.nextInt();
		    }
            //solve
		    solveSum(a, arraySize, sum);
		    nCase--;
		}
	}// end main
}
