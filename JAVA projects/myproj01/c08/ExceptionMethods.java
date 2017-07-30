package c08;

public class ExceptionMethods {

	public static void main(String[] args){
		try{
			throw new Exception("Here is my exception");
		}
		catch(Exception e){
			System.out.println("Caught exception");
			System.out.println("e.getMessage(): "+e.getMessage());
			System.out.println("e.toString():"+e.toString());
			System.out.print("e.printStackTrace:");
			e.printStackTrace();
		}
	}
	
}
