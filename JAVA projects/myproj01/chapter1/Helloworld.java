/**This package is for chapter1 excercises*/
package chapter1;

import java.util.*;
import java.io.*;

/**This Class is printing Hello World*/ 
public class Helloworld {

	public static void main(String[] args)
	{
		System.out.println("Hello World");
		
		String line = "";  
		List<String> lines = new ArrayList<String>();  
		for (int i=0;i<3;i++) {  
			BufferedReader br = new BufferedReader
					(new InputStreamReader(System.in));
			try {
			line = br.readLine();
			} catch(IOException e) {e.printStackTrace();}
			lines.add(line);  
		}  
		for (int i=0;i<3;i++) {
		System.out.println(lines.get(i)); 
		}
	} 
	
}
