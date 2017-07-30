/**This package is for chapter1 excercises*/
package chapter1;

import java.util.*;
import java.io.*;
import userlib20170104.Usercall_package2;

/**This Class is printing Hello World*/ 
public class Helloworld2 {

	public static void main(String[] args)
	{
		System.out.println("Hello World");
		
		// call an external package
		Usercall_package2 outlier= new Usercall_package2();
		int marker;
		marker = outlier.usercall_func();
		String outhint = "The outlier was set to ";
		System.out.println(outhint + String.valueOf(marker));
		
		//input 3 strings and output them
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
