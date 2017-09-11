package basic;

import java.util.Scanner;

public class LongestIncreasingSeq {
	private static final int MAX_STR_LEN = 100;
	private static final char MIN_CHAR = '0';
	public static char[] maxChar = new char[MAX_STR_LEN];   //ending char of current maximum increasing sequence
	public static int[] maxLIS = new int[MAX_STR_LEN];  //maximum length of increasing sequence
	public static int[] pre = new int[MAX_STR_LEN];
	
	public static int binarySearch(int[] arr, int up, int val){   
		//binary search in an ordered sequence
		int pfront = 0;
		int pend = up;
		
		while (pfront<=pend){
			int pmid = (pfront+pend)/2;
			if (arr[pmid] == val){
				return pmid;
			}
			else if (arr[pmid] < val){
				pfront = pmid+1;
			}
			else {
				pend = pmid-1;
			}
		}
		return -1;
	}
	
	public static int binarySearchLessMax(char[] maxChar, int up, char target){
		//find the maximum value less than target in maxChar
		int j=0,k=up;
		while(k>j){
			int p = (k+j)/2;
			char tempChar = maxChar[p];
				
			if(tempChar<target){
				j=p+1;
			}
			else{
				k=p-1;
			}
		}
		while(maxChar[k] >= target){
			k--;
		}
		return k;
	}
	
	public static int longestIncSeq(char[] str){
		maxChar[0] = MIN_CHAR;
		maxLIS[1] = 0;
		maxChar[1] = str[0];
		maxLIS[1] = 1;
		pre[0] = 0;
		pre[1] = 0;
		
		for (int i=2; i<str.length+1; i++){
			//System.out.print(i+ ":" + str[i-1]);  //debug
			int pos = binarySearchLessMax(maxChar,i-1,str[i-1]);
			if (maxLIS[pos]+1 > maxLIS[i-1]){  //update LIS
				maxChar[i] = str[i-1];
				maxLIS[i] = maxLIS[pos] + 1;				
				pre[i] = pos;
			}
			else{  //no update in LIS
				maxChar[i] = maxChar[i-1];
				maxLIS[i] = maxLIS[i-1];				
				pre[i] = pre[i-1];
			}
			//System.out.print(" current ending: " + maxChar[i] + "\n"); //debug

		}
		
		int max=maxLIS[0];
		for (int i=0; i<str.length+1;i++){
			if(max < maxLIS[i])
				max = maxLIS[i];
		}
		
		return max;
	}
	
	public static void main(String args[]){
		
		Scanner sc = new Scanner(System.in);
		String tempstr = sc.nextLine();
		tempstr = tempstr.toLowerCase();
		char[] str = tempstr.toCharArray();
		
		//test binartSearchLessMax()
//		char target = 'a'; 
//		int up = str.length-1;
//		int found = binarySearchLessMax(str, up, target);
//		System.out.println(found + ":" + str[found]);
				
		int len = longestIncSeq(str);
		System.out.println(len);
		
		int pos = binarySearch(maxLIS,str.length,len);
		StringBuffer strbuff = new StringBuffer();
		while(pos!=0){
			strbuff.append(maxChar[pos]);
			pos = pre[pos];
		}
		for(int i=strbuff.length()-1;i>=0;i--){
			System.out.print(strbuff.charAt(i));
		}
		
	}
}
