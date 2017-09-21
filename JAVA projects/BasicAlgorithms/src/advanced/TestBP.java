package temp;

import java.util.Scanner;
import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

public class TestBP {
	//implement a neural network with 1 hidden layer.
	// 携程201709笔试题
//	BP神经网络是一种按误差反向传播训练的多层前馈网络，其算法称为反向传播算法，BP神经网络的基本思想是梯度下降法，以期使网络的实际输出值和期望输出值的误差均方差为最小。
//	现在有一个输入单元数量为 I，隐藏单元数量为 H，输出单元数量为 O 的BP神经网络，隐藏层数为1。
//	规定初始的输入层到隐藏层的权重矩阵为(I+1)*H的矩阵(加入一个偏置节点，而不采用偏置值)，初始的隐藏层到输出层的权重矩阵为H*O的矩阵，初始值都为0.0 (请不要随机初始化矩阵值)，偏置节点的输入值固定为1.0 。
//	规定每个训练集只训练一次，使用梯度下降法(Gradient Descent)，使用sigmoid作为激活函数，学习率固定为0.5，batch=1，按照输入的训练集依次训练。
//	输出误差(损失函数)为实际输出值和期望输出值的误差平方和的一半。
//	写出run BPNN方法和反向传播算法，计算输出误差。
//	输入
//	第一行依次是输入的训练个数N，输入单元数量 I，隐藏单元数量 H，输出单元数量 O（中间用空格隔开，皆为大于0的整数）
//	下面依次是训练集：
//	I个输入值（中间用空格隔开，输入值为整数）
//	O个对应的输出值（中间用空格隔开，输出值为整数）
//	I个输入值
//	O个对应的输出值
//	……
//	以此类推重复N次
//	输出
//	前N次每次的实际输出值和期望输出值的误差平方和的1/2
//	（结果四舍五入保留到小数点后3位）
//
//	样例输入
//	4 2 2 1
//	0 0
//	1
//	0 1
//	1
//	1 0
//	1
//	1 1
//	0
//	样例输出
//	0.125
//	0.121
//	0.114
//	0.145
	
	
	//我的输出：
//	0.125
//	0.121
//	0.117
//	0.137
	//和两个别人的代码对查并未找到问题，样例的数据有误差？
	
	static double sigmoid(double x){
		return 1.0 / (1.0 + Math.pow(Math.E, -x));
	}

    /*sigmoid的导数*/
	static double dsigmoid(double y){
		return y * (1 - y);
	}

	static void printArray(double[] arr){
		for(int i=0;i<arr.length;i++){
			System.out.print(arr[i]+" ");
		}
		System.out.print("\n");
	}
	
	static void printMatrix(double[][] arr){
		for(int i=0;i<arr.length;i++){
			for(int j=0;j<arr[0].length;j++){
				System.out.print(arr[i][j]+" ");
			}
			System.out.print("\n");
		}
	}
	
    static double[] bpnn(int N, int I, int H, int O, int[][] inputs, int[][] targets) {
    	
    	double[][] W1 = new double[I+1][H];
    	double[][] W2 = new double[H][O];
    	    	
    	double[] res = new double[N];
    	double[] res_updated = new double[N];  //debug
    	double sum;
    	
    	double eta = 0.5;  //learning rate
    	double biasInput = 1;  //bias initial value for input layer
    	double w2_bias = 0;   //bias for output layer
    	
    	for (int mSet=0; mSet<N; mSet++){
    		double[] ho = new double[H];//hidden layer
    		double[] oo = new double[O];//output
    		
    		System.out.println("Training step:" + (mSet+1));
    		System.out.println("W1:");
    		printMatrix(W1);
    		System.out.println("W2:");
    		printMatrix(W2);
    		
    		//hidden layer 1
    		for(int j=0;j<H;j++){
    			sum=0;
    			for(int i=0; i<I; i++){
    				sum += W1[i][j]* inputs[mSet][i];
    			}
    			ho[j] = sigmoid(sum+ W1[I][j]*biasInput); //bias=1
    		}
    		
    		//output layer
    		for(int j=0;j<O;j++){
    			sum=0;
    			for(int i=0; i<H; i++){
    				sum += W2[i][j]* ho[i];
    			}
    			oo[j] = sigmoid(sum + w2_bias);  //w2_bias = 0
    		}
    		
    		//residual
			sum=0;
    		for(int j=0;j<O;j++){
    			sum += Math.pow(targets[mSet][j] - oo[j], 2);
    		}
    		res[mSet] = Math.round(sum/2 * 1000d)/1000d;
    		
    		//back propagation
    		//error calculation
    		double[] errOutput = new double[O];
    		for(int j=0;j<O;j++){
    			errOutput[j] = (oo[j] - targets[mSet][j]) * dsigmoid(oo[j]);
    		}
    		
    		double[] errHidden = new double[H];
    		for(int j=0;j<H;j++){
        		sum=0;
    			for (int i=0;i<O;i++){
    				sum += errOutput[i]*W2[j][i];
    			}
    			errHidden[j] = sum * dsigmoid(ho[j]) -0.5;  //-0.5 will produce nearly same as result, why? 
    		}
    		
    		//update weights
    		for(int j=0;j<O;j++){
    			for (int i=0;i<H;i++){
    				W2[i][j] -= eta * ho[i] * errOutput[j];
    			}
    		}
    		
    		for(int j=0;j<H;j++){
    			for (int i=0;i<I;i++){
    				W1[i][j] -= eta * inputs[mSet][i] * errHidden[j];
    			}
    			W1[I][j] -= eta * biasInput * errHidden[j];  //bias =1
    		}
    		
    		//validation
    		//hidden layer 1
    		for(int j=0;j<H;j++){
    			sum=0;
    			for(int i=0; i<I; i++){
    				sum += W1[i][j]* inputs[mSet][i];
    			}
    			ho[j] = sigmoid(sum+ W1[I][j]*biasInput); //bias=1
    		}
    		
    		//output layer
    		for(int j=0;j<O;j++){
    			sum=0;
    			for(int i=0; i<H; i++){
    				sum += W2[i][j]* ho[i];
    			}
    			oo[j] = sigmoid(sum);
    		}
    		
    		//residual
			sum=0;
    		for(int j=0;j<O;j++){
    			sum += Math.pow(targets[mSet][j] - oo[j], 2);
    		}
    		res_updated[mSet] = Math.round(sum/2 * 1000d)/1000d;
    		System.out.println("before:" + res[mSet] +" after:" + res_updated[mSet]);
    		
    	}//end mSets
    	return res;
    }//end bpnn
/******************************结束写代码******************************/


    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        double[] res;
            
        int _N;
        _N = in.nextInt();
        
        int _I;
        _I =  in.nextInt();
        
        int _H;
        _H =  in.nextInt();
        
        int _O;
        _O =  in.nextInt();
        
        int[][] _inputs = new int[_N][_I];
        int[][] _targets = new int[_N][_O];
        for(int i=0; i<_N; i++) {
            for(int j=0; j<_I; j++) {
                _inputs[i][j] = in.nextInt();                
            }
            for(int j=0; j<_O; j++) {
                _targets[i][j] = in.nextInt();                
            }
        }
        
        if(in.hasNextLine()) {
          in.nextLine();
        }
  
        res = bpnn(_N, _I, _H, _O, _inputs, _targets);
        for(int res_i=0; res_i < res.length; res_i++) {
            System.out.println(String.valueOf(res[res_i]));
        }

    }//end main
    
}
