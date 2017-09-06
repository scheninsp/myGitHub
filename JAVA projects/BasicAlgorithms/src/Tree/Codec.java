package Tree;

import java.util.LinkedList;
import java.util.ListIterator;

public class Codec {
	//Build series from tree by broad first pre-order traversal
	//see leetcode problem: https://leetcode.com/problems/serialize-and-deserialize-binary-tree/description/
	
    // Encodes a tree to a single string.
    public String serialize(TreeNode root) {
    	LinkedList<TreeNode> queue = new LinkedList<TreeNode>();
    	StringBuffer result = new StringBuffer();   //CANNOT USE 'STRING' CLASS, CONCAT IS USELESS 
    	queue.add(root);
    	
    	//pre-order traverse tree
    	while(!queue.isEmpty()){
    		TreeNode cur = (TreeNode)queue.removeFirst();
    		if (cur==null)
    			result.append("null,");
    		
    		else{
    			result.append(cur.val + ",");
    			
    			if(cur.left!=null)
    				queue.addLast(cur.left);
    			else
    				queue.addLast(null);

    			if(cur.right!=null)
    				queue.addLast(cur.right);
    			else
    				queue.addLast(null);
    		}
    	}
    	
    	return result.toString();
    }

    // Decodes your encoded data to tree.
    public TreeNode deserialize(String data) {
        String[] dest = data.split(",");
        
        if(dest[0].equals("null"))
        	{return null;}   // root is null
        TreeNode root = new TreeNode(str2int(dest[0]));
        
        LinkedList<Integer> nNode = new LinkedList<Integer>();  //number of Nodes in ith level 
        LinkedList<Integer> nNodeSum = new LinkedList<Integer>(); //number of Nodes from 0 to ith level
        
        int lv=0;  //level 0 is root
        nNode.add(1);
        nNodeSum.add(1);
        int jNode=1;   //current processing number of Node in current level
        int realNodeNum = 0;	//next level node number exclude null
        int strNodeNum = 0;   //next level node number include null 
        
        LinkedList<TreeNode> queue = new LinkedList<TreeNode>();   //maximum capacity reach last nNode[lastLevel]
        queue.add(root);
        
        //build tree
        while(!queue.isEmpty()){
        	
        	TreeNode cur = queue.removeFirst();
        	strNodeNum += 2;  
        	
        	//link leaves to Tree(lv, jNode)
        	int left = nNodeSum.get(lv) + 2*(jNode-1) + 1 - 1;
        	int right = left+1;
        	
        	if(!dest[left].equals("null")){
        		TreeNode tempNode = new TreeNode(str2int(dest[left]));
        		cur.left = tempNode;
            	queue.addLast(cur.left);
            	realNodeNum++;
        	}

        	
        	if(!dest[right].equals("null")){
        		TreeNode tempNode = new TreeNode(str2int(dest[right]));
        		cur.right = tempNode;
            	queue.addLast(cur.right);
            	realNodeNum++;
        	}

        	
        	jNode++;
        	
        	//if current level is full, move to next level
        	if(jNode > nNode.get(lv)){ 
        		lv++;  		 //move to next level
        		jNode = 1;
        		nNode.add(realNodeNum);  //not-null node number in the next level 
        		nNodeSum.add(nNodeSum.get(lv-1)+strNodeNum);  //all node number in the next level
        		realNodeNum = 0;
        		strNodeNum = 0;
        	}
        	
        }
        return root;
    }
    
    public int str2int(String str){
    	return Integer.parseInt(str);
    }
    
//    public void printNodeQueue(LinkedList<TreeNode> queue){
//    	ListIterator e = queue.listIterator();
//    	while(e.hasNext()){
//    		TreeNode tmpNode = (TreeNode) e.next();
//    		System.out.print(tmpNode.val+" ");
//    	}
//    	System.out.print("\n");
//    }
//    
//    public void printIntQueue(LinkedList<Integer> queue){
//    	ListIterator e = queue.listIterator();
//    	while(e.hasNext()){
//    		Integer tmp = (Integer)e.next();
//    		System.out.print(tmp+" ");
//    	}
//    	System.out.print("\n");
//    }
    
    public static void main(String args[]){
    	 //exp.1   	
//    	TreeNode root = new TreeNode(1);
//    	root.left = new TreeNode(2);
//    	root.right = new TreeNode(3);
//    	TreeNode cur = root.right;
//    	cur.left = new TreeNode(4);
//    	cur.right = new TreeNode(5);
    	
    	//exp.2
    	TreeNode root = new TreeNode(1);
    	root.left = new TreeNode(2);
    	TreeNode cur = root.left;
    	cur.left = new TreeNode(3);
    	cur = cur.left;
    	cur.left = new TreeNode(4);
    	    	
    	 Codec codec = new Codec();
    	 String str = codec.serialize(root);
    	 System.out.println(str);
    	 
    	TreeNode root2 = codec.deserialize(str);
   	 	String str2 = codec.serialize(root2);
   	 	System.out.println(str2);
    }
}//end Codec

// Your Codec object will be instantiated and called as such:
// Codec codec = new Codec();
// codec.deserialize(codec.serialize(root));


class TreeNode {
     int val;
     TreeNode left;
     TreeNode right;
     TreeNode(int x) { val = x; }
 }