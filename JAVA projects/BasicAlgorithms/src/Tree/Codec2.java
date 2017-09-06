package Tree;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

public class Codec2 {
	//Build series from tree by depth first pre-order traversal
	//adapted from https://leetcode.com/problems/serialize-and-deserialize-binary-tree/discuss/
	
    private static final String spliter = ",";
    private static final String NN = "X";

    // Encodes a tree to a single string.
    public String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        buildString(root, sb);
        return sb.toString();
    }

    private void buildString(TreeNode node, StringBuilder sb) {
        if (node == null) {
            sb.append(NN).append(spliter);
        } else {
            sb.append(node.val).append(spliter);
            buildString(node.left, sb);
            buildString(node.right,sb);
        }
    }
    // Decodes your encoded data to tree.
    public TreeNode deserialize(String data) {
        Deque<String> nodes = new LinkedList<>();
        nodes.addAll(Arrays.asList(data.split(spliter)));
        return buildTree(nodes);
    }
    
    private TreeNode buildTree(Deque<String> nodes) {
        String val = nodes.remove();
        if (val.equals(NN)) return null;
        else {
            TreeNode node = new TreeNode(Integer.valueOf(val));
            node.left = buildTree(nodes);
            node.right = buildTree(nodes);
            return node;
        }
    }
    
    
    public static void main(String args[]){
   	 //exp.1   	
//   	TreeNode root = new TreeNode(1);
//   	root.left = new TreeNode(2);
//   	root.right = new TreeNode(3);
//   	TreeNode cur = root.right;
//   	cur.left = new TreeNode(4);
//   	cur.right = new TreeNode(5);
   	
   	//exp.2
   	TreeNode root = new TreeNode(1);
   	root.left = new TreeNode(2);
   	TreeNode cur = root.left;
   	cur.left = new TreeNode(3);
   	cur = cur.left;
   	cur.left = new TreeNode(4);
   	    	
   	 Codec2 codec = new Codec2();
   	 String str = codec.serialize(root);
   	 System.out.println(str);
   	 
   	TreeNode root2 = codec.deserialize(str);
  	 	String str2 = codec.serialize(root2);
  	 	System.out.println(str2);
   }
}