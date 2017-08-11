package advanced;

public class NQueen {
//solve the N-Queen problem
	int N;
	int[][] board;
	
	public NQueen(int n){
		N = n;
		board = new int[n][n];
	}
	
	private boolean isSafe(int row,int col){
		//queen is placed from left col to right col
		//so only check the cols to the left of [j]
		int i;
		//upleft
		for(i=1; row-i>=0 && col-i>=0; i++)
			if(board[row-i][col-i] == 1)
				return false;
		//left
		for(i=0;i<col;i++)
			if(board[row][i] == 1)
				return false;
		//downleft
		for(i=1; row+i<N && col-i>=0; i++)
			if(board[row+i][col-i] == 1)
				return false;
		
		return true;
	}//end isSafe
	
	public boolean solveNQueen(int col){
		//this function decides the position of queen to put on column 'col'
		if(col == N)
			return true;
		
		//try from the first row
		for(int i=0;i<N;i++){
			if(isSafe(i,col)){
				board[i][col] = 1; 
				if (solveNQueen(col+1) == true){
					return true;
				}
				else //BACKTRACK  -- IMPORTANT
				{
					board[i][col]=0;
				}
			}
		}//end for
		
		return false; //all rows in this column failed 
		
	}//end solveNQueen
	
	public void printSolution(){
		for(int i=0;i<N;i++){
			for(int j=0;j<N;j++){
				System.out.print(board[i][j]+" ");
			}
			System.out.print("\n");
		}
	}
	
	public static void main(String args[]){
		
		int N = 4;
		NQueen nQueenPrb = new NQueen(N);
		nQueenPrb.solveNQueen(0);
		nQueenPrb.printSolution();
	}
	
}
