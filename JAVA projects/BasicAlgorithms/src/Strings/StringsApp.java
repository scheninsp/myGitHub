package Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class StringsApp {

	public static char[] stringShift(char[] x, int m){  
		//move the first m characters to the end of string
		//with O(N)time and O(1)space cost
		//JAVA对基本类型按值传递，因此不能用函数
		
		char temp;
		//flip 1 ~ m
		for (int i=0; i<m/2; i++){  
			temp = x[i];
			x[i] = x[m-1-i];  //CATION: Last Index is Length-1
			x[m-1-i] = temp;
		}
		
		//flip m+1 ~ L
		int L = x.length;
		for (int i=m+1; i<(m+L)/2; i++){  
			temp = x[i];
			x[i] = x[L-1-i+(m+1)];
			x[L-1-i+(m+1)] = temp;
		}
		
		//flip 1 ~ L
		for (int i=0; i<L/2; i++){  
			temp = x[i];
			x[i] = x[L-1-i];
			x[L-1-i] = temp;
		}
		
		return x;
	}
	
	public static boolean charContain(char[] x, char[] y){
		//test whether set x contains all characters in set y
		// x and y can only be any of 26 lower-case alphabet
		// require O(M+N) time cost, O(1) space cost
		
		
		//create Hash Table of x
		int hashCode = 0;  //int has 32 bit
		for (int i=0; i<x.length; i++){
			hashCode |= (1 << (x[i] - 'a'));  //1 << (x[i] - 'a') is the Hash function
		}
		
		for (int i=0; i<y.length; i++){
			if((hashCode & (1 << (y[i] - 'a'))) == 0) 
					return false;
		}
		return true;
	}
	
	public static List<String> charPermutation(String x){
	//recursively generate all permutation combinations of string x	
		String theSubString = null;
		List<String> listAll = new ArrayList<String>();
		if (x.length()>1){
			for(int i=0;i<x.length();i++){
				theSubString  = x.substring(0,i).concat(x.substring(i+1,x.length()));
				List<String> tempList = charPermutation(theSubString);
				ListIterator<String> iter = tempList.listIterator();
				
				int cnt=0;
				while(iter.hasNext()){
					tempList.set(cnt,iter.next().concat(x.substring(i,i+1)));
					cnt++;
				}
				listAll.addAll(tempList);
			}
		}
		else{ //x has only one character
			listAll.add(x);
		}
		return listAll;
	}
	
	
	public static void main(String args[]){

//		// test stringShift
//		String temp = "abcdefg";
//		char[] x = temp.toCharArray(); //String class is immutable in JAVA
//		int nMove = 3;
//		
//		char[] y = stringShift(x,nMove);
//		System.out.println(y);
		
//		//test charContain
//		String temp1 = "abcdefg";
//		char[] x = temp1.toCharArray(); 
//		String temp2 = "abc";
//		char[] y = temp2.toCharArray();
//		String temp3 = "abck";
//		char[] z = temp3.toCharArray();
//		
//		String yes = "yes";
//		String no = "no";
//		List<String> answers = new ArrayList<String>(); 
//		answers.add(yes);
//		answers.add(no);
//		
//		int tempIndx = charContain(x,y) ? 0:1;  //True -> print 1st string in list
//		System.out.print( temp1 + " contains " + temp2 + " : " 
//						+ (String)answers.get(tempIndx) );
//		System.out.print("\n");
//		
//		tempIndx = charContain(x,z) ? 0:1;  //True -> print 1st string in list
//		System.out.print( temp1 + " contains " + temp2 + " : " 
//						+ (String)answers.get(tempIndx) );
//		System.out.print("\n");
		
		//Generate all permutations of a character sequence
		String temp1 = "abc";
		List<String> listOfPermutation = charPermutation(temp1);
		System.out.println(listOfPermutation);
		
	}//end main
}
