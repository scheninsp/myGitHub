package Graph;

public class Vertex implements Cloneable{
	public char label;
	
	public Vertex(char lab){
		label = lab;
	}
	
	@Override   
	public Object clone(){  // Cloneable
		Vertex v = null;
		try{
			v = (Vertex)super.clone();
		}catch(CloneNotSupportedException e){
			e.printStackTrace();
		}
		return v;
	}
}
