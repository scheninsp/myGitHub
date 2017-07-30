//This script generate random String buffer and sort
package c08;
import java.util.*;

public class Array1 {
	static Random r = new Random();
	static String ssource = 
			"ABCDEFGHIJKLMNOPQRSTUVWXYZ"+
			"abcdefghijklmnopqrstuvwxyz";
	static char[] src = ssource.toCharArray();
	public static String randString(int length){
		char[] buf = new char[length];
		int rnd;
		for (int i=0;i<length;i++){
			rnd = Math.abs(r.nextInt()) % src.length;
			buf[i] = src[rnd];
		}
		return new String(buf);
	}
	
	public static String[] randStrings(int length, int size){
		String[] s = new String[size];
		for (int i=0;i<size;i++){
			s[i] = randString(length);
		}
		return s;
	}
	
	public static void printString(String[] s){
		for (int i=0;i<s.length;i++){
			System.out.print(s[i]+" ");
		}
		System.out.println();
	}
	
	public static void main(String[] args){
		int length = 4;
		int size = 6;
		String[] s = randStrings(length,size);
		printString(s);
		Arrays.sort(s);
		printString(s);
		int loc = Arrays.binarySearch(s, s[4]);
		System.out.println(loc);
	}
	
	
}

