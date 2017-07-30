package c08;
import java.io.*;

public class IOStreamDemo {
	public static void main(String[] args){
		try{
			//1.Buffered input file (in newest version)
			BufferedReader in =
					new BufferedReader(
							new InputStreamReader(
									new FileInputStream(args[0])));
			String s,s2 = new String();
			while((s=in.readLine())!=null){
				s2 += (s + "\r\n");  //for windows output
			}
			in.close();
			
			System.out.printf("%s\n", s2);
			
			//Output to file
			FileOutputStream out = 
						new FileOutputStream("tmpfile.txt");
			byte[] buff=s2.getBytes();
			out.write(buff,0,buff.length);
			out.close();
		}
		

		catch (FileNotFoundException e1){
			System.out.println(args[0]+"file not found");
		}
		catch (IOException e2){
			System.out.println(e2.toString());
		}
		
		finally{
			System.out.println("IODemo completed.");
		}
	}
	
}
