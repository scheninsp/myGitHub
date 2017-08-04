package basic;
//01 Backpack problem solution
//reference to http://www.cnblogs.com/liuzhen1995/p/6374541.html

public class Backpack01Solution {

	public int[][] finalVal;		//final maximum value table 
	public int[] carryWhich;   //solution vector of 01 backpack problem
	
	public Backpack01Solution(int[] weight){
		int sumWeight = 0;
		for (int i=0;i<weight.length;i++){
			sumWeight += weight[i];
		}
		finalVal = new int[weight.length][sumWeight+1];  //for back tracking, save all results
		carryWhich = new int[weight.length];  //only one best solution
	}
	
	public void backpack01Solve(int[] weight, int[] value, int maxCapacity){
		int temp;
		for (int i=1;i<weight.length;i++){
			for (int j=1;j<=maxCapacity;j++){
				if(j-weight[i]>=0){
					temp = finalVal[i-1][j-weight[i]]+value[i];
					if( temp > finalVal[i-1][j]){
						finalVal[i][j] = temp;
						carryWhich[i] = 1;  
					}
					else{
						finalVal[i][j] = finalVal[i-1][j];
					}
				}
				else{ //j-weight[i] <0
					finalVal[i][j] = finalVal[i-1][j];
				}
			}//end j
		}//end i
	
	}//end backpack01Solve()
	
	public static void main(String args[]){
		//weight and value must add 0 in head , for algorithm will omit weight[0]
		//(because cannot calculate weight[-1])
		int[] weight = {0,2,1,3,2};    
		int[] value = {0,12,10,20,15};
		int maxCapacity = 7;
		
		Backpack01Solution mypack = new Backpack01Solution(weight);
		mypack.backpack01Solve(weight,value,maxCapacity);
		
		for(int i=0;i<mypack.finalVal.length;i++){
			for (int j=0;j<mypack.finalVal[0].length;j++){
				System.out.print(mypack.finalVal[i][j] + " ");
			}
			System.out.print("\n");
		}
		System.out.println("the maximum value is " + mypack.finalVal[weight.length-1][maxCapacity]);
		
		System.out.print("the solution vector is : " );
		for(int i=0;i<mypack.carryWhich.length;i++){
			System.out.print(mypack.carryWhich[i]);
		}
	}//end main
	
}//end class
