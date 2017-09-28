package basic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class XTriangle {
	//UNFINISHED
//	�۽������θ���
//	ʱ�����ƣ�C/C++���� 3000MS���������� 5000MS
//	�ڴ����ƣ�C/C++���� 30720KB���������� 555008KB
//	��Ŀ������
//	����Բ���ϵ�n���㣨n>=1�����ԽǶ�a��ʾ��λ��(0<=a<360)�����밴a��С��������������ĵ���Թ��ɵĶ۽������θ�����
//	����
//	��һ��Ϊ�����n�����n�У�ÿ��һ��˫���ȸ���������ʾ��ĽǶȣ�С�������8λ������������������Ϊ4��������룺
//	���
//	������Թ��ɵĶ۽������θ�����'\n'���з���
//
//	��������
//	4
//	0.00000000
//	56.00000000
//	179.00000000
//	180.00000000
//	�������
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
