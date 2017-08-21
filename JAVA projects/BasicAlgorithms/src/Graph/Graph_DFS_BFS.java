package Graph;

import java.util.LinkedList;
import java.util.ListIterator;

public class Graph_DFS_BFS {
	//adapted from
	//http://www.geeksforgeeks.org/depth-first-traversal-for-a-graph/
	
    private int V;   // No. of vertices
    
    // Array  of lists for Adjacency List Representation
    private LinkedList<Integer> adj[];
 
    // Constructor
    public Graph_DFS_BFS(int v)
    {
        V = v;
        adj = new LinkedList[v];
        for (int i=0; i<v; ++i)
            adj[i] = new LinkedList();
    }
 
    //Function to add an edge into the graph
    void addEdge(int v, int w)
    {
        adj[v].add(w);  // Add w to v's list, indicate an edge 'v->w'
    }
 
	public void DFS(int v){
		boolean[] visited = new boolean[V];
		DFSUtil(v,visited);
		System.out.print("\n");
	}
    
	//for graph has loops, visited is necessary
	public void DFSUtil(int v, boolean[] visited){
		if (visited[v] == false){
			visited[v] = true;
			System.out.print(v+" ");
			
			ListIterator<Integer> iter = adj[v].listIterator();
			while (iter.hasNext()){
				int nextNode = iter.next();
				DFSUtil(nextNode,visited);
			}
		}
	}
	
	public void myBFS(int v){    //BAD IMPLEMENT, SCAN TWICE
		boolean[] visited = new boolean[V];
		System.out.print(v+" ");
		myBFSUtil(v,visited);
		System.out.print("\n");
	}
    
	public void myBFSUtil(int v, boolean[] visited){   //BAD IMPLEMENT, SCAN TWICE
		int nextNode;
		
		visited[v] = true;
		ListIterator<Integer> iter = adj[v].listIterator();
		while(iter.hasNext()){
			nextNode = iter.next();
			if (visited[nextNode] == false){
			System.out.print(nextNode+" ");
			}
		}

		iter = adj[v].listIterator();  //reset iterator
		while(iter.hasNext()){
			nextNode = iter.next();
			if (visited[nextNode] == false){
				myBFSUtil(nextNode,visited);
			}
		}
	}
	
	public void BFS(int v){   
		//using queue to implement BFS
		boolean[] visited = new boolean[V];
		
		int cur;
		LinkedList<Integer> queue = new LinkedList<Integer>();
		queue.add(v);
		
		while(!queue.isEmpty()){
			cur = queue.poll();
			visited[cur] = true;
			System.out.print(cur + " ");
			
			ListIterator<Integer> iter = adj[cur].listIterator();
			while(iter.hasNext()){
				int nextNode = iter.next();
				if(visited[nextNode] == false){
					queue.add(nextNode);
				}
			}
		}
		System.out.print("\n");
	}

	
    public static void main(String args[])
    {
    	Graph_DFS_BFS g = new Graph_DFS_BFS(4);
 
        g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(1, 2);
        g.addEdge(2, 0);
        g.addEdge(2, 3);
        g.addEdge(3, 3);
 
        System.out.println("Following is Depth First Traversal "+
                           "(starting from vertex 2)");

        g.DFS(2);
        //g.myBFS(2);
        g.BFS(2);
    }
}
