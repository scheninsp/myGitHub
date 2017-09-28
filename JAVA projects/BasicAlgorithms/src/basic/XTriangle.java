package basic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class XTriangle {
	//UNFINISHED
//	钝角三角形个数
//	时间限制：C/C++语言 3000MS；其他语言 5000MS
//	内存限制：C/C++语言 30720KB；其他语言 555008KB
//	题目描述：
//	输入圆周上的n个点（n>=1），以角度a表示其位置(0<=a<360)，输入按a从小到大排序。求输入的点可以构成的钝角三角形个数。
//	输入
//	第一行为点个数n，后跟n行，每行一个双精度浮点数，表示点的角度（小数点后保留8位），例如输入样例中为4个点的输入：
//	输出
//	输出可以构成的钝角三角形个数和'\n'换行符。
//
//	样例输入
//	4
//	0.00000000
//	56.00000000
//	179.00000000
//	180.00000000
//	样例输出
//	2
	
	public static int binarySearchLessMax(double[] angles, int down, int up, double target){
		//find the maximum value less than target in maxChar
		int j=down,k=up;
		while(k>j){
			int p = (k+j)/2;
			double temp = angles[p];
				
			if(temp<target){
				j=p+1;
			}
			else{
				k=p-1;
			}
		}
		while(angles[k] >= target){
			k--;
		}
		return k;
	}
	
	
	public static int numXTriangle(int np, double[] angles){
		//angles[i]
		//find all angles[j] < 180+angles[i]
		//find all angles[k] between angles[i], angles[j]
		//angles[i],angles[j] and angles[k] can form a XTriangle
		
		//UNFINISHED
		//find the angles[p] > angles[i]+180
		//find 360 > angles[q]> angles[p], and angles[i]> angles[q] >0
		//angles[i], angles[p] and angles[q] can form a XTriangle
		
		int num = 0;
		for(int i=0;i<np;i++){
			double theUpper = angles[i]+180;
			if(theUpper>360) {theUpper = 360;}
			
			int j = binarySearchLessMax(angles, i+1, angles.length-1, theUpper);
			
			//			System.out.println(angles[j]);	
			if(j-i>1){
				num += (j-i-1);
			}
		}
		return num;
	}
	
	
	public static void main(String args[]) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int np = Integer.parseInt(br.readLine());
		double[] angles = new double[np];
		for(int i=0; i<np; i++){
			angles[i] = Double.parseDouble(br.readLine());
		}
		
//		System.out.print(np);
//		for(int i=0;i<np; i++){
//			System.out.print(" " + angles[i]);
//		}
		
		int num = numXTriangle(np,angles);
		System.out.println(num);
	}
}
