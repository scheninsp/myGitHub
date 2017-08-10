package Graph; //directed without-weight graph with adjacent matrix implementation

public class Graph implements Cloneable { //directed without-weight graph
	
	private final int MAX_VERTS = 20;
	private Vertex vertexList[];
	private int adjMat[][];
	private int nVerts;
	private char sortedArray[];
	
	public Graph(){
		vertexList = new Vertex[MAX_VERTS];
		adjMat = new int[MAX_VERTS][MAX_VERTS];  //array must be allocated
		
		nVerts = 0;
		for (int i=0;i<MAX_VERTS;i++){
			for (int j=0;j<MAX_VERTS;j++){
				adjMat[i][j] = 0;
			}
		}
		sortedArray = new char[MAX_VERTS]; 
	}
	
	public void addVertex(char lab){
		Vertex v = new Vertex(lab);
		vertexList[nVerts] = v;
		nVerts++;
	}
	
	public void deleteVertex(int delVertex){
		if(delVertex < nVerts){
			for(int i=delVertex;i<nVerts;i++){
				vertexList[i] = vertexList[i+1];
			}
			
			for(int i=delVertex;i<nVerts;i++){
				adjMat[i] = adjMat[i+1];  //delete Row
				}
			for(int i=0;i<nVerts;i++){
				for(int j=delVertex;j<nVerts;j++){
					adjMat[i][j] = adjMat[i][j+1];  //delete Column
				}
			}

			nVerts--;
		}
	}//end deleteVertex
	
	public void addEdge(int start, int end){
		adjMat[start][end]=1;
	}
	
	public void displayVertexList(){  //display all vertices
		for(int i=0;i<nVerts;i++){
			System.out.print(vertexList[i].label);
		}
		System.out.print("\n");
	}
	
	public void displayAdjMat(){
		for(int i=0;i<nVerts;i++){
			for(int j=0;j<nVerts;j++){
			System.out.print(adjMat[i][j]+" ");
			}
			System.out.print("\n");
		}
	}
	
	public int noSuccessor(){ //return a vertex with no successor
		int temp;
		for (int i=0;i<nVerts;i++){
			temp=0;
			for (int j=0;j<nVerts;j++){
				temp+=adjMat[i][j];
			}
			if(temp == 0){
				return i;
			}
		}
		return -1; //no vertex 
	}//end noSucessor
	
	public void topo(){  //topology sorting of graph
		int orig_nVerts = nVerts;
		
		while(nVerts > 0){ 
			int curVertex = noSuccessor();
			
			if(curVertex == -1){ //has cycle
				System.out.println("ERROR: Graph has cycle");
				return;
			}
			
			sortedArray[nVerts-1] = vertexList[curVertex].label;
			
			deleteVertex(curVertex);
		}
		
		//output sortedArray
		System.out.print("Topologically sorted order: \n");
		for(int i=0;i<orig_nVerts;i++){
			System.out.print(sortedArray[i]+" ");
		}
		System.out.print("\n");
	}//end topo
	
	@Override   
	public Object clone(){  // Cloneable
		Graph g = null;
		try{
			g = (Graph)super.clone();
		}catch(CloneNotSupportedException e){
			e.printStackTrace();
		}
		
		for (int i=0;i<nVerts;i++){
			g.vertexList[i] = (Vertex)vertexList[i].clone();
		}
		return g;
	}//end clone()	
}
