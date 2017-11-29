package basic;

import java.util.Scanner;

public class ValidNumber {
	//Judge if a given string is numeric
	
//test case:
//	3.
//	3. 
//	.1
//	1 
//	 1
//	46.e3
//	  2.8
//
//	1 4
//	- e
//	a1
//	1a
//	1.a
//	1.2a
//	1.2e 1
// cases before '' are true, after '' are false 
	
	public static void main(String args[]){
		Scanner sc = new Scanner(System.in);
		int tCase = 0;
		while(tCase<100){
		String str = sc.nextLine();
		System.out.println(isNumber(str));
		}
		sc.close();
	}
	
	public static boolean isNumber(String s){
		int p=0;
		int len = s.length();
		boolean flagFirstNum = false;
		boolean flagFirstPoint = false;
		boolean flagHasBreak = false;
		
		if(len == 0) {return false;}
		
		while(s.charAt(p) == ' ') {p++;  if(p==len){return false;} }  //' 0','   0'
		
		if (isPlusMinus(s.charAt(p))) {p++; if(p==len){return false;} }
		
		if( isNum(s.charAt(p)) ) {
			flagFirstNum=true;  //the string starts with [0-9]
			p++;
			if(p==len){return true;}
			
			while(isNum(s.charAt(p))) {p++; if(p==len){return true;}}
		}
		
		if( s.charAt(p) == '.') {
			p++;
			if(flagFirstNum == false && p == len) {return false;} //'.'
			else if(flagFirstNum == true && p==len){return true;} //'9.'
			else{
				if(isNum(s.charAt(p))){
					flagFirstPoint=true;  //the string starts with .[0-9]
					p++;
					if(p==len){return true;}

					while(isNum(s.charAt(p))) {p++; if(p==len){return true;}}
				}
				else if(!(flagFirstNum && (s.charAt(p)==' ' || s.charAt(p)=='e'))) {return false;}  //'.X','2.X','. ',  caution : '2. ','2.e1' is true
			}
		}
		
		if(flagFirstNum || flagFirstPoint) {
			if(s.charAt(p) == 'e' || s.charAt(p) == 'E'){
				p++;
				if(p==len){return false;}
				
				if (isPlusMinus(s.charAt(p))) {p++; if(p==len){return false;} }

				if( isNum(s.charAt(p)) ) {
					p++;
					if(p==len){return true;}

					while(isNum(s.charAt(p))) {p++; if(p==len){return true;}}
				}
				else{return false;}  //'2e ', '2eX' 

			}
		}	
		
		if (!flagFirstNum && !flagFirstPoint) {return false;}
		if(((s.charAt(p)!= ' ') && !isNum(s.charAt(p))) ) {return false;} //'5.1e24X'
				
		while(s.charAt(p) == ' ') {
			flagHasBreak =true; 
			p++; if(p==len){return true;} }  //'1 '
		
		if(flagHasBreak && s.charAt(p)!=' ') {return false;}  //'1 4'

		return true;
	}
	
	public static boolean isPlusMinus(char x){
		return (x == '-' || x == '+');
	}
	
	public static boolean isNum(char x){
		return ('0'<=x && x<='9');
	}
	
		
}
