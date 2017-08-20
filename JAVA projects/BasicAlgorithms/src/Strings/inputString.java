package Strings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class inputString {
	
	private List<String> stringBuffer;
	
	public inputString(){
		stringBuffer = new ArrayList<String>();
	}
	
	public void in() {
		//save input strings from command
		System.out.println("Please enter strings : ");
		BufferedReader br = new BufferedReader(
				new InputStreamReader(System.in));
		while(true){
			try {
				String str = br.readLine();
				if (str.equals("end")) { return; }
				else {stringBuffer.add(str);}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void out() {
		//print all saved strings
		ListIterator<String> iter = stringBuffer.listIterator();
		while(iter.hasNext()){
			System.out.println(iter.next());
		}
	}
	
	
	
	public List<String> getStringBuffer() {
		return stringBuffer;
	}

	public static void main(String args[]){
		inputString myStrings = new inputString();
		myStrings.in();
		myStrings.out();
	}
}
