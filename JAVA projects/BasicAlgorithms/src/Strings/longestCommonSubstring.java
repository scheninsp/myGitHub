package Strings;

public class longestCommonSubstring {
//This class solves the longest common substring problem
	
	private char[] x,y;
	
	public longestCommonSubstring(char[] a, char[] b){
		this.x=a;
		this.y=b;
	}
	
	
	public char[] solveLCS(){
		int[][] lenLCSuff = new int[x.length+1][y.length+1];    //save the length of longest suffix of x[1~i],y[1~j]
		int lenLCSubstring = 0;   //the length of longest substring 
		int endLCSx,endLCSy = 0;  //the ending index in string x and y
		
		//initialize
		for(int j=1;j<y.length+1;j++)
			lenLCSuff[0][j] = 0;
		for(int i=1;i<x.length+1;i++)
			lenLCSuff[i][0] = 0;
		
		//calculate all longest suffix
		for (int i=1;i<x.length+1;i++){
			for (int j=1;j<y.length+1;j++){
				if(x[i-1] == y[j-1]){
					lenLCSuff[i][j] = lenLCSuff[i-1][j-1]+1;
				}
				else{
					lenLCSuff[i][j] = 0;
				}
			}
		}
		
		//find maximum in lenLCSuff[][]
		int temp=0, maxRow=0 , maxCol=0;   //maximum position index
		for (int i=1;i<x.length+1;i++){
			temp = lenLCSuff[i][0];
			for(int j=1;j<y.length;j++){
				if (lenLCSuff[i][j]>temp){
					temp = lenLCSuff[i][j];
					maxCol = j;
				}
			}
			if (temp > lenLCSubstring){
				lenLCSubstring = temp;
				maxRow = i;
			}
		}
		
		endLCSx=maxRow-1;
		endLCSy=maxCol-1;
		
		//TraceBack from maxRow,maxCol to get the common string
		char[] theLCSxy = new char[lenLCSubstring];      //the result string 
		for (int i=0; i<lenLCSubstring; i++){
			theLCSxy[lenLCSubstring-1-i] = x[endLCSx-i];
		}
		
		return theLCSxy;

	}
	
	public static void main(String args[]){
		
		String temp1 = "abcdefg";
		char[] str1 = temp1.toCharArray(); 
		String temp2 = "sewdefnabkfg";
		char[] str2 = temp2.toCharArray();
		longestCommonSubstring testLCS = new longestCommonSubstring(str1, str2);
		
		char[] strcomm = testLCS.solveLCS();
		System.out.println("LCS: " + String.valueOf(strcomm));
	}
	
}
